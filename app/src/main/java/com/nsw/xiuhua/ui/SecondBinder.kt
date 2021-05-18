package com.nsw.xiuhua.ui

import com.nsw.xiuhua.databinding.SecondTextBinding
import com.xiuhua.mutilutil.quickadapter.DataViewBinder

class SecondBinder :DataViewBinder<SecondTextBinding,Model4>() {
    override fun onCreateViewHolder(viewDataBinding: SecondTextBinding) {

    }

    override fun onBindViewHolder(viewDataBinding: SecondTextBinding, item: Model4) {
        viewDataBinding.model4=item
    }
}