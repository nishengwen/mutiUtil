package com.nsw.xiuhua.ui

import androidx.lifecycle.LiveData

/**
 * 设定初始值 : Any 非空类型
 */
class NotifyLiveData<T : Any>(value: T) : LiveData<T>(value) {
    fun setData(value: T) {
        postValue(value)
    }
    fun getData() = value!!
    /**
     *   增加notifyChange 应对局部刷新
     */
    fun notifyChange() {
        postValue(value!!)
    }
}