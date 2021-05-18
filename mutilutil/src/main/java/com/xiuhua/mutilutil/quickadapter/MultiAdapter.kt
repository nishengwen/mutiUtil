package com.xiuhua.mutilutil.quickadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xiuhua.mutilutil.core.OutTAction
import com.xiuhua.mutilutil.time.tick
import java.util.*

/**
 * 用于不重复类型的数据，大型列表，自带排序
 */
class MultiAdapter private constructor(outTaction: OutTAction<MultiAdapter>, mContext: Context, private val mLifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    constructor (activity: ComponentActivity, outTAction: OutTAction<MultiAdapter>) : this(outTAction, activity, activity)

    constructor (fragment: Fragment, outTAction: OutTAction<MultiAdapter>) : this(outTAction, fragment.requireContext(), fragment.viewLifecycleOwner)

    val binders = sortedMapOf<Int, DataViewBinder<in ViewDataBinding, in Any>>()

    val sortedMap: SortedMap<Int, Any> = sortedMapOf()

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    init {
        outTaction.invoke(this)
    }

    /**
     * 添加itemView
     *
     * @param delegate itemView
     * @throws IllegalArgumentException 已存在不能再添加
     */
    fun registerBinder(dataViewBinder: DataViewBinder<out ViewDataBinding, out Any>) {
        //直接强转反正运行都是Any
        binders[binders.size] = dataViewBinder as DataViewBinder<ViewDataBinding, Any>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val dataBinder = binders[viewType]!!
        val viewDataBinding: ViewDataBinding = dataBinder.getSViewDataBinding(inflater, parent)
        viewDataBinding.lifecycleOwner = mLifecycleOwner
        dataBinder.onCreateViewHolder(viewDataBinding)
        return DataBindingViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        binders[getItemViewType(position)]?.onBindViewHolder((holder as DataBindingViewHolder).dataBinding, getItemData(position))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any?>) {
        onBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int) = sortedMap.keys.elementAt(position)
    fun getPosition(viewType: Int) = sortedMap.keys.indexOf(viewType)

    override fun getItemCount() = sortedMap.values.size
    fun getItemData(position: Int) = sortedMap.values.elementAt(position)

    //用于新数据添加获取类型
    fun getItemViewType(data: Any): Int {
        for ((key, value) in binders) {
            if (data::class.java == value.getTType()) {
                return key
            }
        }
        throw IllegalArgumentException("没找到有处理这个数据的binder${data::class.java}")
    }

    //用于数据删除
    inline fun <reified T> getItemViewType(): Int {
        for ((key, value) in binders) {
            if (T::class.java == value.getTType()) {
                return key
            }
        }
        return -1
    }

    private class DataBindingViewHolder(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)

    //--------------------------------下面的方法应用于 binder和holder一一绑定的情况
    //刷新或者添加指定的数据
    fun refreshOrAddItem(itemData: Any) {
        val viewType = getItemViewType(itemData)
        val oldItemBean = sortedMap[viewType]
        //刷新或添加数据
        sortedMap[viewType] = itemData
        //刷新或添加UI
        if (oldItemBean == null)
            notifyItemInserted(getPosition(viewType))
        else
            notifyItemChanged(getPosition(viewType), "partialRefreshTag")
    }

    //删除指定类型数据
    inline fun <reified T> removeItem() {
        val viewType = getItemViewType<T>()
        if (viewType != -1 && sortedMap.keys.contains(viewType)) {
            val position = sortedMap.keys.indexOf(viewType)
            sortedMap.remove(viewType)
            notifyItemRemoved(position)
        }
    }

    companion object {
        fun test(recyclerView: RecyclerView, fragment: Fragment) {
            val mAdapter = MultiAdapter(fragment) {
                registerBinder(SimpleBinder())
            }

            recyclerView.apply {
                layoutManager = LinearLayoutManager(recyclerView.context)
                adapter = mAdapter
            }

            val notifyText = NotifyText("11111111")
            mAdapter.refreshOrAddItem(notifyText)
            (8 tick 1000).apply {
                onTick {
                    mAdapter.refreshOrAddItem(NotifyText("11111111${it}"))
                }
                onFinish {
                    mAdapter.removeItem<NotifyText>()
                }
                start()
            }
        }
    }
}