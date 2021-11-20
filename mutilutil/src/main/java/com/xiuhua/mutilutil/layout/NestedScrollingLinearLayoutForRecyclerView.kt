package com.xiuhua.mutilutil.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renrenche.carapp.widget.custom.NestedScrollingLinearLayout
import kotlin.math.max
import kotlin.math.min


/**
 *
 * mParentRecyclerView 和 mChildRecyclerView 都会找到这个NestedScrollingParent2Layout，把Scroll和fling事件传过来
 *
 */
class NestedScrollingLinearLayoutForRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollingLinearLayout(context, attrs, defStyleAttr) {

    //在mParentRecyclerView
    private var mParentRecyclerView: RecyclerView? = null

    //mChildRecyclerView 的父容器
    private var mInnerLayout: View? = null

    //嵌套在mParentRecyclerView itemView中的recyclerView
    private var mChildRecyclerView: RecyclerView? = null

    /**
     * 在嵌套滑动的子View未滑动之前，判断父view是否优先与子view处理(也就是父view可以先消耗，然后给子view消耗）
     *
     * @param target   具体嵌套滑动的那个子类，就是手指滑的那个 产生嵌套滑动的view
     * @param dx       水平方向嵌套滑动的子View想要变化的距离
     * @param dy       垂直方向嵌套滑动的子View想要变化的距离 dy<0向下滑动 dy>0 向上滑动
     * @param consumed 这个参数要我们在实现这个函数的时候指定，回头告诉子View当前父View消耗的距离
     * consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离 好让子view做出相应的调整
     * @param type     滑动类型，ViewCompat.TYPE_NON_TOUCH fling 效果ViewCompat.TYPE_TOUCH 手势滑动
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(target, dx, dy, consumed, type)
        //保证线程安全 使用值对象
        val parentRcy = mParentRecyclerView
        val sonRcy = mChildRecyclerView
        val innerLayout = mInnerLayout
        if (parentRcy != null && sonRcy != null && innerLayout != null) {

            //因为mParentRecyclerView是默认拦截滑动事件，所以target一直是parentRcy
            if (target === parentRcy) {
                handleRecyclerViewScroll(innerLayout.top, dy, consumed, sonRcy)
            }
        }
    }


    /**
     * 滑动外层RecyclerView时，的处理
     *
     * @param innerLayoutTop tab到屏幕顶部的距离，是0就代表到顶了
     * @param dy          目标滑动距离， dy>0 代表向上滑
     * @param consumed
     */
    private fun handleRecyclerViewScroll(
        innerLayoutTop: Int,
        dy: Int,
        consumed: IntArray, childRcy: RecyclerView
    ) {
        val childScrolledY = childRcy.computeVerticalScrollOffset()
        val childScrolledYLeft = childRcy.computeVerticalScrollRange() - childScrolledY - childRcy.computeVerticalScrollExtent()
        // 不知道为什么当recyclerview再回到屏幕内的时候 向下滑动了一行（只有使用StaggeredGridLayoutManager这样，这里做了修正保证sonRcy滑动最顶部或者最底部）
        when {
            //上滑最大距离
            innerLayoutTop < 0 -> childRcy.scrollBy(0, childScrolledYLeft)
            //下滑最大距离
            innerLayoutTop > 0 -> childRcy.scrollBy(0, -childScrolledY)
        }

        //parent先吃最多lastItemTop
        //son最多吃 -childScrolledY~childScrolledYLeft
        when (innerLayoutTop) {
            in dy..-1 -> {
                //当 dy<innerLayoutTop<0 就需要son滑动了
                val childScroll = max(-childScrolledY, dy - innerLayoutTop)
                childRcy.scrollBy(0, childScroll)
                consumed[1] = childScroll
            }

            0 -> {
                //childScroll取-childScrolledY和childScrolledYLeft 之间
                val childScroll = max(min(dy, childScrolledYLeft), -childScrolledY)
                childRcy.scrollBy(0, childScroll)
                consumed[1] = childScroll
            }

            in 1..dy -> {
                //当 1<innerLayoutTop<dy 就需要son滑动了
                val childScroll = min(childScrolledYLeft, dy - innerLayoutTop)
                childRcy.scrollBy(0, childScroll)
                consumed[1] = childScroll
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        //直接获取外层RecyclerView
        mParentRecyclerView = getRecyclerView(this)
    }

    private fun getRecyclerView(viewGroup: ViewGroup): RecyclerView? {
        val childCount = viewGroup.childCount
        for (i in 0 until childCount) {
            val childAt = getChildAt(i)
            if (childAt is RecyclerView) {
                if (mParentRecyclerView == null) {
                    return childAt
                }
            }
        }
        return null
    }

    /**
     * 传入内部RecyclerView
     *
     * @param childRecyclerView
     */
    fun setChildRecyclerView(childRecyclerView: RecyclerView) {
        //当自己可见的时候赋值到嵌套滑动
        childRecyclerView.post { mChildRecyclerView = childRecyclerView }
    }

    /**
     *
     * 外层RecyclerView的最后一个item，即：tab + viewPager
     * 用于判断 滑动 临界位置
     */
    fun setLastItem(lastItemView: View) {
        //当自己可见的时候赋值到嵌套滑动
        lastItemView.post { mInnerLayout = lastItemView }
    }
}
