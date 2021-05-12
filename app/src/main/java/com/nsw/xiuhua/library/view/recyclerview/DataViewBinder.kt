package com.nsw.xiuhua.library.view.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 模板 数据到view的绑定
 *
 */
 abstract class    DataViewBinder<T> { // 由于T只是作为参数，所以T是contravariant逆变，加in
    /**
     * 获取layoutId
     */
    abstract  val itemViewLayoutId: Int

    /**
     * 判断类型
     *
     * @param item data数据
     * @param position 当前position
     * @return true显示数据
     */
    abstract fun isItemViewType(item: ItemBean<T>): Boolean

    /**
     * 显示数据
     *
     * @param context Context
     * @param holder ViewHolder
     * @param item data数据
     * @param position 当前position
     */
    var itemData: T?=null
    var itemHolder: RecyclerView.ViewHolder?=null
    var itemView: View?=null
    var ctx: Context?=null

    open fun bindData(context: Context, holder: RecyclerView.ViewHolder, item: T, position: Int){
        itemData=item
        itemHolder=holder
        itemView=holder.itemView
        ctx=context
    }

    open fun onViewHolderCreated(context: Context,holder: RecyclerView.ViewHolder){
        itemHolder=holder
        itemView=holder.itemView
        ctx=context
    }

    protected open fun setRootViewVisiblity(visible: Boolean) {
        itemView?.apply {
            val param = layoutParams as RecyclerView.LayoutParams
            if (visible) {
                if(visibility == View.GONE){
                    param.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    param.width = ViewGroup.LayoutParams.MATCH_PARENT
                    visibility = View.VISIBLE
                    layoutParams = param
                }
            } else {
                if(visibility != View.GONE){
                    visibility = View.GONE
                    param.height = 0
                    param.width = 0
                    layoutParams = param
                }
            }
        }
    }

    fun onDestoryBinder() {

    }

    fun isBindData(): Boolean {
        return itemView != null
    }

}