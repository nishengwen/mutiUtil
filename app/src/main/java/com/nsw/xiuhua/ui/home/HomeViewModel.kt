package com.nsw.xiuhua.ui.home

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.nsw.xiuhua.ui.User

class HomeViewModel : ViewModel() {

    val user: ObservableField<User> = ObservableField();

    init {
        user.set(User("init"))
    }

    fun getTitle(): String {
        return user.get()!!.name
    }

    fun setTitle(str: String){
       user.set(User(str))
    }

}