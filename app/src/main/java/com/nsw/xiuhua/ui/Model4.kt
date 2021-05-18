package com.nsw.xiuhua.ui

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.xiuhua.mutilutil.core.toast

class Model4(title: String) {

    val name =MutableLiveData<String>(title)

    fun setModelName(value: String) {
        name.value=value
    }

    fun getModelName() : String {
      return  name.value!!
    }

    fun onClick(view : View){
        name.value!!.toast(view.context)
        name.hasObservers().toString().toast(view.context)
    }
}