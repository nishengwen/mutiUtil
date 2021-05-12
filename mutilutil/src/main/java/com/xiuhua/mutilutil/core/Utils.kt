package com.xiuhua.mutilutil.core
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
//核心的工具类
fun String?.toast(context: Context) {
    this.apply {
        if (!TextUtils.isEmpty(this)) {
            Toast.makeText(context, this, Toast.LENGTH_LONG).show()
        }
    }
}

//一些常用的lambda
typealias  SetListener<T> = T.() -> Unit
typealias  OutData<T> = (T) -> Unit
typealias  OutAction = () -> Unit
typealias  Filter<T> = (T) -> Boolean




