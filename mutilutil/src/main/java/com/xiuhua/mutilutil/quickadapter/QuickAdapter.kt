package com.xiuhua.mutilutil.quickadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xiuhua.mutilutil.R
import com.xiuhua.mutilutil.databinding.QuickTextBinding

/**
 * 只支持一种模板重复列表
 */
class QuickAdapter<S : ViewDataBinding, T> private constructor(
    val context: Context,
    val lifecycle: LifecycleOwner,
    private val layoutId: Int,
    private val data: MutableList<T>,
    private val binder: (S, T) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    constructor (layoutId: Int, data: MutableList<T>, activity: ComponentActivity, binder: (S, T) -> Unit) : this(activity, activity, layoutId, data, binder)

    constructor (layoutId: Int, data: MutableList<T>, fragment: Fragment, binder: (S, T) -> Unit) : this(
        fragment.requireContext(),
        fragment.viewLifecycleOwner,
        layoutId,
        data,
        binder
    )

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val s: S = DataBindingUtil.inflate(inflater, layoutId, parent, false)
        s.lifecycleOwner = lifecycle
        return DataBindingViewHolder(s)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataBindingViewHolder) {
            binder.invoke(holder.dataBinding as S, data[position])
            holder.dataBinding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int = data.size

    private class DataBindingViewHolder(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)


    companion object {
        /**
         * 方便快速测试数据,提供模拟数据
         */
        fun setDataToList(recyclerView: RecyclerView, activity: ComponentActivity, mLayoutManager: RecyclerView.LayoutManager=LinearLayoutManager(recyclerView.context), textSize: Int=20):MutableList<NotifyText>{
            val data=getSimpleTextList(textSize)
            recyclerView.apply {
                layoutManager=mLayoutManager
                adapter= getSimpleTextListAdapter(data,activity)
            }
            return data
        }

        fun setDataToList(recyclerView: RecyclerView, fragment: Fragment, mLayoutManager: RecyclerView.LayoutManager=LinearLayoutManager(recyclerView.context), textSize: Int=20):MutableList<NotifyText>{
            val data=getSimpleTextList(textSize)
            recyclerView.apply {
                layoutManager=mLayoutManager
                adapter= getSimpleTextListAdapter(data,fragment)
            }
            return data
        }

       private fun getSimpleTextListAdapter(data: MutableList<NotifyText>, activity: ComponentActivity): QuickAdapter<QuickTextBinding, NotifyText> {
            return QuickAdapter(R.layout.quick_text, data, activity) { quickTextBinding, quickText ->
                quickTextBinding.notifyText = quickText
            }
        }

       private fun getSimpleTextListAdapter( data: MutableList<NotifyText>, fragment: Fragment): QuickAdapter<QuickTextBinding, NotifyText> {
            return QuickAdapter(R.layout.quick_text, data, fragment) { quickTextBinding, quickText ->
                quickTextBinding.notifyText = quickText
            }
        }

        private fun getSimpleTextList(size: Int):MutableList<NotifyText>{
            return MutableList(size){
                    index ->
                NotifyText("${index}-${index}-${index}-${index}-${index}-${index}-${index}-${index}")
            }
        }
    }

}