package com.nsw.xiuhua.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nsw.xiuhua.R

class SonAdapter(val context: Context)  : RecyclerView.Adapter<SonAdapter.TextViewHolder>() {
    private val testList= mutableListOf<String>().apply {
        for(index  in 1..100){
            add(">>>>>>>>>>>>$index-$index-$index-$index")
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text,parent,false))
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.textView.text=testList[position]
    }

    override fun getItemCount(): Int {
        return testList.size
    }

    class  TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val textView:TextView = itemView.findViewById(R.id.text);
    }
}