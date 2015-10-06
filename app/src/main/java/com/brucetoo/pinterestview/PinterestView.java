package com.brucetoo.pinterestview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by Bruce Too
 * On 10/2/15.
 * At 11:10
 */
public class PinterestView extends ViewGroup {

    private final static String TAG = "PinterestView";

    private final static int ANIMATION_DURATION = 200;

    private final static int LONG_PRESS_DURATION = 500;

    private int mChildSize;

    public static final float DEFAULT_FROM_DEGREES = 270.0f;

    public static final float DEFAULT_TO_DEGREES = 360.0f;

    public static final int DEFAULT_CHILD_SIZE = 44;
    
    public static final int DEFAULT_RECT_MARGIN_SIZE = 100;

    private float mFromDegrees = DEFAULT_FROM_DEGREES;

    private float mToDegrees = DEFAULT_TO_DEGREES;

    private static final int DEFAULT_RADIUS = 220;//px

    private int mRadius;

    private Context mContext;

    private boolean mExpanded = false;

    private SparseArray<Rect> mChildRects = new SparseArray<>();

    private long mPressDuration;

    private float mCenterX;
    private float mCenterY;

    private PinterestView.PinMenuClickListener mPinMenuClickListener;

    private PopupWindow mPopTips;

    private Handler mHandler = new Handler();
    //handle long press event
    private Runnable mLongPressRunnable = new Runnable() {
        @Override
        public void run() {
            PinterestView.this.setVisibility(View.VISIBLE);
            switchState(true);
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
        createTipsPopWindow(context);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PinterestView, 0, 0);
            mFromDegrees = a.getFloat(R.styleable.PinterestView_fromDegrees, DEFAULT_FROM_DEGREES);
            mToDegrees = a.getFloat(R.styleable.PinterestView_toDegrees, DEFAULT_TO_DEGREES);
            mChildSize = a.getDimensionPixelSize(R.styleable.PinterestView_childSize, DEFAULT_CHILD_SIZE);
            a.recycle();
        }

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    mCenterX = event.getRawX() - mChildSize / 3;
                    mCenterY = event.getRawY() - mChildSize / 3;
                    confirmDegreeRangeByCenter(mCenterX, mCenterY);
                    mHandler.postDelayed(mLongPressRunnable, LONG_PRESS_DURATION);
                    mPressDuration = System.currentTimeMillis();
                } else {
                    mHandler.removeCallbacks(mLongPressRunnable);
                }
                handleTouchEvent(event);
                return true;
            }
        });

    }

    private void createTipsPopWindow(Context context) {
        TextView tips = new TextView(context);
        tips.setTypeface(null, Typeface.BOLD);
        tips.setTextSize(15);
        tips.setTextColor(Color.parseColor("#ffffff"));
        tips.setBackgroundResource(R.drawable.shape_child_item);
        mPopTips = new PopupWindow(tips, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    }

    private void confirmDegreeRangeByCenter(float centerX, float centerY) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        //left-top (-60,90)
        Rect leftTopRect = new Rect(0,0,dp2px(DEFAULT_RECT_MARGIN_SIZE),dp2px(DEFAULT_RECT_MARGIN_SIZE));

        //top (-10,140)
        Rect topRect = new Rect(dp2px(DEFAULT_RECT_MARGIN_SIZE),0,metrics.widthPixels - dp2px(DEFAULT_RECT_MARGIN_SIZE),dp2px(DEFAULT_RECT_MARGIN_SIZE));

        //right-top  50,200
        Rect rightTopRect = new Rect(metrics.widthPixels-dp2px(DEFAULT_RECT_MARGIN_SIZE),0,metrics.widthPixels,dp2px(DEFAULT_RECT_MARGIN_SIZE));

        //left -100,50
        Rect leftRect = new Rect(0,dp2px(DEFAULT_RECT_MARGIN_SIZE),dp2px(DEFAULT_RECT_MARGIN_SIZE),metrics.heightPixels-dp2px(DEFAULT_RECT_MARGIN_SIZE));

        //right 80,230
        Rect rightRect = new Rect(metrics.widthPixels-dp2px(DEFAULT_RECT_MARGIN_SIZE),dp2px(DEFAULT_RECT_MARGIN_SIZE),metrics.widthPixels,metrics.heightPixels-dp2px(DEFAULT_RECT_MARGIN_SIZE));

        //left_bottom -140,10
        Rect leftBottomRect = new Rect(0,metrics.heightPixels-dp2px(DEFAULT_RECT_MARGIN_SIZE),dp2px(DEFAULT_RECT_MARGIN_SIZE),metrics.heightPixels);
        //bottom  170,320
        Rect bottomRect = new Rect(dp2px(DEFAULT_RECT_MARGIN_SIZE),metrics.heightPixels-dp2px(DEFAULT_RECT_MARGIN_SIZE),metrics.widthPixels-dp2px(DEFAULT_RECT_MARGIN_SIZE),metrics.heightPixels);
        //right_bottom 150,300 and center
        Rect rightBottomRect = new Rect(metrics.widthPixels-dp2px(DEFAULT_RECT_MARGIN_SIZE),metrics.heightPixels-dp2px(DEFAULT_RECT_MARGIN_SIZE),metrics.widthPixels,metrics.heightPixels);
        Rect centerRect = new Rect(dp2px(DEFAULT_RECT_MARGIN_SIZE),dp2px(DEFAULT_RECT_MARGIN_SIZE),metrics.widthPixels-dp2px(DEFAULT_RECT_MARGIN_SIZE),metrics.heightPixels-dp2px(DEFAULT_RECT_MARGIN_SIZE));

        if (leftTopRect.contains((int)centerX,(int)centerY)){
            mFromDegrees = -60;
            mToDegrees = 90;
            Log.i(TAG,"leftTopRect");
        }else if (topRect.contains((int)centerX,(int)centerY)){
            mFromDegrees = -10;
            mToDegrees = 150;
            Log.i(TAG,"topRect");
        }else if (rightTopRect.contains((int)centerX,(int)centerY)){
            mFromDegrees = 50;
            mToDegrees = 200;
            Log.i(TAG,"rightTopRect");
        }else if (leftRect.contains((int)centerX,(int)centerY)){
            mFromDegrees = -100;
            mToDegrees = 50;
            Log.i(TAG,"leftRect");
        }else if (rightRect.contains((int)centerX,(int)centerY)){
            mFromDegrees = 80;
            mToDegrees = 230;
            Log.i(TAG,"rightRect");
        }else if (leftBottomRect.contains((int)centerX,(int)centerY)){
            mFromDegrees = -140;
            mToDegrees = 10;
            Log.i(TAG,"leftBottomRect");
        }else if (bottomRect.contains((int)centerX,(int)centerY)){
            mFromDegrees = 170;
            mToDegrees = 320;
            Log.i(TAG,"bottomRect");
        }else if (rightBottomRect.contains((int)centerX,(int)centerY) || centerRect.contains((int)centerX,(int)centerY)){
            mFromDegrees = 150;
            mToDegrees = 300;
            Log.i(TAG,"rightBottomRect");
        }
        requestLayout();

    }

    private void handleTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //only listen ACTION_MOVE when PinterestView is visible
                if (PinterestView.this.getVisibility() == VISIBLE) {
                    for (int i = 0; i < mChildRects.size(); i++) {
                        Rect rect = mChildRects.valueAt(i);
                        boolean contains = rect.contains((int) event.getRawX(), (int) event.getRawY());
                        if (contains) {
                            ((CircleImageView) getChildAt(mChildRects.keyAt(i))).setFillColor(mContext.getResources().getColor(R.color.colorPrimary));
                            getChildAt(mChildRects.keyAt(i)).setScaleX(1.3f);
                            getChildAt(mChildRects.keyAt(i)).setScaleY(1.3f);
                            if(!mPopTips.isShowing())
                              mPopTips.showAsDropDown(getChildAt(mChildRects.keyAt(i)), 0, -mChildSize * 2);
                            ((TextView)mPopTips.getContentView()).setText((String) getChildAt(mChildRects.keyAt(i)).getTag());

                            for (int j = 0; j < mChildRects.size(); j++) {
                                if (j != i) {
                                    Log.i(TAG, "recover position:" + (String) getChildAt(mChildRects.keyAt(j)).getTag());
                                    ((CircleImageView) getChildAt(mChildRects.keyAt(j))).setFillColor(mContext.getResources().getColor(R.color.colorAccent));
                                    getChildAt(mChildRects.keyAt(j)).setScaleX(1);
                                    getChildAt(mChildRects.keyAt(j)).setScaleY(1);
                                }
                            }
                            break;
                        } else {
                            mPopTips.dismiss();
                            ((CircleImageView) getChildAt(mChildRects.keyAt(i))).setFillColor(mContext.getResources().getColor(R.color.colorAccent));
                            getChildAt(mChildRects.keyAt(i)).setScaleX(1);
                            getChildAt(mChildRects.keyAt(i)).setScaleY(1);
                        }


                    }

                }
                break;
