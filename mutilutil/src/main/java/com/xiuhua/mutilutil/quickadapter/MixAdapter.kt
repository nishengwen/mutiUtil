package com.xiuhua.mutilutil.quickadapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * 多模板列表
 *
 * 列表顺序由数据决定
 *
 * 最广泛的列表模型
 *
 */
private const val TAG = "MixAdapter"
class MixAdapter private constructor(registerBinderAction: MixAdapter.() -> Unit, mContext: Context, private val mLifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    constructor (activity: ComponentActivity, registerBinderAction: MixAdapter.() -> Unit) : this(registerBinderAction, activity, activity)

    constructor (fragment: Fragment, registerBinderAction: MixAdapter.() -> Unit) : this(registerBinderAction, fragment.requireContext(), fragment.viewLifecycleOwner)

    private val binderManager = BinderManager()
    private val dataKey = hashMapOf<Any, Int>()

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    init {
        registerBinderAction.invoke(this)
    }

    /**
     * 添加模板
     */
    fun registerBinder(dataViewBinder: DataViewBinder<out ViewDataBinding, out Any>) {
        //直接强转反正运行都是DataViewBinder 泛型是擦除的
        binderManager.addBinder(dataViewBinder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val dataBinder = binderManager.getBinder(viewType)
        val viewDataBinding = dataBinder.getSDataBinding(inflater, parent)
        viewDataBinding.lifecycleOwner = mLifecycleOwner
        dataBinder.onCreateViewHolder(viewDataBinding)
        Log.d(TAG, "onCreateViewHolder: ${viewType}")
        return DataBindingViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as DataBindingViewHolder
        val dataBinder = binderManager.getBinder(getItemViewType(position))
        dataBinder.onBindViewHolder(holder.dataBinding, getItemData(position))
        holder.dataBinding.executePendingBindings()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any?>) {
        onBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int) = dataKey.values.elementAt(position)
    override fun getItemCount() = dataKey.keys.size
    fun getItemData(position: Int) = dataKey.keys.elementAt(position)

    private class DataBindingViewHolder(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)

    /**
     * 刷新或者添加指定类型条目，(局部刷新还是使用LiveData)
     */
    fun appendItem(itemData: Any) {
        val viewType = binderManager.getItemViewType(itemData)
        dataKey[itemData] = viewType
        notifyItemInserted(dataKey.size - 1)
    }

    fun setList(list: MutableList<out Any>) {
        dataKey.clear()
        for (data in list) {
            appendItem(data)
        }
        notifyDataSetChanged()
    }

}



