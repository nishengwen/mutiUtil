package com.nsw.xiuhua.library.view.recyclerview

class PlacerBinder : DataViewBinder<Any>(){
    override val itemViewLayoutId=-1

    override fun isItemViewType(item: ItemBean<Any>): Boolean {
        return false
    }
}