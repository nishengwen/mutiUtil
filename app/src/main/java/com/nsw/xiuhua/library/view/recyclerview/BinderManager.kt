package com.nsw.xiuhua.library.view.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * itemView管理器
 * 用数据模型获取相应的DataViewBinder
 *
 */
class BinderManager<T> {

    /**
     * itemView集合
     */
    val dataViewBinders = sortedMapOf<Int, DataViewBinder<T>>()

    /**
     * 获取itemView总数
     */
    val dataViewBinderCount: Int
        get() = dataViewBinders.size

    fun registerDataViewBinder(dataViewBinder: DataViewBinder<*>) {
        for ((key, value) in dataViewBinders) {
            if (value.itemViewLayoutId == dataViewBinder.itemViewLayoutId) {
                throw IllegalArgumentException("An DataViewBinder is already registered for the delegate = $dataViewBinder.")
            }
        }
        dataViewBinders[dataViewBinders.size * 10] = dataViewBinder as DataViewBinder<T>
    }
    /**
     * 根据数据模型获取itemView的类型 Int
     *
     * @param item data数据
     * @param position 当前position
     * @return Int
     */
    fun getItemViewType(item: ItemBean<T>): Int {
        for ((key, value) in dataViewBinders) {
            if (value.isItemViewType(item)) {
                return key
            }
        }
        throw IllegalArgumentException(
                "No DataViewBinder registered that matches in data = $item source");
    }

    /**
     * 根据数据类型获取binder
     */
    fun getItemViewBinder(item: ItemBean<T>): DataViewBinder<T> {
        for ((_, value) in dataViewBinders) {
            if (value.isItemViewType(item)) {
                return value
            }
        }
        throw IllegalArgumentException(
                "No DataViewBinder registered that matches in data = $item source");
    }

    /**
     * 创建ViewHolder
     */
    fun createViewHolder(context: Context, parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val dataViewBinder = getBinder(viewType)
        val viewHolder = object : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(dataViewBinder.itemViewLayoutId, parent, false)) {}
        dataViewBinder.onViewHolderCreated(context, viewHolder)
        return viewHolder
    }

    /**
     * 绑定数据
     *
     * @param context Context
     * @param holder ViewHolder
     * @param item data数据
     * @param position 当前position
     */
    fun bindData(context: Context, holder: RecyclerView.ViewHolder, item: ItemBean<T>, position: Int) {
        for ((key, value) in dataViewBinders) {
            if (value.isItemViewType(item)) {
                value.bindData(context, holder, item.data, position);
                return@bindData
            }
        }
        throw IllegalArgumentException(
                "No DataViewBinder registered that matches position= $position in data = $item source")
    }

    inline fun <reified S, reified R> switchPosition(): Boolean {
        var positionS = -1
        var dataS: DataViewBinder<T>? = null
        var positionR = -1
        var dataR: DataViewBinder<T>? = null;

        for ((key, value) in dataViewBinders) {
            if (value is S) {
                positionS = key
                dataS = value
            } else if (value is R) {
                positionR = key
                dataR = value
            }
            if (positionS != -1 && positionR != -1) {
                break
            }
        }

        if (dataS != null && dataR != null && positionR > positionS) {
            dataViewBinders[positionS] = dataR
            dataViewBinders[positionR] = dataS
            return true
        }
        return false
    }


    /**
     * 获取itemView
     *
     * @param viewType 代表itemView的下标
     * @return DelegateType
     */
    fun getBinder(viewType: Int): DataViewBinder<T> = dataViewBinders[viewType]
            ?: throw IllegalArgumentException("No DataViewBinder registered that matches in data = $viewType source");

    fun destoryViewBinder() {
        for ((key, value) in dataViewBinders) {
            value.onDestoryBinder()
        }
    }

    inline fun <reified S> getBinder(): S {
        for ((key,value) in dataViewBinders){
            if (S::class.java == value::class.java){
                return value as S
            }
        }
        throw IllegalArgumentException("No DataViewBinder registered that matches = ${S::class}.")
    }

    inline fun <reified S> getBinderType(): Int {
        for ((key,value) in dataViewBinders){
            if (S::class.java == value::class.java){
                return key
            }
        }
        throw IllegalArgumentException("No DataViewBinder registered that matches = ${S::class}.")
    }
}

