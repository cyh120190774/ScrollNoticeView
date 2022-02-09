package com.cyh.scrollnoticeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;

import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;
import java.util.List;

public class ScrollNoticeView  extends TextSwitcher {

    private Animation mInUp = anim(1.5f, 0);
    private Animation mOutUp = anim(0, -1.5f);

    private List<String> mDataList = new ArrayList<>();

    private int mIndex = 0;
    private int mIntervalTime = 3000;

    private int mInterval = 3000;
    private int mDuration = 1000;


    private Drawable mIcon;
    private int mIconTint = 0xff999999;
    private int mIconPadding = 0;
    private int mPaddingLeft = 0;

    private boolean mIsVisible = false;
    private boolean mIsStarted = false;
    private boolean mIsResumed = true;
    private boolean mIsRunning = false;


    private int mSpeed = -5;

    private int mDelay = 1000;


    private final TextFactory mDefaultFactory = new TextFactory();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {

                show(mIndex + 1);
                postDelayed(mRunnable, mInterval);

            }
        }
    };

    public ScrollNoticeView(Context context) {
        this(context, null);
    }

    public ScrollNoticeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context, attrs);
        setInAnimation(mInUp);
        setOutAnimation(mOutUp);
        setFactory(mDefaultFactory);
        mInUp.setDuration(mDuration);
        mOutUp.setDuration(mDuration);
    }

    @Override
    public void setFactory(ViewSwitcher.ViewFactory factory) {
        super.setFactory(factory);
    }

    private void initWithContext(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollNoticeView);

        mIcon = a.getDrawable(R.styleable.ScrollNoticeView_nsvIcon);

        mIconPadding = (int)a.getDimension(R.styleable.ScrollNoticeView_nsvIconPadding, 0);

        boolean hasIconTint = a.hasValue(R.styleable.ScrollNoticeView_nsvIconTint);

        if (hasIconTint) {
            mIconTint = a.getColor(R.styleable.ScrollNoticeView_nsvIconTint, 0xff999999);
        }

        mIntervalTime = a.getInteger(R.styleable.ScrollNoticeView_nsvInterval, 3000);
        mDuration = a.getInteger(R.styleable.ScrollNoticeView_nsvDuration, 1000);

        mSpeed = a.getInteger(R.styleable.ScrollNoticeView_nsvSpeed, -5);
        mDelay = a.getInteger(R.styleable.ScrollNoticeView_nsvDelay, 1000);


        mDefaultFactory.resolve(a);
        a.recycle();

        if (mIcon != null) {
            mPaddingLeft = getPaddingLeft();
            int realPaddingLeft = mPaddingLeft + mIconPadding + mIcon.getIntrinsicWidth();
            setPadding(realPaddingLeft, getPaddingTop(), getPaddingRight(), getPaddingBottom());

            if (hasIconTint) {
                mIcon = mIcon.mutate();
                DrawableCompat.setTint(mIcon, mIconTint);
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mIcon != null) {
            int y = (getMeasuredHeight() - mIcon.getIntrinsicWidth()) / 2;
            mIcon.setBounds(mPaddingLeft, y, mPaddingLeft + mIcon.getIntrinsicWidth(), y + mIcon.getIntrinsicHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIcon != null) {
            mIcon.draw(canvas);
        }
    }

    public int getIndex() {
        return mIndex;
    }

    public void start(List<String> list) {
        mDataList = list;
        if (mDataList == null || mDataList.size() < 1) {
            mIsStarted = false;
            update();
        } else {
            mIsStarted = true;
            update();
            show(0);
        }
    }



    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mIsVisible = visibility == VISIBLE;
        update();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsResumed = false;
                update();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsResumed = true;
                update();
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    private void update() {
        boolean running = mIsVisible && mIsResumed && mIsStarted;
        if (running != mIsRunning) {
            if (running) {
                postDelayed(mRunnable, mInterval);
            } else {
                removeCallbacks(mRunnable);
            }
            mIsRunning = running;
        }

    }

    private void show(int index) {
        mIndex = index % mDataList.size();
        setText(mDataList.get(mIndex));
    }

    @Override
    public void setText(CharSequence text) {

        final ScrollTextView t = (ScrollTextView) getNextView();

        Paint paint = t.getPaint();

        float wm = paint.measureText((String) text);

        if (wm > getMeasuredWidth()){
            mInterval = (int)(mIntervalTime + mDuration + mDelay + Math.abs( wm/mSpeed * 30));

        }else {
            mInterval = mIntervalTime;
        }

        t.setText(text);
        t.startScroll(mDelay);

        showNext();


    }

    private Animation anim(float from, float to) {
        final TranslateAnimation anim = new TranslateAnimation(0, 0f, 0, 0f, Animation.RELATIVE_TO_PARENT, from, Animation.RELATIVE_TO_PARENT, to);
        anim.setDuration(mDuration);
        anim.setFillAfter(false);
        anim.setInterpolator(new LinearInterpolator());
        return anim;
    }

    class TextFactory implements ViewSwitcher.ViewFactory {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();

        float size = dp2px(14);
        int color = 1;
        int lines = 1;
        int speed = -5;


        void resolve(TypedArray ta) {
            lines = ta.getInteger(R.styleable.ScrollNoticeView_nsvTextMaxLines, lines);
            size = ta.getDimension(R.styleable.ScrollNoticeView_nsvTextSize, size);
            color = ta.getColor(R.styleable.ScrollNoticeView_nsvTextColor, color);
            speed =  ta.getInteger(R.styleable.ScrollNoticeView_nsvSpeed, speed);
        }

        private int dp2px(float dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
        }
        @Override
        public View makeView() {

            ScrollTextView tv = new ScrollTextView(getContext());

            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

            tv.setMaxLines(lines);

            tv.setSpeed(speed);

            if (color != 1) {
                tv.setTextColor(color);
            }

            tv.setGravity(Gravity.CENTER);

            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));


            return tv;
        }
    }
}
