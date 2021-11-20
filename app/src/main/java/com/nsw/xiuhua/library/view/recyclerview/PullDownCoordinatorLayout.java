package com.nsw.xiuhua.library.view.recyclerview;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * PullDownCoordinatorLayout 额外回调了下拉事件
 */
public class PullDownCoordinatorLayout extends CoordinatorLayout {
    public PullDownCoordinatorLayout(@NonNull Context context) {
        super(context);
    }

    public PullDownCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //判断是否滑动的临界点
    private float touchSlop;

    public PullDownCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setHandlePullDown(boolean handlePullDown) {
        isHandlePullDown = handlePullDown;
    }

    /**
     * 是否处理下拉事件
     */
    private boolean isHandlePullDown = false;
    private boolean isPulling = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isHandlePullDown) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    recordPosition(positionTemp, ev);
                    isPulling = false;
                    break;
                case MotionEvent.ACTION_UP:
                    if (dragListener != null && isPulling) {
                        isPulling = false;
                        dragListener.onPullRelease();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    //不处理点击事件并且是下滑事件
                    if (dragListener != null&&getMoveDistance(ev) > touchSlop && isPullDownEvent(ev)) {
                        isPulling = true;
                        dragListener.onShowText(ev.getRawY() - positionTemp.y);
                    }
                    break;
            }
        } else if(dragListener != null){
            isPulling = false;
            dragListener.onPullFailed();
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判断是否下拉事件
     * @param curEvent
     * @return
     */
    private boolean isPullDownEvent(MotionEvent curEvent) {
        return Math.abs(curEvent.getRawY() - positionTemp.y) > Math.abs(curEvent.getRawX() - positionTemp.x) && curEvent.getRawY() > positionTemp.y+touchSlop;
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    private DragListener dragListener;

    public interface DragListener {
        void onPullRelease();

        void onShowText(float offY);

        void onPullFailed();
    }

    private PointF positionTemp = new PointF();

    private double getMoveDistance(MotionEvent ev) {
        float offsetx = ev.getRawX() - positionTemp.x;
        float offsetY = ev.getRawY() - positionTemp.y;
        return Math.sqrt(offsetx * offsetx + offsetY * offsetY);
    }

    private void recordPosition(PointF pointF, MotionEvent ev) {
        pointF.x = ev.getRawX();
        pointF.y = ev.getRawY();
    }
}
