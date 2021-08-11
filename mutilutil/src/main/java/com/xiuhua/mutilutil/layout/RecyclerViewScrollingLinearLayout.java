package com.xiuhua.mutilutil.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * mParentRecyclerView 和 mChildRecyclerView 都会找到这个NestedScrollingParent2Layout，把Scroll和fling事件传过来
 *
 * */
public class RecyclerViewScrollingLinearLayout extends NestedScrollingLinearLayout {
    private final String TAG = this.getClass().getSimpleName();
    //在mParentRecyclerView
    private RecyclerView mParentRecyclerView;
    //嵌套在mParentRecyclerView itemView中的recyclerView
    private RecyclerView mChildRecyclerView;
    //mChildRecyclerView 的父容器
    private View innerLayout;

    public RecyclerViewScrollingLinearLayout(Context context) {
        super(context);
    }

    public RecyclerViewScrollingLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewScrollingLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     * 在嵌套滑动的子View未滑动之前，判断父view是否优先与子view处理(也就是父view可以先消耗，然后给子view消耗）
     *
     * @param target   具体嵌套滑动的那个子类，就是手指滑的那个 产生嵌套滑动的view
     * @param dx       水平方向嵌套滑动的子View想要变化的距离
     * @param dy       垂直方向嵌套滑动的子View想要变化的距离 dy<0向下滑动 dy>0 向上滑动
     * @param consumed 这个参数要我们在实现这个函数的时候指定，回头告诉子View当前父View消耗的距离
     *                 consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离 好让子view做出相应的调整
     * @param type     滑动类型，ViewCompat.TYPE_NON_TOUCH fling 效果ViewCompat.TYPE_TOUCH 手势滑动
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(target,dx,dy,consumed,type);
        //自己处理逻辑
        if (innerLayout == null) {
            return;
        }
        //当mLastItemView不可见的时候get Top不准
        int lastItemTop = innerLayout.getTop();
        int lastItemBottom= innerLayout.getBottom();
        //最大上滑距离
        int maxUpScrollBy=mChildRecyclerView.computeVerticalScrollRange()-mChildRecyclerView.computeVerticalScrollExtent();
        int maxDownScrollBy=-maxUpScrollBy;
       // 不知道为什么当recyclerview再回到屏幕内的时候 向下滑动了一行（只有使用StaggeredGridLayoutManager这样，这里做了修正）
        if(lastItemTop>0&&lastItemTop<getHeight()){
            //下滑最大距离
            mChildRecyclerView.scrollBy(0,maxDownScrollBy);
        }else if(lastItemBottom>0&&lastItemBottom<getHeight()){
            //上滑最大距离
            mChildRecyclerView.scrollBy(0,maxUpScrollBy);
        }

        Log.d(TAG+"222", "getheight:"+getHeight()+"lastItemTop："+lastItemTop+" lastItemBottom:"+lastItemBottom+" height:"+(lastItemBottom-lastItemTop));
        if (target == mParentRecyclerView) {
            handleParentRecyclerViewScroll(lastItemTop,lastItemBottom, dy, consumed);
        }
        //因为RecyclerView是默认拦截滑动事件，所以不会走这个分支了
        else if (target == mChildRecyclerView) {
            handleChildRecyclerViewScroll(lastItemTop, dy, consumed);
            Log.d(TAG, "onNestedPreScroll: son");

        }

    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        //子view没消化掉Fling事件
        if( super.onNestedPreFling(target, velocityX, velocityY)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 滑动外层RecyclerView时，的处理
     *
     * @param lastItemTop tab到屏幕顶部的距离，是0就代表到顶了
     * @param dy          目标滑动距离， dy>0 代表向上滑
     * @param consumed
     */
    private void handleParentRecyclerViewScroll(int lastItemTop,int lastItemBottom, int dy, int[] consumed) {
        //tab上边没到顶
        if (lastItemTop > 0) {
            if (dy > 0) {
                //向上滑  1、parent 先吃最多lastItemTop 2、然后 child后吃 最多childScrolledYLeft 3、剩下的再让parent吃,吃不了没事系统自行处理
                if (lastItemTop > dy) {
                    //tab的top>想要滑动的dy,就让外部RecyclerView自行处理
                    Log.d(TAG, "没到顶parent自行处理 上滑  parent>lastItemTop："+lastItemTop+" dy:"+dy);
                } else {
                    Log.d(TAG, "没到顶parent和son处理 上滑  parent>lastItemTop："+lastItemTop+ " son>dy - lastItemTop:"+(dy - lastItemTop));
                    //tab的top<=想要滑动的dy,先滑外部RecyclerView，滑距离为lastItemTop，刚好到顶；剩下的就滑内层了。
//                    consumed[1] = dy;
                    mParentRecyclerView.scrollBy(0, lastItemTop);
//                    mChildRecyclerView.scrollBy(0, dy - lastItemTop);
                    //子recyclerview最多只能消化childScrolledY,剩下的就让外部RecyclerView自行处理
                    int childScrolledY = mChildRecyclerView.computeVerticalScrollOffset();
                    int childScrolledYLeft=mChildRecyclerView.computeVerticalScrollRange()-childScrolledY-mChildRecyclerView.computeVerticalScrollExtent();
                    if(childScrolledYLeft>dy-lastItemTop){
                        mChildRecyclerView.scrollBy(0, dy - lastItemTop);
                        Log.d(TAG+"555", "childScrolledY："+ (dy-lastItemTop)+" childScrolledYLeft:"+childScrolledYLeft);
                        consumed[1] = dy;
                    }else{
                        mChildRecyclerView.scrollBy(0,childScrolledYLeft);
                        Log.d(TAG+"555", "childScrolledY："+ childScrolledYLeft+" childScrolledYLeft:"+childScrolledYLeft);
                        consumed[1] = childScrolledYLeft+lastItemTop;
                    }
                }
            } else {
                //parent吃,吃不了没事系统自行处理 向下滑，就让外部RecyclerView自行处理
                Log.d(TAG, "没到顶parent自行处理下滑   parent>lastItemTop："+lastItemTop+" dy:"+dy);
            }
            //tab 已经超越顶部
        }else if(lastItemTop < 0){
            if (dy > 0) {
                //parent吃,吃不了没事系统自行处理 ，向上滑，就让外部RecyclerView自行处理
                Log.d(TAG, "过顶了parent自行处理上滑   parent>lastItemTop："+lastItemTop+" dy:"+dy);
                //child先吃满了
            } else {
                //向下滑   1、parent 先吃最多lastItemTop 2、然后 child后吃 最多childScrolledY 3、剩下的再让parent吃,吃不了没事系统自行处理
                if (lastItemTop < dy) {
                    //tab的top> 想要滑动的dy,就让外部RecyclerView自行处理
                    Log.d(TAG, "没到顶parent自行处理 下滑  parent>lastItemTop："+lastItemTop+" dy:"+dy);
                } else {
                    Log.d(TAG, "没到顶parent和son处理 上滑  parent>lastItemTop："+lastItemTop+ " son>dy - lastItemTop:"+(dy - lastItemTop));
                    //tab的top<=想要滑动的dy,先滑外部RecyclerView，滑距离为lastItemTop，刚好到顶；剩下的就滑内层了。
//                    consumed[1] = dy;
                    //父recycler最多吃掉lastItemTop
                    mParentRecyclerView.scrollBy(0, lastItemTop);
//                    mChildRecyclerView.scrollBy(0, dy - lastItemTop);
                    //子recyclerview最多只能消化childScrolledY,剩下的就让外部RecyclerView自行处理
                    int childScrolledY = mChildRecyclerView.computeVerticalScrollOffset();
                    int childScrolledYLeft=mChildRecyclerView.computeVerticalScrollRange()-childScrolledY-mChildRecyclerView.computeVerticalScrollExtent();
                    if(childScrolledY>lastItemTop-dy){
                        mChildRecyclerView.scrollBy(0,  dy-lastItemTop);
                        Log.d(TAG+"555", "childScrolledY："+ (dy-lastItemTop)+" childScrolledYLeft:"+childScrolledYLeft);
                        consumed[1] = dy;
                    }else{
                        mChildRecyclerView.scrollBy(0,-childScrolledY);
                        Log.d(TAG+"555", "childScrolledY："+ -childScrolledY+" childScrolledYLeft:"+childScrolledYLeft);
                        consumed[1] = lastItemTop-childScrolledY;
                    }
                }
            }
        }else {
            //已经向下滚动距离
            int childScrolledY = mChildRecyclerView.computeVerticalScrollOffset();
            int childScrolledYLeft=mChildRecyclerView.computeVerticalScrollRange()-childScrolledY-mChildRecyclerView.computeVerticalScrollExtent();
            Log.d(TAG+"111", "childScrolledY："+childScrolledY+ " childScrolledYLeft:"+childScrolledYLeft);
            if (dy > 0){
                //上滑
                if(childScrolledYLeft>dy){
                    mChildRecyclerView.scrollBy(0,  dy);
                    Log.d(TAG+"555", "childScrolledY："+dy+" childScrolledYLeft:"+childScrolledYLeft);
                    consumed[1] = dy;
                }else {
                    mChildRecyclerView.scrollBy(0,  childScrolledYLeft);
                    Log.d(TAG+"555", "childScrolledY："+childScrolledYLeft+" childScrolledYLeft:"+childScrolledYLeft);
                    consumed[1] =childScrolledYLeft;
                }
            }else {
                //下滑,child能消化childScrolledY以内的
                if(childScrolledY>-dy){
                    mChildRecyclerView.scrollBy(0,  dy);
                    Log.d(TAG+"555", "childScrolledY："+dy+" childScrolledYLeft:"+childScrolledYLeft);
                    consumed[1] = dy;
                }else{
                    mChildRecyclerView.scrollBy(0,  -childScrolledY);
                    Log.d(TAG+"555", "childScrolledY："+ -childScrolledY+" childScrolledYLeft:"+childScrolledYLeft);
                    consumed[1] = -childScrolledY;
                }
            }
        }

    }

    /**
     * 滑动内层RecyclerView时，的处理
     *
     * @param lastItemTop tab到屏幕顶部的距离，是0就代表到顶了
     * @param dy
     * @param consumed
     */
    private void handleChildRecyclerViewScroll(int lastItemTop, int dy, int[] consumed) {
        //tab上边没到顶
        if (lastItemTop != 0) {
            if (dy > 0) {
                //向上滑
                if (lastItemTop > dy) {
                    //tab的top>想要滑动的dy,外层直接消耗掉
                    mParentRecyclerView.scrollBy(0, dy);
                    consumed[1] = dy;
                } else {
                    //tab的top<=想要滑动的dy,先滑外层，消耗距离为lastItemTop，刚好到顶；剩下的就滑内层了。
                    mParentRecyclerView.scrollBy(0, lastItemTop);
                    consumed[1] = dy - lastItemTop;
                }
            } else {
                //向下滑，外层直接消耗
                mParentRecyclerView.scrollBy(0, dy);
                consumed[1] = dy;
            }
        }else {
            //tab上边到顶了
            if (dy > 0){
                //向上，内层自行处理
            }else {
                int childScrolledY = mChildRecyclerView.computeVerticalScrollOffset();
                if (childScrolledY > Math.abs(dy)) {
                    //内层已滚动的距离，大于想要滚动的距离，内层自行处理
                }else {
                    //内层已滚动的距离，小于想要滚动的距离，那么内层消费一部分，到顶后，剩的外层滑动
                    mChildRecyclerView.scrollBy(0, -childScrolledY);
                    mParentRecyclerView.scrollBy(0, -(Math.abs(dy)-childScrolledY));
                    consumed[1] = dy;
                }
            }
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //直接获取外层RecyclerView
        mParentRecyclerView = getRecyclerView(this);
        Log.i(TAG, "onFinishInflate: mParentRecyclerView=" + mParentRecyclerView);

        //关于内层RecyclerView：此时还获取不到ViewPager内fragment的RecyclerView，需要在加载ViewPager后 fragment可见时 传入
    }

    private RecyclerView getRecyclerView(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof RecyclerView) {
                if (mParentRecyclerView == null) {
                    return (RecyclerView) childAt;
                }
            }
        }
        return null;
    }

    /**
     * 传入内部RecyclerView
     *
     * @param childRecyclerView
     */
    public void setChildRecyclerView(RecyclerView childRecyclerView) {
        //当自己可见的时候赋值到嵌套滑动
        childRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mChildRecyclerView = childRecyclerView;
            }
        });
    }


    /**
     * 外层RecyclerView的最后一个item，即：tab + viewPager
     * 用于判断 滑动 临界位置
     *
     * @param lastItemView
     */
    public void setLastItem(View lastItemView) {
        //当自己可见的时候赋值到嵌套滑动
        lastItemView.post(new Runnable() {
            @Override
            public void run() {
                innerLayout = lastItemView;
            }
        });
    }
}
