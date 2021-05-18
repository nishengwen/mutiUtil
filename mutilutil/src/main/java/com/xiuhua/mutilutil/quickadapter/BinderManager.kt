package com.xiuhua.mutilutil.quickadapter

import androidx.databinding.ViewDataBinding
import java.lang.IllegalArgumentException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 管理模板和数据对应的数据结构
 */
class BinderManager {
    val binders = sortedMapOf<Int, DataViewBinder<in ViewDataBinding, in Any>>()

    fun addBinder(dataViewBinder: DataViewBinder<out ViewDataBinding, out Any>){
          binders[binders.size] = dataViewBinder as DataViewBinder<ViewDataBinding, Any>
    }

    /**
     * create/bind ViewHolder的时候viewType->binder
     */
    fun getBinder(viewType: Int)=binders[viewType]?:throw IllegalArgumentException("不存在这个${viewType}的Binder")

    /**
     * 添加数据的时候使用 数据对象->获取ViewType
     */
    fun getItemViewType(data: Any): Int {
        for ((key, value) in binders) {
            if(data::class.java==getSuperGenericTypes(value::class.java)[1]){
                return key
            }
        }
        throw IllegalArgumentException("没找到有处理这个数据的binder,数据为${data::class.java}")
    }

    /**
     * 删除指定种类数据使用 数据类型->获取ViewType
     */
    inline fun <reified T> getItemViewType(): Int {
        for ((key, value) in binders) {
            if (T::class.java == getSuperGenericTypes(value::class.java)[1]) {
                return key
            }
        }
        throw IllegalArgumentException("没找到有处理这个数据的binder,数据为${T::class.java}")
    }

    /**
     * 获取父类中确定的泛型不能是变量
     */
    fun getSuperGenericTypes(clazz: Class<*>): Array<out Type> {
        val parameterizedType = clazz.genericSuperclass as ParameterizedType
        return parameterizedType.actualTypeArguments
    }
}