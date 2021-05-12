package com.nsw.xiuhua.library.view.recyclerview

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * itemView适配器，有多少类型就要添加多少个Delegate，并且不能添加相同的Delegate，不然会抛异常
 */
abstract class KBaseAdapter<T : Any>(val mContext: Context, mDatas: List<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**
     * itemView管理器
     */
    val binderManager: BinderManager<T> = BinderManager()
    val sortedMap: SortedMap<Int, ItemBean<T>> = sortedMapOf()

    init {
        //准备模板
        this.registerDataViewBinders()
        //准备数据
        mDatas.forEach {
            insertData(ItemBean(it))
        }
    }

    private fun insertData(itemBean: ItemBean<T>): Int {
        val viewType = binderManager.getItemViewType(itemBean)
        sortedMap[viewType] = itemBean
        //返回这个viewType所在的位置
        return sortedMap.keys.indexOf(viewType)
    }

    private val TAG = "KBaseAdapter"

    /**
     * 获取itemView类型
     *
     * @param position 当前position
     */
    override fun getItemViewType(position: Int) : Int {
        val viewType= sortedMap.keys.elementAt(position)
        if(viewType!=null)
            return viewType-(viewType%10)
        else
            throw IllegalArgumentException("No viewType  that matches in sortedMap = $viewType source");
    }

    fun getItemData(position: Int) = sortedMap.values.elementAtOrNull(position)
    override fun getItemCount(): Int = sortedMap.values.size
    fun getPosition(viewType: Int)= sortedMap.keys.indexOf(viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any?>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            binderManager.bindData(mContext, holder,getItemData(position)!!, position)
        }
    }

    /**
     * 初始化数据模板  置入所有的数据模板 ，不管其动态显示不显示
     * viewType 是模板的添加顺序 0~size-1
     */
    abstract fun registerDataViewBinders()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            //寻找模板绑定数据到ViewHolder里
            binderManager.bindData(mContext, holder,getItemData(position)!!, position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            //创建viewHolder
            binderManager.createViewHolder(mContext, parent, viewType)

    /**
     * 添加itemView
     *
     * @param delegate itemView
     * @throws IllegalArgumentException 已存在不能再添加
     */
    fun registerBinder(delegate: DataViewBinder<*>) {
        binderManager.registerDataViewBinder(delegate)
    }

    //--------------------------------下面的方法应用于 binder和holder一一绑定的情况
    //刷新或者添加指定的数据
   inline fun <reified S> refreshOrAddItem(itemData: T) {
        val newItemBean= ItemBean(itemData);
        val viewType=  binderManager.getBinderType<S>()
        val oldItemBean= sortedMap[viewType]
        //刷新或添加数据
        sortedMap[viewType] = newItemBean
        //刷新或添加UI
        if(oldItemBean==null)
            notifyItemInserted(getPosition(viewType))
        else
            notifyItemChanged(getPosition(viewType), "partialRefreshTag")
    }

    inline fun <reified S> refreshList(itemData: List<T>) {
        val type = binderManager.getBinderType<S>()
        //大小不能超过10
        for (index in itemData.indices) {
            if (sortedMap.keys.contains(type + index)) {
                sortedMap.remove(type + index)
                notifyItemRemoved(type + index)
            }
            sortedMap[type + index] = ItemBean(itemData[index])
            notifyItemInserted(type + index)
        }
    }

    inline fun <reified S> refreshItemData(itemData: T?) {
        //这块没想好
        if (itemData == null || (itemData is List<*> && itemData.size == 0)) {
            removeBinderData<S>()
        } else {
            refreshOrAddItem<S>(itemData)
        }
    }

    //移除指定binder 的数据
    inline fun <reified S> removeBinderData() {
        val type = binderManager.getBinderType<S>()
        if (sortedMap.keys.contains(type)) {
            val position = sortedMap.keys.indexOf(type)
            sortedMap.remove(type)
            notifyItemRemoved(position)
        }
    }

    fun onFragmentViewDestroy() {
        binderManager.destoryViewBinder()
    }

    /**
     * 必不为空如果 没找到就抛出没注册异常, 注意更改 itemView 同时要更改原来的数据 itemData
     */
    inline fun <reified S> getDataBinder(): S {
        return binderManager.getBinder<S>()
    }

    inline fun <reified S> getItemViewType(): Int {
        return binderManager.getBinderType<S>()
    }

    inline fun <reified R> switchBinderPosition(isUp: Boolean) {
        if (isUp && binderManager.switchPosition<PlacerBinder, R>()) {
            //这里有歧义
            //PlacerBinder 换了位置后就是旧的帮买位置的数据，所以移除旧的帮买的数据
            removeBinderData<PlacerBinder>()
        } else if (!isUp && binderManager.switchPosition<R, PlacerBinder>()) {
            removeBinderData<PlacerBinder>()
        }
    }
}