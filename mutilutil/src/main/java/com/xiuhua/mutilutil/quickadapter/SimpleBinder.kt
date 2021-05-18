package com.xiuhua.mutilutil.quickadapter

import com.xiuhua.mutilutil.databinding.QuickTextBinding

class SimpleBinder:DataViewBinder<QuickTextBinding,NotifyText> (){
    override fun onCreateViewHolder(viewDataBinding: QuickTextBinding) {

    }

    override fun onBindViewHolder(viewDataBinding: QuickTextBinding, item: NotifyText) {
        viewDataBinding.notifyText= item
    }
}