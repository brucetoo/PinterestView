package com.brucetoo.pinterestview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Bruce Too
 * On 10/2/15.
 * At 11:10
 */
public class PinterestView extends ViewGroup {

    private final static String TAG = "PinterestView";
    private int mChildSize;

    public static final float DEFAULT_FROM_DEGREES = 270.0f;

    public static final float DEFAULT_TO_DEGREES = 360.0f;

    private float mFromDegrees = DEFAULT_FROM_DEGREES;

    private float mToDegrees = DEFAULT_TO_DEGREES;

    private static final int DEFAULT_RADIUS = 250;//px

    private int mRadius;

    private Context mContext;

    private boolean mExpanded = true;

    private float mCenterX;
    private float mCenterY;

    private Handler mHandler = new Handler();
    //handle long press event
    private Runnable mLongPressRunnable = new Runnable() {
        @Override
        public void run() {
            PinterestView.this.setVisibility(View.VISIBLE);
            switchState(true);
            Log.i(TAG, "LongPressLongPress");
        }
    };

    public PinterestView(Context context) {
        super(context);
        this.mContext = context;
    }

    public PinterestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mRadius = DEFAULT_RADIUS;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PinterestView, 0, 0);
            mFromDegrees = a.getFloat(R.styleable.PinterestView_fromDegrees, DEFAULT_FROM_DEGREES);
            mToDegrees = a.getFloat(R.styleable.PinterestView_toDegrees, DEFAULT_TO_DEGREES);
            mChildSize = Math.max(a.getDimensionPixelSize(R.styleable.PinterestView_childSize, 0), 0);
            a.recycle();
        }

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    mCenterX = event.getRawX();
                    mCenterY = event.getRawY();
                    mHandler.postDelayed(mLongPressRunnable, 1200);
                } else {
                    mHandler.removeCallbacks(mLongPressRunnable);
                }
                handleTouchEvent(event);
                return true;
            }
        });

    }

    private void handleTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                Rect bounds;
                int[] location = new int[2];
                getChildAt(1).getLocationOnScreen(location);
                bounds = new Rect(location[0],location[1],location[0]+getChildAt(1).getWidth(),location[1]+getChildAt(1).getHeight());
                boolean contains = bounds.contains((int) event.getRawX(), (int) event.getRawY());
                Log.i(TAG,"bounds:"+bounds);
                if(contains){
                    Log.i(TAG, "contains");
                    ((CircleImageView)getChildAt(1)).setFillColor(mContext.getResources().getColor(R.color.colorPrimary));
                }
                Log.i(TAG, "ACTION_MOVE-----" + bounds);
                Log.i(TAG, "ACTION_MOVE-----" + event.getRawX() + "," + event.getRawY());
                Log.i(TAG, "ACTION_MOVE-----" + event.getX() + "," + event.getX());
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP-----" + event.getX());
                ((CircleImageView)getChildAt(1)).setFillColor(mContext.getResources().getColor(R.color.colorAccent));
                switchState(true);
                break;
        }
    }

    private Rect getChildDisPlayBounds(View view){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Rect(location[0],location[1],location[0]+getChildAt(1).getWidth(),location[1]+getChildAt(1).getHeight());
    }

    private void bindChildAnimation(final View child) {
        Rect childRect = getChildDisPlayBounds(child);
        AnimatorSet childAnim;
        if(mExpanded){
            childAnim = new AnimatorSet();
            ObjectAnimator transX = ObjectAnimator.ofFloat(child,"translationX",mCenterX - childRect.centerX(), 0);
            ObjectAnimator transY = ObjectAnimator.ofFloat(child,"translationY",mCenterY - childRect.centerY(),0);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(child,"scaleX",(int)0.5,1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(child,"scaleY",(int)0.5,1);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(child,"alpha",(int)0.5,1);
            childAnim.playTogether(transX, transY, scaleX, scaleY,alpha);
            childAnim.setDuration(300);
            childAnim.setInterpolator(new AccelerateInterpolator());
            childAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recoverChildView();
                }
            });
            childAnim.start();
        }else {
            childAnim = new AnimatorSet();
            ObjectAnimator transX = ObjectAnimator.ofFloat(child,"translationX",0,mCenterX - childRect.centerX());
            ObjectAnimator transY = ObjectAnimator.ofFloat(child,"translationY",0,mCenterY - childRect.centerY());
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(child,"scaleX",1,(int)0.5);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(child,"scaleY",1,(int)0.5);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(child,"alpha",1,(int)0.5);
            childAnim.playTogether(transX, transY, scaleX, scaleY,alpha);
            childAnim.setDuration(300);
            childAnim.setInterpolator(new AccelerateInterpolator());
            childAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recoverChildView();
                    PinterestView.this.setVisibility(GONE);
                }
            });
            childAnim.start();
        }
    }


    /**
     * center view animation
     * @param child
     */
    private void bindCenterViewAnimation(View child) {
        AnimatorSet childAnim;
        if(mExpanded){
            childAnim = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(child,"scaleX",(int)0.5,1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(child,"scaleY",(int)0.5,1);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(child,"alpha",(int)0.5,1);
            childAnim.playTogether(scaleX, scaleY,alpha);
            childAnim.setDuration(300);
            childAnim.setInterpolator(new AccelerateInterpolator());
            childAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recoverChildView();
                }
            });
            childAnim.start();
        }else {
            childAnim = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(child,"scaleX",1,(int)0.5);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(child,"scaleY",1,(int)0.5);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(child,"alpha",1,(int)0.5);
            childAnim.playTogether(scaleX, scaleY,alpha);
            childAnim.setDuration(300);
            childAnim.setInterpolator(new AccelerateInterpolator());
            childAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recoverChildView();
                    PinterestView.this.setVisibility(GONE);
                }
            });
            childAnim.start();
        }

    }

    private void recoverChildView() {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).animate().translationX(1).translationY(1).scaleX(1).scaleX(1).start();
        }
        requestLayout();
    }

    private static Rect computeChildFrame(final float centerX, final float centerY, final int radius, final float degrees,
                                          final int size) {

        final double childCenterX = centerX + radius * Math.cos(Math.toRadians(degrees));
        final double childCenterY = centerY + radius * Math.sin(Math.toRadians(degrees));

        return new Rect((int) (childCenterX - size / 2), (int) (childCenterY - size / 2),
                (int) (childCenterX + size / 2), (int) (childCenterY + size / 2));
    }


    public void switchState(final boolean showAnimation) {
        if (showAnimation) {
            final int childCount = getChildCount();
            //other view
            for (int i = 1; i < childCount; i++) {
                bindChildAnimation(getChildAt(i));
            }
            //center view
            bindCenterViewAnimation(getChildAt(0));
        }

        mExpanded = !mExpanded;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mContext.getResources().getDisplayMetrics().widthPixels, mContext.getResources().getDisplayMetrics().heightPixels);

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(mChildSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mChildSize, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final float centerX;
        final float centerY;
        centerX = mCenterX == 0 ? getWidth() / 2 : mCenterX;
        centerY = mCenterY == 0 ? getHeight() / 2 : mCenterY - mRadius;

        final int childCount = getChildCount();
        //single degrees
        final float perDegrees = (mToDegrees - mFromDegrees) / (childCount - 1);

        float degrees = mFromDegrees;
        //add centerView
        Rect centerRect = computeChildFrame(centerX, centerY, 0, perDegrees, mChildSize);
        getChildAt(0).layout(centerRect.left, centerRect.top, centerRect.right, centerRect.bottom);
        degrees += perDegrees;
        //add other view
        for (int i = 1; i < childCount; i++) {
            Rect frame = computeChildFrame(centerX, centerY, mRadius, degrees, mChildSize);
            if(i == 1){
                Log.i("computeChildFrame:",frame+"");
            }
            degrees += perDegrees;
            getChildAt(i).layout(frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void setArc(float fromDegrees, float toDegrees) {
        if (mFromDegrees == fromDegrees && mToDegrees == toDegrees) {
            return;
        }

        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;

        requestLayout();
    }

    /**
     * size (dp)
     *
     * @param size //dp
     */
    public void setChildSize(int size) {
        if (mChildSize == size || size < 0) {
            return;
        }
        //convert to px
        mChildSize = dp2px(size);
        requestLayout();
    }


    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

}