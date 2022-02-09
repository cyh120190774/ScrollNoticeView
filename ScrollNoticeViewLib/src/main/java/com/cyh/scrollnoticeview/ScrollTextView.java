package com.cyh.scrollnoticeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScrollTextView extends AppCompatTextView {


    private String mText = "";

    private int mOffsetX = 0;

    private Rect mRect = new Rect();

    private ScheduledExecutorService executorService;
    /**
     * 速度，负数左移，正数右移。
     */
    private int mSpeed = -5;

    
    private int width;

    

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
    }


    public void startScroll(int delay) {

        mText = getText().toString();

        mOffsetX = 0;

        if (TextUtils.isEmpty(mText)) {
            return;
        }

        if (executorService != null) {
            executorService.shutdown();
        }


        if (mSpeed < 0) {
            //左移
            mOffsetX = 0;
        } else if (mSpeed > 0) {
            //右移
            mOffsetX = width - mRect.right;
        }
        executorService = new ScheduledThreadPoolExecutor(1);

        executorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                //如果View能容下所有文字，直接返回
                if (mRect.right < width) {
                    return;
                }

                if (mSpeed < 0) {
                    //左移
                    if (mOffsetX < -mRect.right + width) {
                        mOffsetX = -mRect.right + width;
                        executorService.shutdown();
                        return;
                    }
                } else if (mSpeed > 0) {
                    //右移
                    if (mOffsetX > width) {
                        mOffsetX = -mRect.right;
                    }
                } else {
                    executorService.shutdown();
                    return;
                }
                mOffsetX += mSpeed;
                
                postInvalidate();
            }
        }, delay, 50, TimeUnit.MILLISECONDS);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        //获取文本区域大小，保存在mRect中。
        textPaint.getTextBounds(mText, 0, mText.length(), mRect);
        float mTextCenterVerticalToBaseLine = (-textPaint.ascent() + textPaint.descent()) / 2 - textPaint.descent();
        int width = getWidth();
        if (mRect.right < width) {
            canvas.drawText(mText, 0, getHeight() / 2 + mTextCenterVerticalToBaseLine, textPaint);
        } else {
            canvas.drawText(mText, mOffsetX, getHeight() / 2 + mTextCenterVerticalToBaseLine, textPaint);
        }
    }

    /**
     * 视图移除时销毁任务和定时器
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * 设置速度，每次更新的像素偏移大小
     *
     * @param speed >0:向右；<0 向左
     */
    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

}
