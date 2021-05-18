package com.nsw.xiuhua.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nsw.xiuhua.databinding.HomeLayoutBinding
import com.xiuhua.mutilutil.quickadapter.MultiAdapter
import com.xiuhua.mutilutil.quickadapter.QuickAdapter


class HomeFragment : Fragment() {
//    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fragmentHomeBinding: HomeLayoutBinding
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
//        homeViewModel =
//                ViewModelProvider(this).get(HomeViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//        })
        fragmentHomeBinding=HomeLayoutBinding.inflate(inflater,container,false).apply {
            lifecycleOwner=viewLifecycleOwner
        }
//        fragmentHomeBinding.model4=model4
        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val data=QuickAdapter.setDataToList(fragmentHomeBinding.recyclerView,this@HomeFragment)
//        view.postDelayed({data[5].setText("-----------------------------")},5000)

        MultiAdapter.test(fragmentHomeBinding.recyclerView,this@HomeFragment)

    }

}