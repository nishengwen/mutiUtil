package com.nsw.xiuhua.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.nsw.xiuhua.databinding.HomeLayoutBinding
import com.nsw.xiuhua.ui.Model3


class HomeFragment : Fragment() {
//    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fragmentHomeBinding: HomeLayoutBinding

    val model3= Model3()
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
//        homeViewModel =
//                ViewModelProvider(this).get(HomeViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//        })

        fragmentHomeBinding=HomeLayoutBinding.inflate(inflater,container,false)

//        fragmentHomeBinding.homeModel=homeViewModel
//        val model2=Model2()
//        fragmentHomeBinding.model2= model2
        fragmentHomeBinding.lifecycleOwner=viewLifecycleOwner
        fragmentHomeBinding.model3=model3
//        container?.postDelayed ({
////            homeViewModel.test1="123412341234123"
////            fragmentHomeBinding.title="123412341234123"
////            model2.setTitle("66666666")
////            homeViewModel.setTitle("66666666")
//            model3.userName="66666666"
//        },3000)
//        container?.postDelayed ({
////            homeViewModel.test1="22222222222222222"
////            fragmentHomeBinding.title="22222222222222222"
////            model2.setTitle("555555555555555")
////            homeViewModel.setTitle("555555555555555")
//            model3.userName="5555555"
//        },6000)


//        val ob:(User) -> Unit=
        model3.userOb.observe(viewLifecycleOwner) { user ->
            user.getName()
        }
        return fragmentHomeBinding.root
    }

}