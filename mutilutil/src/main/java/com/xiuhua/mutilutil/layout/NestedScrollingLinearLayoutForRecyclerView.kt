package com.xiuhua.mutilutil.layout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
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
    private val TAG = this.javaClass.simpleName

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
            // 不知道为什么当recyclerview再回到屏幕内的时候 向下滑动了一行（只有使用StaggeredGridLayoutManager这样，这里做了修正保证sonRcy滑动最顶部或者最底部）
            when {
                //上滑最大距离
                innerLayout.top < 0 -> sonRcy.scrollBy(0, sonRcy.computeVerticalScrollRange() - sonRcy.computeVerticalScrollExtent())
                //下滑最大距离
                innerLayout.top > 0 -> sonRcy.scrollBy(0, sonRcy.computeVerticalScrollExtent() - sonRcy.computeVerticalScrollRange())
            }

            //因为mParentRecyclerView是默认拦截滑动事件，所以target一直是parentRcy
            if (target === parentRcy) {
                handleRecyclerViewScroll(innerLayout.top, dy, consumed,parentRcy,sonRcy)
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
        consumed: IntArray, parentRecyclerView: RecyclerView, childRecyclerView: RecyclerView
    ) {
        val childScrolledY = childRecyclerView.computeVerticalScrollOffset()
        val childScrolledYLeft = childRecyclerView.computeVerticalScrollRange() - childScrolledY - childRecyclerView.computeVerticalScrollExtent()
        //parent 先吃最多lastItemTop 的dy 负的（下滑）更没 son什么事情了,return 直接就是不管让parent内部拦截
        if(innerLayoutTop>0 ){
            if(dy<innerLayoutTop) {
                return
            }else{
                //当 dy>innerLayoutTop 就需要son滑动了
                parentRecyclerView.scrollBy(0, innerLayoutTop)
                val childScroll= min(childScrolledYLeft,dy - innerLayoutTop)
                childRecyclerView.scrollBy(0, childScroll)
                consumed[1]=childScroll+innerLayoutTop
            }
        }else if(innerLayoutTop<0){
            if(dy>innerLayoutTop){
                return
            }else{
                //当 dy<innerLayoutTop 就需要son滑动了
                parentRecyclerView.scrollBy(0, innerLayoutTop)
                val childScroll= max(-childScrolledY,dy - innerLayoutTop)
                childRecyclerView.scrollBy(0, childScroll)
                consumed[1]=childScroll+innerLayoutTop
            }
        }else{
            //childScroll取-childScrolledY和childScrolledYLeft 之间
            val childScroll= max(min(dy,childScrolledYLeft),-childScrolledY)
            childRecyclerView.scrollBy(0, childScroll)
            consumed[1] = childScroll
        }

//
//        //tab上边没到顶
//        if (innerLayoutTop > 0) {
//            if (dy > 0) {
//                //向上滑  1、parent 先吃最多lastItemTop 2、然后 child后吃 最多childScrolledYLeft 3、剩下的再让parent吃,吃不了没事系统自行处理
//                if (innerLayoutTop > dy) {
//                    //tab的top>想要滑动的dy,就让外部RecyclerView自行处理
//                    Log.d(TAG, "没到顶parent自行处理 上滑  parent>lastItemTop：$innerLayoutTop dy:$dy")
//                } else {
//                    Log.d(
//                        TAG,
//                        "没到顶parent和son处理 上滑  parent>lastItemTop：" + innerLayoutTop + " son>dy - lastItemTop:" + (dy - innerLayoutTop)
//                    )
//                    //tab的top<=想要滑动的dy,先滑外部RecyclerView，滑距离为lastItemTop，刚好到顶；剩下的就滑内层了。
////                    consumed[1] = dy;
//                    mParentRecyclerView!!.scrollBy(0, innerLayoutTop)
//                    //                    mChildRecyclerView.scrollBy(0, dy - lastItemTop);
//                    //子recyclerview最多只能消化childScrolledY,剩下的就让外部RecyclerView自行处理
//                    val childScrolledY = mChildRecyclerView!!.computeVerticalScrollOffset()
//                    val childScrolledYLeft =
//                        mChildRecyclerView!!.computeVerticalScrollRange() - childScrolledY - mChildRecyclerView!!.computeVerticalScrollExtent()
//                    if (childScrolledYLeft > dy - innerLayoutTop) {
//                        mChildRecyclerView!!.scrollBy(0, dy - innerLayoutTop)
//                        Log.d(
//                            TAG + "555",
//                            "childScrolledY：" + (dy - innerLayoutTop) + " childScrolledYLeft:" + childScrolledYLeft
//                        )
//                        consumed[1] = dy
//                    } else {
//                        mChildRecyclerView!!.scrollBy(0, childScrolledYLeft)
//                        Log.d(
//                            TAG + "555",
//                            "childScrolledY：$childScrolledYLeft childScrolledYLeft:$childScrolledYLeft"
//                        )
//                        consumed[1] = childScrolledYLeft + innerLayoutTop
//                    }
//                }
//            } else {
//                //parent吃,吃不了没事系统自行处理 向下滑，就让外部RecyclerView自行处理
//                Log.d(TAG, "没到顶parent自行处理下滑   parent>lastItemTop：$innerLayoutTop dy:$dy")
//            }
//            //tab 已经超越顶部
//        } else
//            if (innerLayoutTop < 0) {
//            if (dy > 0) {
//                //parent吃,吃不了没事系统自行处理 ，向上滑，就让外部RecyclerView自行处理
//                Log.d(TAG, "过顶了parent自行处理上滑   parent>lastItemTop：$innerLayoutTop dy:$dy")
//                //child先吃满了
//            } else {
//                //向下滑   1、parent 先吃最多lastItemTop 2、然后 child后吃 最多childScrolledY 3、剩下的再让parent吃,吃不了没事系统自行处理
//                if (innerLayoutTop < dy) {
//                    //tab的top> 想要滑动的dy,就让外部RecyclerView自行处理
//                    Log.d(TAG, "没到顶parent自行处理 下滑  parent>lastItemTop：$innerLayoutTop dy:$dy")
//                } else {
//                    Log.d(
//                        TAG,
//                        "没到顶parent和son处理 上滑  parent>lastItemTop：" + innerLayoutTop + " son>dy - lastItemTop:" + (dy - innerLayoutTop)
//                    )
//                    //tab的top<=想要滑动的dy,先滑外部RecyclerView，滑距离为lastItemTop，刚好到顶；剩下的就滑内层了。
////                    consumed[1] = dy;
//                    //父recycler最多吃掉lastItemTop
//                    mParentRecyclerView!!.scrollBy(0, innerLayoutTop)
//                    //                    mChildRecyclerView.scrollBy(0, dy - lastItemTop);
//                    //子recyclerview最多只能消化childScrolledY,剩下的就让外部RecyclerView自行处理
//                    val childScrolledY = mChildRecyclerView!!.computeVerticalScrollOffset()
//                    val childScrolledYLeft =
//                        mChildRecyclerView!!.computeVerticalScrollRange() - childScrolledY - mChildRecyclerView!!.computeVerticalScrollExtent()
//                    if (childScrolledY > innerLayoutTop - dy) {
//                        mChildRecyclerView!!.scrollBy(0, dy - innerLayoutTop)
//                        Log.d(
//                            TAG + "555",
//                            "childScrolledY：" + (dy - innerLayoutTop) + " childScrolledYLeft:" + childScrolledYLeft
//                        )
//                        consumed[1] = dy
//                    } else {
//                        mChildRecyclerView!!.scrollBy(0, -childScrolledY)
//                        Log.d(
//                            TAG + "555",
//                            "childScrolledY：" + -childScrolledY + " childScrolledYLeft:" + childScrolledYLeft
//                        )
//                        consumed[1] = innerLayoutTop - childScrolledY
//                    }
//                }
//            }
//        } else {
//            if (dy > 0) {
//                //上滑
//                if (childScrolledYLeft > dy) {
//                    mChildRecyclerView!!.scrollBy(0, dy)
//
//                    consumed[1] = dy
//                } else {
//                    mChildRecyclerView!!.scrollBy(0, childScrolledYLeft)
//
//                    consumed[1] = childScrolledYLeft
//                }
//            } else {
//                //下滑,child能消化childScrolledY以内的
//                if (childScrolledY > -dy) {
//                    mChildRecyclerView!!.scrollBy(0, dy)
//
//                    consumed[1] = dy
//                } else {
//                    mChildRecyclerView!!.scrollBy(0, -childScrolledY)
//                    consumed[1] = -childScrolledY
//                }
//            }
//        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        //直接获取外层RecyclerView
        mParentRecyclerView = getRecyclerView(this)
        Log.i(TAG, "onFinishInflate: mParentRecyclerView=$mParentRecyclerView")

        //关于内层RecyclerView：此时还获取不到ViewPager内fragment的RecyclerView，需要在加载ViewPager后 fragment可见时 传入
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
     * 外层RecyclerView的最后一个item，即：tab + viewPager
     * 用于判断 滑动 临界位置
     *
     * @param lastItemView
     */
    fun setLastItem(lastItemView: View) {
        //当自己可见的时候赋值到嵌套滑动
        lastItemView.post { mInnerLayout = lastItemView }
    }
}
