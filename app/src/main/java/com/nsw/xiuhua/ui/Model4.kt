package com.nsw.xiuhua.ui

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.xiuhua.mutilutil.core.NotifyLiveData
import com.xiuhua.mutilutil.core.toast

class Model4(title: String) {

    val name =NotifyLiveData<String>(title)

    fun setModelName(value: String) {
        name.setData(value)
    }

    fun getModelName() : String {
      return  name.getData()
    }

    fun onClick(view : View){
        name.getData().toast(view.context)
        name.hasObservers().toString().toast(view.context)
    }
}