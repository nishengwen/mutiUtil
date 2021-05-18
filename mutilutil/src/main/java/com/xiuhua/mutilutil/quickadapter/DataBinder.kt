package com.xiuhua.mutilutil.quickadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class DataViewBinder<S : ViewDataBinding, T : Any> {
    abstract fun onCreateViewHolder(viewDataBinding: S)
    abstract fun onBindViewHolder(viewDataBinding: S, item: T)

    /**
     * 反射获取当前保存在父类中的T类型
     */
    fun getTType(): Type {
        val parameterizedType = this.javaClass.genericSuperclass as ParameterizedType
        val actualTypeArguments = parameterizedType.actualTypeArguments
        return actualTypeArguments[1]
    }
    /**
     * 反射获取泛型参数S的静态方法inflate 并调用
     */
    fun getSViewDataBinding(inflater: LayoutInflater, parent: ViewGroup):ViewDataBinding{
        val parameterizedType = this.javaClass.genericSuperclass as ParameterizedType
        val actualTypeArguments = parameterizedType.actualTypeArguments
        val method=(actualTypeArguments[0] as Class<*>).getDeclaredMethod("inflate",LayoutInflater::class.java,ViewGroup::class.java,Boolean::class.java)
        return method.invoke(null,inflater,parent,false) as ViewDataBinding
    }
}