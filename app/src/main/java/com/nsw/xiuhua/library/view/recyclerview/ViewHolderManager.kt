package com.nsw.xiuhua.library.view.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ViewHolderManager {

    private val mViewHolderMap = HashMap<RecyclerView.ViewHolder, Int>()

    /**
     * 创建ViewHolder
     * @param context Context
     * @param parent ViewGroup
     * @param layoutId layoutId
     * @return ViewHolder
     */
    fun createViewHolder(context: Context, parent: ViewGroup, layoutId: Int, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder = object : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(layoutId, parent, false)) {}

        mViewHolderMap.put(viewHolder,viewType)
        return viewHolder
    }

    fun getFirstViewHolder(viewType: Int) :RecyclerView.ViewHolder? {
        for((key,value) in mViewHolderMap){
            if(value==viewType){
                return key
            }
        }
        return null
    }

}