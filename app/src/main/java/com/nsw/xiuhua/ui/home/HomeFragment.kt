package com.nsw.xiuhua.ui.home

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.nsw.xiuhua.databinding.HomeLayoutBinding
import com.nsw.xiuhua.library.view.recyclerview.PullDownCoordinatorLayout
import com.nsw.xiuhua.ui.Model4
import com.nsw.xiuhua.ui.SecondBinder
import com.nsw.xiuhua.ui.SimpleBinder
import com.xiuhua.mutilutil.core.NotifyText
import com.xiuhua.mutilutil.quickadapter.*

 class OffsetListener : AppBarLayout.OnOffsetChangedListener {
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout?.apply {
            outData?.invoke((totalScrollRange + verticalOffset).toFloat() / totalScrollRange,totalScrollRange + verticalOffset)
        }
    }

    var outData: ((Float,Int) -> Unit)? = null

    fun onScrollPercent(outData: (Float,Int) -> Unit) {
        this@OffsetListener.outData = outData
    }

}

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
//        fragmentHomeBinding.model4=Model4("11111111111")
        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentHomeBinding.appbar.addOnOffsetChangedListener(
            OffsetListener().apply {
                onScrollPercent { scrollPercent,offSetY ->
                    if(scrollPercent<0.9f){
                        fragmentHomeBinding.goLive.alpha=0f
                    }else{
                        val alf= (scrollPercent-0.9f)*10
                        fragmentHomeBinding.goLive.alpha=alf
                        fragmentHomeBinding.coordinator.setHandlePullDown(offSetY>=fragmentHomeBinding.appbar.totalScrollRange)
                    }
                }
            }
        )
        fragmentHomeBinding.coordinator.setDragListener(object : PullDownCoordinatorLayout.DragListener {
            override fun onPullRelease() {
                Toast.makeText(context,"去了直播间",Toast.LENGTH_LONG).show()
                fragmentHomeBinding.goLive.text="下拉去直播间"
            }

            override fun onShowText(offY: Float) {
                fragmentHomeBinding.goLive.text="松开去直播间"
            }

            override fun onPullFailed() {
                fragmentHomeBinding.goLive.text="下拉去直播间"
            }

        })
//        val data=QuickAdapter.setDataToList(fragmentHomeBinding.recyclerView,this@HomeFragment)
//        view.postDelayed({data[5].setText("-----------------------------")},5000)
//        test(fragmentHomeBinding,this@HomeFragment)
        fragmentHomeBinding.webview.apply {
            //开启js
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true // For Baidu Map web page。
            settings.domStorageEnabled = true
            settings.setAppCacheEnabled(true)
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            webChromeClient = WebChromeClient()
            loadUrl("https://www.baidu.com/")
        }
//        fragmentHomeBinding.appbarLayout.addOnOffsetChangedListener(object :AppBarStateChangeListener(){
//            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
//
//
//                state.toString().toast(requireContext())
//            }
//        })
    }

    fun test(recyclerView: RecyclerView, fragment: Fragment) {
//        val mAdapter = MultiAdapter(fragment) {
//            registerBinder(SimpleBinder())
//        }

//        recyclerView.apply {
//            layoutManager = LinearLayoutManager(recyclerView.context)
//            adapter =
//        }
        val mAdapter=MixAdapter(fragment){
            registerBinder(SimpleBinder())
            registerBinder(SecondBinder())
        }

        recyclerView.apply {
            layoutManager=LinearLayoutManager(context)
            adapter=mAdapter
        }

        mAdapter.setList(mutableListOf(NotifyText("11111"),Model4("22222"),NotifyText("11111"),NotifyText("11111"),NotifyText("11111"),Model4("22222"),Model4("22222"),Model4("22222"),Model4("22222"),Model4("22222"),NotifyText("11111"),Model4("22222"),NotifyText("11111"),NotifyText("11111"),NotifyText("11111"),Model4("22222"),Model4("22222"),Model4("22222"),Model4("22222"),Model4("22222")))




//        val notifyText = NotifyText("11111111")
//        mAdapter.refreshOrAddItem(notifyText)
//        (8 tick 1000).apply {
//            onTick {
//                mAdapter.refreshOrAddItem(NotifyText("11111111${it}"))
//            }
//            onFinish {
//                mAdapter.removeItem<NotifyText>()
//            }
//            start()
//        }
    }
    abstract class AppBarStateChangeListener : OnOffsetChangedListener {
        enum class State {
            EXPANDED("展开"), COLLAPSED("折叠"), IDLE("中间");
            var content:String?=null
            constructor(str:String){
                content=str
            }
            override fun toString(): String {
                return content.toString()
            }
        }

        private var mCurrentState = State.IDLE
        override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {

            mCurrentState =
             if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                State.EXPANDED
            } else if (Math.abs(i) >= appBarLayout.totalScrollRange) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                State.COLLAPSED
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                State.IDLE
            }
        }

        abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: State?)
    }
}
val GROUP_A_LIST= listOf(
    "app-indexpage-aisellversionnew",
    "app-indexpage-aisellversionnew-a",
    "app-indexpage-aisellversionnew-b",
    "app-indexpage-aisellversionnew-c",
    "app-indexpage-aisellversionnew-d",
    "app-indexpage-aisellversionnew-e",
    "app-indexpage-aisellversionnew-f"
)
fun main(args: Array<String>) {
    val x=System.nanoTime()
    System.out.println("app-indexpage-aisellversionnew" in GROUP_A_LIST)
    System.out.println(System.nanoTime()-x)

}