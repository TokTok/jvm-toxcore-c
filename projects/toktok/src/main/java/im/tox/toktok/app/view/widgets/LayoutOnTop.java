package im.tox.toktok.app.view.widgets;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import im.tox.toktok.R;

public class LayoutOnTop extends ViewGroup {

    private float mDragOffset;
    private float mInitialMotionY;
    private int mDragRange = 0;
    private int mScrollMark = -1;
    private int mTop = -1;
    private int mHeight = -1;
    private int mWight = -1;
    private boolean childScrollActive = false;

    private View mChild = null;
    private ViewDragHelper mDragHelper = null;
    private View mScroll = null;

    private LayoutOnTopCallback callback = null;


    public LayoutOnTop(Context context) {
        this(context, null);
    }

    public LayoutOnTop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayoutOnTop(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());

    }

    public boolean smoothSlideTo(float slideOffset) {

        int y = (int) slideOffset * mDragRange;

        if (mDragHelper.smoothSlideViewTo(mChild, 0, y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        float y = ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                if (mInitialMotionY - y < 0 && mScrollMark == mScroll.getBottom()) {
                    childScrollActive = false;
                    Log.d("TokTok","Onlock");
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mInitialMotionY - y < 0 && mScrollMark == mScroll.getBottom()) {
                    childScrollActive = false;
                    return true;
                }
                break;
            }
        }

        return mDragHelper.shouldInterceptTouchEvent(ev) && !childScrollActive;

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            float dy = mInitialMotionY - ev.getY();

            switch (ev.getAction()) {

                case MotionEvent.ACTION_MOVE: {
                    if (dy > 0 && mTop == 0) {
                        childScrollActive = true;
                        return false;
                    }
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    if (dy > 0) {
                        childScrollActive = true;
                        mScrollMark = mScroll.getBottom();
                        smoothSlideTo(0);
                        return false;
                    } else if (dy < 0) {

                        Log.d("TokTok","Down");

                        smoothSlideTo(1);

                        Handler handler = new Handler();

                        final Runnable r = new Runnable() {
                            public void run() {
                                callback.onClose();
                            }
                        };
                        handler.postDelayed(r, 500);
                    }
                    break;
                }
            }
            mDragHelper.processTouchEvent(ev);
            return true;

        } catch (Exception ex) {
            return false;
        }

    }

    public boolean dispatchTouchEvent(MotionEvent ev) {


        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mInitialMotionY = ev.getY();
        }

        return super.dispatchTouchEvent(ev);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        mHeight = maxHeight;
        mWight = maxWidth;
        mDragRange = maxHeight;

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0), resolveSizeAndState(maxHeight, heightMeasureSpec, 0));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (changed) {
            mChild = getChildAt(0);
            mTop = mHeight / 2;
            mDragOffset = (float) mTop / mDragRange;
        }
        mChild.layout(l, mTop, r, mTop + mHeight);

    }

    public void setScrollView(View v) {
        mScroll = v;
    }

    public void setLayoutOnTopCallback(LayoutOnTopCallback callback) {
        this.callback = callback;
    }

    public interface LayoutOnTopCallback {
        void onClose();
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mChild || child == mScroll;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mTop = top;
            mDragOffset = (float) top / mDragRange;
            requestLayout();
        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top = getPaddingTop();
            if (yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
                top += mDragRange;
            }
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mDragRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = 0;
            final int bottomBound = getHeight();
            return Math.min(Math.max(top, topBound), bottomBound);
        }

    }

}
