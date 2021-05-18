package com.nsw.xiuhua.ui

import com.nsw.xiuhua.databinding.QuickTextBinding
import com.xiuhua.mutilutil.core.NotifyText
import com.xiuhua.mutilutil.quickadapter.DataViewBinder

/**
 * 示例，设置 ViewDataBinding 和 Model
 * Model 必须不同类型
 */
class SimpleBinder: DataViewBinder<QuickTextBinding, NotifyText>() {
    override fun onCreateViewHolder(viewDataBinding: QuickTextBinding) {

    }

    override fun onBindViewHolder(viewDataBinding: QuickTextBinding, item: NotifyText) {
        viewDataBinding.notifyText= item
    }
}