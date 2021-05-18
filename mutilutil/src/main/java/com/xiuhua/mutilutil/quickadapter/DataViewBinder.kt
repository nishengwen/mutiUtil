package com.xiuhua.mutilutil.quickadapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 模板方法 不能存储数据，很多数据对象可能共用一个模板
 */
abstract class DataViewBinder<S : ViewDataBinding, T : Any> {
    abstract fun onCreateViewHolder(viewDataBinding: S)
    abstract fun onBindViewHolder(viewDataBinding: S, item: T)

    /**
     * 获取此 Binder的ViewDataBinding
     */
    internal fun getSDataBinding(inflater: LayoutInflater,parent: ViewGroup):ViewDataBinding{
        val method=(getSuperGenericTypes()[0] as Class<*>).getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java,Boolean::class.java)
        return  method.invoke(null,inflater,parent,false) as ViewDataBinding
    }

    /**
     * 判断数据类型是否是T
     */
    internal fun isTType(t: T): Boolean{
       return t.javaClass==getSuperGenericTypes()[1]
    }

    /**
     * 获取父类中确定的泛型不能是变量
     */
    private fun getSuperGenericTypes(): Array<out Type> {
        val parameterizedType = this.javaClass.genericSuperclass as ParameterizedType
        return parameterizedType.actualTypeArguments
    }
}