//          case MotionEvent.ACTION_CANCEL://why this will cause RecyclerView get scroll listener??
            case MotionEvent.ACTION_UP:
                mPressDuration = System.currentTimeMillis() - mPressDuration;
                if (mPressDuration >= LONG_PRESS_DURATION) { //handle long press
                    if (PinterestView.this.getVisibility() == VISIBLE) {
                        mPopTips.dismiss();
                        Log.i(TAG, "ACTION_UP--CHOOSE ONE---");
                        for (int i = 0; i < mChildRects.size(); i++) {
                            Rect rect = mChildRects.valueAt(i);
                            boolean contains = rect.contains((int) event.getRawX(), (int) event.getRawY());
                            if (contains) {
                                mPinMenuClickListener.onMenuItemClick(mChildRects.keyAt(i));
                                getChildAt(mChildRects.keyAt(i)).setScaleX(1);
                                getChildAt(mChildRects.keyAt(i)).setScaleY(1);
                            }
                        }
                        switchState(true);
                    }
                } else { //handle single press
                    Log.i(TAG, "ACTION_UP--single press---" + event.getX());
                    mPinMenuClickListener.onPreViewClick();
                }
                break;
        }
    }

    private Rect getChildDisPlayBounds(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        //scale the rect range
        Rect rect = new Rect();
        rect.left = location[0]+view.getWidth()/4;
        rect.top = location[1]-view.getHeight()/4;
        rect.right = location[0]+view.getWidth()*3/4;
        rect.bottom = location[1]-view.getHeight()*3/4;
        return new Rect(location[0], location[1], location[0] + getChildAt(1).getWidth(), location[1] + getChildAt(1).getHeight());
    }

    private void bindChildAnimation(final View child, final int position) {
        //in case when init in,child.getWidth = 0 cause get wrong rect
        child.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect childRect = getChildDisPlayBounds(child);
                mChildRects.put(position, childRect);
                if (mExpanded) {
                    expandAnimation(child, childRect);
                }
            }
        });
        Rect childRect = getChildDisPlayBounds(child);
        if (!mExpanded) {
            collapseAnimation(child, childRect);
        }
    }

    private void collapseAnimation(View child, Rect childRect) {
        AnimatorSet childAnim = new AnimatorSet();
        ObjectAnimator transX = ObjectAnimator.ofFloat(child, "translationX", 0, (mCenterX - childRect.exactCenterX()) / 2);
        ObjectAnimator transY = ObjectAnimator.ofFloat(child, "translationY", 0, (mCenterY - childRect.exactCenterY()) / 2);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(child, "alpha", 1, (int) 0.5);
        childAnim.playTogether(transX, transY, alpha);
        childAnim.setDuration(ANIMATION_DURATION);
        childAnim.setInterpolator(new AccelerateInterpolator());
        childAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
//                PinterestView.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                recoverChildView();
                PinterestView.this.setVisibility(GONE);
            }
        });
        childAnim.start();
    }

    private void expandAnimation(View child, Rect childRect) {
        AnimatorSet childAnim = new AnimatorSet();
        ObjectAnimator transX = ObjectAnimator.ofFloat(child, "translationX", (mCenterX - childRect.exactCenterX()) / 2, 0);
        ObjectAnimator transY = ObjectAnimator.ofFloat(child, "translationY", (mCenterY - childRect.exactCenterY()) / 2, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(child, "alpha", (int) 0.5, 1);
        childAnim.playTogether(transX, transY, alpha);
        childAnim.setDuration(ANIMATION_DURATION);
        childAnim.setInterpolator(new AccelerateInterpolator());
        childAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                recoverChildView();
            }
        });
        childAnim.start();
    }


    /**
     * center view animation
     *
     * @param child
     */
    private void bindCenterViewAnimation(View child) {
        AnimatorSet childAnim;
        if (mExpanded) {
            childAnim = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(child, "scaleX", (int) 0.5, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(child, "scaleY", (int) 0.5, 1);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(child, "alpha", (int) 0.5, 1);
            childAnim.playTogether(scaleX, scaleY, alpha);
            childAnim.setDuration(ANIMATION_DURATION);
            childAnim.setInterpolator(new AccelerateInterpolator());
            childAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recoverChildView();
                }
            });
            childAnim.start();
        } else {
            childAnim = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(child, "scaleX", 1, (int) 0.5);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(child, "scaleY", 1, (int) 0.5);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(child, "alpha", 1, (int) 0.5);
            childAnim.playTogether(scaleX, scaleY, alpha);
            childAnim.setDuration(ANIMATION_DURATION);
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
            getChildAt(i).animate().setDuration(100).translationX(0).translationY(0).scaleX(1).scaleX(1).start();
        }
    }

    private static Rect computeChildFrame(final float centerX, final float centerY, final int radius, final float degrees,
                                          final int size) {

        final double childCenterX = centerX + radius * Math.cos(Math.toRadians(degrees));
        final double childCenterY = centerY + radius * Math.sin(Math.toRadians(degrees));

        return new Rect((int) (childCenterX - size / 2), (int) (childCenterY - size / 2),
                (int) (childCenterX + size / 2), (int) (childCenterY + size / 2));
    }


    public void switchState(final boolean showAnimation) {
        mExpanded = !mExpanded;
        final int childCount = getChildCount();
        if (showAnimation) {
            mChildRects.clear();
            //other view
            for (int i = 1; i < childCount; i++) {
                ((CircleImageView) getChildAt(i)).setFillColor(mContext.getResources().getColor(R.color.colorAccent));
                bindChildAnimation(getChildAt(i), i);
            }
            //center view
            bindCenterViewAnimation(getChildAt(0));
        }

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
            if (i == 1) {
                Log.i("computeChildFrame:", frame + "");
            }
            degrees += perDegrees;
            getChildAt(i).layout(frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    /**
     * set Pinterest Menu show degrees range
     * @param fromDegrees
     * @param toDegrees
     */
    public void setArc(float fromDegrees, float toDegrees) {
        if (mFromDegrees == fromDegrees && mToDegrees == toDegrees) {
            return;
        }

        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        requestLayout();
    }

    /**
     * addView to PinterestView
     *
     * @param size        view size
     * @param centerView
     * @param normalViews
     */
    public void addShowView(int size, View centerView, View... normalViews) {
        this.setChildSize(size);
        addView(centerView, 0);
        for (int i = 0; i < normalViews.length; i++) {
            addView(normalViews[i]);
        }
    }

    /**
     * size (dp)
     * default all item child size are same
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

    /**
     * set Pinterest click listener
     * @param pinMenuClickListener callback
     */
    public void setPinClickListener( PinMenuClickListener pinMenuClickListener) {
        this.mPinMenuClickListener = pinMenuClickListener;
    }

    interface PinMenuClickListener{
        /**
         *  PinterestView item click
         * @param childAt position in PinterestView
         */
        void onMenuItemClick(int childAt);

        /**
         * preview(the view click to show pinterestview) click
         */
        void onPreViewClick();
    }
}