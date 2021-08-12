package com.nsw.xiuhua.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nsw.xiuhua.R
import com.nsw.xiuhua.databinding.FragmentDashboardBinding
import android.util.DisplayMetrics
import android.util.Log


class DashboardFragment : Fragment() {
    private  val dashboardViewModel by lazy{
        ViewModelProvider(this).get(DashboardViewModel::class.java)
    }
    private lateinit var rootBinding: FragmentDashboardBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        rootBinding=FragmentDashboardBinding.inflate(layoutInflater)
        rootBinding.lifecycleOwner=this
        return rootBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootBinding.parentRecyclerView.adapter=ParentAdapter(requireContext(),rootBinding.nestScrollLinerLayout)
        Log.d("DashboardFragment_dpi", "onViewCreated: "+getDisplayMetrics(requireContext()))
    }


    fun getDisplayMetrics(cx: Context): String? {
        var str = ""
        var dm = DisplayMetrics()
        dm = cx.getApplicationContext().getResources().getDisplayMetrics()
        val screenWidth = dm.widthPixels
        val screenHeight = dm.heightPixels
        val density = dm.density
        val scaledDensity = dm.scaledDensity
        val densityDpi = dm.densityDpi
        val xdpi = dm.xdpi
        val ydpi = dm.ydpi
        str += "he absolute width of the display in pixels: $screenWidth\n"
        str += "The absolute height of the display in pixels: $screenHeight\n"
        str += "The logical density of the display: $density\n"
        str += "A scaling factor for fonts displayed on the display: $scaledDensity\n"
        str += "The screen density expressed as dots-per-inch: $densityDpi\n"
        str += "The exact physical pixels per inch of the screen in the X dimension: $xdpi\n"
        str += "The exact physical pixels per inch of the screen in the Y dimension: $ydpi\n"
        return str
    }


}