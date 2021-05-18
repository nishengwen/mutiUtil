package com.xiuhua.mutilutil.quickadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 单模板列表
 *
 * 数据和模板 多对一
 */
class SingleAdapter<S : ViewDataBinding, T : Any> private constructor(
    private val mContext: Context,
    private val mLifecycleOwner: LifecycleOwner,
    private val data: MutableList<T>,
    val binder: DataViewBinder<S, T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    constructor (data: MutableList<T>, activity: ComponentActivity, binder: DataViewBinder<S, T>) : this(activity, activity, data, binder)

    constructor (data: MutableList<T>, fragment: Fragment, binder: DataViewBinder<S, T>) : this(
        fragment.requireContext(),
        fragment.viewLifecycleOwner,
        data,
        binder
    )

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewDataBinding: ViewDataBinding = binder.getSDataBinding(inflater,parent)
        viewDataBinding.lifecycleOwner = mLifecycleOwner
        return DataBindingViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as DataBindingViewHolder
        binder.onBindViewHolder(holder.dataBinding as S, data[position])
        holder.dataBinding.executePendingBindings()
    }

    override fun getItemCount(): Int = data.size

    private class DataBindingViewHolder(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)

    /**
     * 获取父类中确定的泛型不能是变量
     */
    fun getSuperGenericTypes(clazz: Class<*>): Array<out Type> {
        val parameterizedType = clazz.genericSuperclass as ParameterizedType
        return parameterizedType.actualTypeArguments
    }
}