package com.nsw.xiuhua.ui

import android.view.View
import android.widget.TextView
import com.nsw.xiuhua.core.toast

class Model3 {
    val userOb = NotifyLiveData(User("11"))

    fun onClickBtn(view : View ) {
        view as TextView
        view.context.toast(  view.text.toString())
//        userOb.value?.setName("444444444444444")
//        userOb.postValue(User("444444444444444"))
        userOb.getData().name="444444444444444"
        userOb.notifyChange()
    }

//    private val temp=ObservableField<User>().apply {
//        set(User("1111111111111111"))
//    }
//
//    var userOb=temp
//
//    fun onClickBtn(view: View){
//        view.context.toast("2222222222222")
//        userOb.set(User("444444444444444"))
//    }


//    private val temp=ObservableField<User>().apply {
//        set(User("1111111111111111"))
//    }
//
//    var userOb=temp
//
//    fun setName(name: String){
//        userOb.get()!!.setName(name)
//    }
//
//    fun getName()=userOb.get()!!.getName()
//
//
//    fun onClickBtn(view: View){
//        view.context.toast("2222222222222")
////        userOb.get()!!.setName("44444444444444444")
////        userOb.notifyChange()
//        setName("44444444444444444")
//        userOb.notifyChange()
////        userOb.set(User("444444444444444"))
//    }

//        private val temp=ObservableField<String>().apply {
//        set("111111111111111")
//    }
//
//    var userOb=temp
//
//    fun onClickBtn(view: View){
//        view.context.toast("2222222222222")
//        userOb.set("44444444444444444")
//    }
}