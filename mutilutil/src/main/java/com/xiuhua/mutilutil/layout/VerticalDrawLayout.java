package com.xiuhua.mutilutil.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * Creator      : nishengwen
 * Email        : nishengwen@renrenche.com
 * CreateTime   : 2020/6/21:17
 */
public class VerticalDrawLayout extends LinearLayout {
    private View view_top;
    private float downX;
    private float downY;
    private boolean opened = false;//状态是否开闭
    private int ambit_scroll = 100;//滑动界限，开闭

    public VerticalDrawLayout(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public VerticalDrawLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public VerticalDrawLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        // 当xml解析完成时的回调
        super.onFinishInflate();
        view_top = getChildAt(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX();
                float moveY = event.getRawY();
                // 竖直滑动
                //竖直滚动了
                if (Math.abs(moveY - downY) > 0.5 * Math.abs(moveX - downX)) {
                    //上面隐藏还想上滑
                    if (!opened && moveY - downY < 0) {
                        return false;
                    }
                    //上面显示并且下滑
                    if (opened && moveY - downY > 0) {
                        return false;
                    }
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = event.getRawY() - downY;
                if (getY() + dy > 0 || getY() + dy < -getHeight_top()) {
                    return false;
                } else {
                    setY(getY() + dy);
                    downY = event.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
                float totalY = Math.abs(getY());
                if (opened) {
                    open(!(totalY > ambit_scroll || totalY > getHeight_top() / 3), getY());
                } else {
                    open(totalY < getHeight_top() - ambit_scroll || totalY < getHeight_top() * 2 / 3, getY());
                }
                break;
        }
        // 消费掉
        return true;
    }

    public void open() {
        if (!opened)
            open(true, -getHeight_top());
    }

    public void close() {
        if (opened)
            open(false, getY());
    }

    /**
     * 开闭抽屉
     *
     * @param open
     */
    public void open(final boolean open, float startY) {
        float endY = open ? 0 : -getHeight_top();
        this.opened = open;
        PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat("y", startY, endY);
        ValueAnimator valueAnimator = ObjectAnimator.ofPropertyValuesHolder(this, pvh);
        valueAnimator.setDuration(500);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (closeOpenLister != null) {
                    if (open) {
                        closeOpenLister.open();
                    }else{
                        closeOpenLister.close();
                    }
                }
            }
        });
        // 模拟数据变化
        valueAnimator.start();
    }

    public int getHeight_top() {
        return view_top.getMeasuredHeight();
    }

    public boolean isOpened() {
        return opened;
    }

    public void setCloseOpenLister(CloseOpenLister closeOpenLister) {
        this.closeOpenLister = closeOpenLister;
    }

    private CloseOpenLister closeOpenLister;

    public interface CloseOpenLister {
        void open();

        void close();
    }
}
