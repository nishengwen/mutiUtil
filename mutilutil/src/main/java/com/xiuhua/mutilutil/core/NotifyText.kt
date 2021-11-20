package com.xiuhua.mutilutil.core

import android.view.View
import android.widget.TextView





class NotifyText(content: String) {
     var test: TextView?=null;

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