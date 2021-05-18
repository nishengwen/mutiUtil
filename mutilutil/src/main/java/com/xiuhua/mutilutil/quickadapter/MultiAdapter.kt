package com.xiuhua.mutilutil.quickadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 *
 * 模板和数据一对一
 *
 * 多种不同模板数据，适用大型不重复长页面（自带排序），单文件可运行
 *
 * ViewDataBinding 可以重复
 * Data 不可以重复
 * DataViewBinder 不可以重复
 */
class MultiAdapter private constructor(registerBinderAction: MultiAdapter.() -> Unit, mContext: Context, private val mLifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    constructor (activity: ComponentActivity, registerBinderAction: MultiAdapter.() -> Unit) : this(registerBinderAction, activity, activity)

    constructor (fragment: Fragment, registerBinderAction: MultiAdapter.() -> Unit) : this(registerBinderAction, fragment.requireContext(), fragment.viewLifecycleOwner)

    //根据数据的类型查询binders中的viewType 添加到sortedMap自动排序
    val sortedMap: SortedMap<Int, Any> = sortedMapOf()

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    init {
        registerBinderAction.invoke(this)
    }

    val binderManager = BinderManager()

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

    override fun getItemViewType(position: Int) = sortedMap.keys.elementAt(position)
    fun getPosition(viewType: Int) = sortedMap.keys.indexOf(viewType)

    override fun getItemCount() = sortedMap.values.size
    fun getItemData(position: Int) = sortedMap.values.elementAt(position)


    private class DataBindingViewHolder(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)

    /**
     * 刷新或者添加指定类型条目，(局部刷新还是使用LiveData)
     */
    fun refreshOrAddItem(itemData: Any) {
        val viewType = binderManager.getItemViewType(itemData)
        val oldItemBean = sortedMap[viewType]
        //刷新或添加数据
        sortedMap[viewType] = itemData
        //刷新或添加UI
        if (oldItemBean == null)
            notifyItemInserted(getPosition(viewType))
        else
            notifyItemChanged(getPosition(viewType), "refreshTag")
    }

    /**
     * 删除指定类型条目
     */
    inline fun <reified T> removeItem() {
        val viewType = binderManager.getItemViewType<T>()
        if (sortedMap.keys.contains(viewType)) {
            val position = sortedMap.keys.indexOf(viewType)
            sortedMap.remove(viewType)
            notifyItemRemoved(position)
        }
    }

}



