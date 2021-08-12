package com.nsw.xiuhua.ui.dashboard

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nsw.xiuhua.R
import com.xiuhua.mutilutil.layout.NestedScrollingLinearLayoutForRecyclerView

class ParentAdapter(private val context: Context, private val scrollParent: NestedScrollingLinearLayoutForRecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val testList1 = mutableListOf<String>().apply {
        for (index in 1..40) {
            add("$index-$index-$index-$index")
        }
    }

    private val testList2 = mutableListOf<String>().apply {
        for (index in 1..40) {
            add("$index-$index-$index-$index")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> TextViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text, parent, false))
            1 -> ListHolder(LayoutInflater.from(context).inflate(R.layout.item_list, parent, false))
            else ->
                TextViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextViewHolder -> {
                if (position < testList1.size) {
                    holder.textView.text = testList1[position]
                }

                else if (position > testList1.size) {
                    holder.textView.text = testList2[position - testList1.size - 1]
                }
            }
            is ListHolder -> {
                if (holder.recyclerView.adapter == null) {
                    val layoutManager= StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
                    layoutManager.gapStrategy=StaggeredGridLayoutManager.GAP_HANDLING_NONE
                    holder.recyclerView.layoutManager =layoutManager
//                    holder.recyclerView.layoutManager =LinearLayoutManager(context)
                    holder.recyclerView.adapter = SonAdapter(context)
                    //当itemView可见的时候嵌套滑动有效
                    scrollParent.setLastItem(holder.itemView)
                    scrollParent.setChildRecyclerView(holder.recyclerView)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return testList1.size + 1 + testList2.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == testList1.size) {
            1
        } else {
            0
        }
    }

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text);
    }


    class ListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.son_recyclerView);
    }

}