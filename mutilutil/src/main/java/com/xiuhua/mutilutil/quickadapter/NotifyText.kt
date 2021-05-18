package com.xiuhua.mutilutil.quickadapter

import android.view.View
import com.xiuhua.mutilutil.core.NotifyLiveData
import com.xiuhua.mutilutil.core.toast


class NotifyText(content: String) {
    val notifyLiveData =NotifyLiveData(content)

    fun setText(value: String) {
        notifyLiveData.setData(value)
    }

    fun getText() : String {
        return  notifyLiveData.getData()
    }

    fun onClick(view : View){
        notifyLiveData.getData().toast(view.context)
    }
}