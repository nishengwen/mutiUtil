package com.xiuhua.mutilutil.core
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

//核心的工具类
fun String?.toast(context: Context) {
    this.apply {
        if (!TextUtils.isEmpty(this)) {
            Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
        }
    }
}

//一些常用的lambda
typealias  OutTAction<T> = T.() -> Unit
typealias  OutData<T> = (T) -> Unit
typealias  OutAction = () -> Unit
typealias  Filter<T> = (T) -> Boolean

//测试用数据
fun getSimpleTextList(size: Int):MutableList<NotifyText>{
    return MutableList(size){
            index ->
        NotifyText("${index}-${index}-${index}-${index}-${index}-${index}-${index}-${index}")
    }
}




