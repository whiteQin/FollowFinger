package com.followfingerlibrary;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by qinkang on 2018/5/21.
 */
//跟随手指移动view，并且有边界
public class FingerFollowView extends ViewGroup {

    private int screenWidth;
    private int screenHeight;
    private Context context;
    private int mMeasuredHeight;
    private int mMeasuredWidth;

    public FingerFollowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FingerFollowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        //获取屏幕高度（总高度-状态栏高度--如果有toolbar还要减去toolbar高度）
        screenHeight = displayMetrics.heightPixels - getStatusBarHeight();
    }

    public FingerFollowView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        mMeasuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        //将所有的子View进行测量，这会触发每个子View的onMeasure函数
        //注意要与measureChild区分，measureChild是对单个view进行测量
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childCount = getChildCount();
        if (childCount == 0) {//如果没有子View,当前ViewGroup没有存在的意义，不用占用空间
            setMeasuredDimension(0, 0);
        } else {
            //如果宽高都是包裹内容
            if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
                //我们将高度设置为所有子View的高度相加，宽度设为子View中最大的宽度
                int height = getTotleHeight();
                int width = getMaxChildWidth();
                setMeasuredDimension(width, height);
            } else if (heightMode == MeasureSpec.AT_MOST) {//如果只有高度是包裹内容
                //宽度设置为ViewGroup自己的测量宽度，高度设置为所有子View的高度总和
                setMeasuredDimension(widthSize, getTotleHeight());
            } else if (widthMode == MeasureSpec.AT_MOST) {//如果只有宽度是包裹内容
                //宽度设置为子View中宽度最大的值，高度设置为ViewGroup自己的测量值
                setMeasuredDimension(getMaxChildWidth(), heightSize);
            } else {
                setMeasuredDimension(widthSize, heightSize);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int curHeight = 0;
        int curWidth = 0;
        //将子View逐个摆放
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int height = child.getMeasuredHeight();
            int width = child.getMeasuredWidth();
            //摆放子View，参数分别是子View矩形区域的左、上、右、下边
            child.layout(curWidth, curHeight, curWidth + width, curHeight + height);
            //水平布局的写法
            //curWidth+=width;
            //垂直布局的写法
            curHeight += height;
        }
    }


    private int lastX, lastY;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) motionEvent.getRawX();
                lastY = (int) motionEvent.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) motionEvent.getRawX() - lastX;
                int dy = (int) motionEvent.getRawY() - lastY;
                int left = this.getLeft() + dx;
                int top = this.getTop() + dy;
                int right = this.getRight() + dx;
                int bottom = this.getBottom() + dy;
                if (left < 0) {
                    left = 0;
                    right = getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = getHeight();
                }
                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - getHeight();
                }
                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - getWidth();
                }
                MarginLayoutParams marginParams = (MarginLayoutParams) getLayoutParams();
                marginParams.height = mMeasuredHeight;
                marginParams.width = mMeasuredWidth;
                marginParams.setMargins(left, top, screenWidth - right, screenHeight - bottom);
                setLayoutParams(marginParams);
                lastX = (int) motionEvent.getRawX();
                lastY = (int) motionEvent.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_HOVER_ENTER:
                break;
        }
        return true;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /***
     * 获取子View中宽度最大的值
     */
    private int getMaxChildWidth() {
        int childCount = getChildCount();
        int maxWidth = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getMeasuredWidth() > maxWidth)
                maxWidth = childView.getMeasuredWidth();

        }

        return maxWidth;
    }

    /***
     * 将所有子View的高度相加
     **/
    private int getTotleHeight() {
        int childCount = getChildCount();
        int height = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            height += childView.getMeasuredHeight();
        }
        return height;
    }

}
