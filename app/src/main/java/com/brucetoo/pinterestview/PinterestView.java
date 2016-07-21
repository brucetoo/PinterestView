package com.brucetoo.pinterestview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Bruce Too
 * On 10/2/15.
 * At 11:10
 */
public class PinterestView extends ViewGroup implements View.OnTouchListener {

    private final static String TAG = "PinterestView";

    private final static int EXPAND_ANIMATION_DURATION = 200;

    private final static int SCALE_ANIMATION_DURATION = 100;

    private final static float MAX_SCALE = 1.2f;

    private int mChildSize;

    private int mTipsColor;

    private int mTipsBackground;

    private int mTipsSize;

    private static final float DEFAULT_FROM_DEGREES = -90.0f;

    private static final float DEFAULT_TO_DEGREES = -90.0f;

    private static final int DEFAULT_CHILD_SIZE = 44;

    private static final int DEFAULT_TIPS_COLOR = Color.WHITE;

    private static final int DEFAULT_TIPS_BACKGROUND = R.drawable.shape_child_item;

    private static final int DEFAULT_TIPS_SIZE = 15;

    private static final int DEFAULT_RECT_RADIUS = 100;

    private float mFromDegrees = DEFAULT_FROM_DEGREES;

    private float mToDegrees = DEFAULT_TO_DEGREES;

    private static final int DEFAULT_RADIUS = 80;

    private int mRadius;

    private float mMaxScale;

    private Context mContext;

    private boolean mExpanded = false;

    private ArrayList<View> mChildViews = new ArrayList<>();

    private float mCenterX;
    private float mCenterY;

    private PinterestView.PinMenuClickListener mPinMenuClickListener;

    private PopupWindow mPopTips;

    private Rect mInner = new Rect();
    private View mLastNearestView;
    private boolean mIsAnimating;

    final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
            mCenterX = e.getRawX();
            mCenterY = e.getRawY();
            Log.i(TAG, "centerX:" + mCenterX + "  centerY:" + mCenterY);
            confirmDegreeRangeByCenter(mCenterX, mCenterY);
            PinterestView.this.setVisibility(View.VISIBLE);
            switchState();
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mPinMenuClickListener != null) {
                mPinMenuClickListener.onAnchorViewClick();
            }
            return true;
        }
    });

    public PinterestView(Context context) {
        super(context);
        this.mContext = context;
    }

    public PinterestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PinterestView, 0, 0);
            mChildSize = a.getDimensionPixelSize(R.styleable.PinterestView_child_size, DEFAULT_CHILD_SIZE);
            mTipsColor = a.getColor(R.styleable.PinterestView_tips_color, DEFAULT_TIPS_COLOR);
            mTipsBackground = a.getResourceId(R.styleable.PinterestView_tips_background, DEFAULT_TIPS_BACKGROUND);
            mTipsSize = a.getDimensionPixelSize(R.styleable.PinterestView_tips_size, DEFAULT_TIPS_SIZE);
            mRadius = a.getDimensionPixelOffset(R.styleable.PinterestView_child_radius, dp2px(DEFAULT_RADIUS));
            mMaxScale = a.getFloat(R.styleable.PinterestView_child_max_scale, MAX_SCALE);
            createTipsPopWindow(context);
            a.recycle();
        }

        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (getVisibility() == VISIBLE) {
            handleTouchEvent(event);
            return true;
        }
        return gestureDetector.onTouchEvent(event);
    }


    private static double distSq(double x1, double y1, double x2, double y2) {
        return Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
    }

    /**
     * find the nearest child view
     */
    private View nearest(float x, float y, List<View> views) {
        double minDistSq = Double.MAX_VALUE;
        View minView = null;

        for (View view : views) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            double distSq = distSq(x, y, rect.centerX(),
                    rect.centerY());

            if (distSq < Math.pow(1.2f * view.getMeasuredWidth(), 2) && distSq < minDistSq) {
                minDistSq = distSq;
                minView = view;
            }
        }

        return minView;
    }

    private void handleTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //only listen ACTION_MOVE when PinterestView is visible
                if (PinterestView.this.getVisibility() == VISIBLE) {

                    View nearest = nearest(event.getRawX(), event.getRawY(), mChildViews);
                    if (nearest != null) {
                        if (mLastNearestView != null && mLastNearestView == nearest) return;

                        DurX.putOn(nearest).animate().scale(mMaxScale).duration(SCALE_ANIMATION_DURATION);
                        ((CircleImageView) nearest).setFillColor(mContext.getResources().getColor(R.color.colorPrimary));
                        if (mPopTips.isShowing()) {
                            mPopTips.dismiss();
                        }
                        TextView contentView = (TextView) mPopTips.getContentView();
                        contentView.setText((String) nearest.getTag());
                        int width = contentView.getMeasuredWidth();
                        int offsetLeft = width == 0 ? -mChildSize / 4 : (-width / 2 + mChildSize / 2);

                        if (!mIsAnimating) {
                            mPopTips.showAsDropDown(nearest, offsetLeft, -mChildSize * 2);
                        }
                        for (View view : mChildViews) {
                            if (view != nearest) {
                                ((CircleImageView) view).setFillColor(mContext.getResources().getColor(R.color.colorAccent));
                                DurX.putOn(view).animate().scale(1).duration(SCALE_ANIMATION_DURATION);
                            }
                        }
                        mLastNearestView = nearest;
                    } else {
                        mLastNearestView = null;
                        mPopTips.dismiss();
                        for (View view : mChildViews) {
                            DurX.putOn(view).scale(1);
                            ((CircleImageView) view).setFillColor(mContext.getResources().getColor(R.color.colorAccent));
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (PinterestView.this.getVisibility() == VISIBLE) {
                    mPopTips.dismiss();
                    View nearest = nearest(event.getRawX(), event.getRawY(), mChildViews);
                    if (nearest != null && nearest.getTag() != null) {
                        int clickItemPos = PinterestView.this.getTag() == null ? -1 : (int) PinterestView.this.getTag();
                        mPinMenuClickListener.onMenuItemClick(nearest,clickItemPos);
                    }
                    switchState();
                }
                break;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        //Screen width and height
        setMeasuredDimension(mContext.getResources().getDisplayMetrics().widthPixels, mContext.getResources().getDisplayMetrics().heightPixels);

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(mChildSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mChildSize, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //get PintertestView Height
        getGlobalVisibleRect(mInner);
        //distance from screen top
        float innerTop = getResources().getDisplayMetrics().heightPixels - (mInner.bottom - mInner.top);

        mCenterY = mCenterY - innerTop;
        Log.i(TAG, "innerTop:" + innerTop + " centerX:" + mCenterX + "  centerY:" + mCenterY);

        final int childCount = getChildCount();
        //single degrees
        final float perDegrees = (mToDegrees - mFromDegrees) / (childCount - 1);

        float degrees = mFromDegrees;

        mChildViews.clear();
        //Note if i = 1 indicate ignore the center view
        for (int i = 1; i < getChildCount(); i++) {
            mChildViews.add(getChildAt(i));
        }

        //add centerView
        Rect centerRect = computeChildFrame(mCenterX, mCenterY, 0, perDegrees, mChildSize);
        getChildAt(0).layout(centerRect.left, centerRect.top, centerRect.right, centerRect.bottom);
        degrees += perDegrees;
        //add other view
        for (int i = 1; i < childCount; i++) {
            Rect frame = computeChildFrame(mCenterX, mCenterY, mRadius, degrees, mChildSize);
            if (i == 1) {
                Log.i("computeChildFrame:", frame + "");
            }
            degrees += perDegrees;
            getChildAt(i).layout(frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    private void createTipsPopWindow(Context context) {
        TextView tips = new TextView(context);
        tips.setTypeface(null, Typeface.BOLD);
        tips.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTipsSize);
        tips.setTextColor(mTipsColor);
        tips.setBackgroundResource(mTipsBackground);
        tips.setGravity(Gravity.CENTER);
        mPopTips = new PopupWindow(tips, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    private void confirmDegreeRangeByCenter(float centerX, float centerY) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();

        //left-top (-60,90)
        Rect leftTopRect = new Rect(0, 0, dp2px(DEFAULT_RECT_RADIUS), dp2px(DEFAULT_RECT_RADIUS));

        //top (-10,140)
        Rect topRect = new Rect(dp2px(DEFAULT_RECT_RADIUS), 0, metrics.widthPixels - dp2px(DEFAULT_RECT_RADIUS), dp2px(DEFAULT_RECT_RADIUS));

        //right-top  50,200
        Rect rightTopRect = new Rect(metrics.widthPixels - dp2px(DEFAULT_RECT_RADIUS), 0, metrics.widthPixels, dp2px(DEFAULT_RECT_RADIUS));

        //left -100,50
        Rect leftRect = new Rect(0, dp2px(DEFAULT_RECT_RADIUS), dp2px(DEFAULT_RECT_RADIUS), metrics.heightPixels - dp2px(DEFAULT_RECT_RADIUS));

        //right 80,230
        Rect rightRect = new Rect(metrics.widthPixels - dp2px(DEFAULT_RECT_RADIUS), dp2px(DEFAULT_RECT_RADIUS), metrics.widthPixels, metrics.heightPixels - dp2px(DEFAULT_RECT_RADIUS));

        //left_bottom -140,10
        Rect leftBottomRect = new Rect(0, metrics.heightPixels - dp2px(DEFAULT_RECT_RADIUS), dp2px(DEFAULT_RECT_RADIUS), metrics.heightPixels);
        //bottom  170,320
        Rect bottomRect = new Rect(dp2px(DEFAULT_RECT_RADIUS), metrics.heightPixels - dp2px(DEFAULT_RECT_RADIUS), metrics.widthPixels - dp2px(DEFAULT_RECT_RADIUS), metrics.heightPixels);
        //right_bottom 150,300 and center
        Rect rightBottomRect = new Rect(metrics.widthPixels - dp2px(DEFAULT_RECT_RADIUS), metrics.heightPixels - dp2px(DEFAULT_RECT_RADIUS), metrics.widthPixels, metrics.heightPixels);
        Rect centerRect = new Rect(dp2px(DEFAULT_RECT_RADIUS), dp2px(DEFAULT_RECT_RADIUS), metrics.widthPixels - dp2px(DEFAULT_RECT_RADIUS), metrics.heightPixels - dp2px(DEFAULT_RECT_RADIUS));

        if (leftTopRect.contains((int) centerX, (int) centerY)) {
            mFromDegrees = -60;
            mToDegrees = 90;
        } else if (topRect.contains((int) centerX, (int) centerY)) {
            mFromDegrees = -10;
            mToDegrees = 150;
        } else if (rightTopRect.contains((int) centerX, (int) centerY)) {
            mFromDegrees = 50;
            mToDegrees = 200;
        } else if (leftRect.contains((int) centerX, (int) centerY)) {
            mFromDegrees = -100;
            mToDegrees = 50;
        } else if (rightRect.contains((int) centerX, (int) centerY)) {
            mFromDegrees = 80;
            mToDegrees = 230;
        } else if (leftBottomRect.contains((int) centerX, (int) centerY)) {
            mFromDegrees = -140;
            mToDegrees = 10;
        } else if (bottomRect.contains((int) centerX, (int) centerY)) {
            mFromDegrees = 170;
            mToDegrees = 320;
        } else if (rightBottomRect.contains((int) centerX, (int) centerY) || centerRect.contains((int) centerX, (int) centerY)) {
            mFromDegrees = 150;
            mToDegrees = 300;
        }
        requestLayout();

    }

    private Rect getChildDisPlayBounds(View view) {
        //scale the rect range
        Rect rect = new Rect();
        view.getHitRect(rect);
        return rect;
    }

    private void bindChildAnimation(final View child, final int position) {
        //in case when init in,child.getWidth = 0 cause get wrong rect
        child.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect childRect = getChildDisPlayBounds(child);
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
        DurX.putOn(child).animate()
                .translationX(0, (mCenterX - childRect.exactCenterX()) / 2)
                .translationY(0, (mCenterY - childRect.exactCenterY()) / 2)
                .alpha(0.5f)
                .duration(EXPAND_ANIMATION_DURATION)
                .interpolator(new AccelerateInterpolator())
                .end(new DurX.Listeners.End() {
                    @Override
                    public void onEnd() {
                        recoverChildView();
                        PinterestView.this.setVisibility(GONE);
                    }
                });
    }

    private void expandAnimation(View child, Rect childRect) {
        DurX.putOn(child).animate()
                .translationX((mCenterX - childRect.exactCenterX()) / 2, 0)
                .translationY((mCenterY - childRect.exactCenterY()) / 2, 0)
                .alpha(0.5f, 1)
                .duration(EXPAND_ANIMATION_DURATION / 2)
                .interpolator(new AccelerateInterpolator())
                .start(new DurX.Listeners.Start() {
                    @Override
                    public void onStart() {
                        mIsAnimating = true;
                    }
                })
                .end(new DurX.Listeners.End() {
                    @Override
                    public void onEnd() {
                        mIsAnimating = false;
                        recoverChildView();
                    }
                });
    }


    /**
     * center view animation
     *
     * @param child
     */
    private void bindCenterViewAnimation(View child) {
        float from = mExpanded ? 0.5f : 1.0f;
        float to = mExpanded ? 1.0f : 0.5f;
        DurX.putOn(child).animate()
                .scale(from, to)
                .alpha(from, to)
                .duration(EXPAND_ANIMATION_DURATION)
                .interpolator(new AccelerateInterpolator())
                .end(new DurX.Listeners.End() {
                    @Override
                    public void onEnd() {
                        recoverChildView();
                        if (!mExpanded) {
                            PinterestView.this.setVisibility(GONE);
                        }
                    }
                });
    }

    private void recoverChildView() {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
//            getChildAt(i).animate().setDuration(100).translationX(0).translationY(0).scaleX(1).scaleX(1).start();
            DurX.putOn(getChildAt(i)).scale(1).translation(0f, 0f);
        }
    }

    private static Rect computeChildFrame(final float centerX, final float centerY, final int radius, final float degrees,
                                          final int size) {

        final double childCenterX = centerX + radius * Math.cos(Math.toRadians(degrees));
        final double childCenterY = centerY + radius * Math.sin(Math.toRadians(degrees));

        return new Rect((int) (childCenterX - size / 2), (int) (childCenterY - size / 2),
                (int) (childCenterX + size / 2), (int) (childCenterY + size / 2));
    }


    public void switchState() {
        mExpanded = !mExpanded;
        final int childCount = getChildCount();
        //other view
        for (int i = 1; i < childCount; i++) {
            ((CircleImageView) getChildAt(i)).setFillColor(mContext.getResources().getColor(R.color.colorAccent));
            bindChildAnimation(getChildAt(i), i);
        }
        //center view
        bindCenterViewAnimation(getChildAt(0));

    }

    /**
     * addView to PinterestView
     *
     * @param centerView
     * @param normalViews
     */
    public void addMenuItem(View centerView, View... normalViews) {
        addView(centerView, 0);
        for (View normalView : normalViews) {
            addView(normalView);
        }
    }

    /**
     * size (dp)
     * default all item child size are same
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

    /**
     * set Pinterest click listener
     *
     * @param pinMenuClickListener callback
     */
    public void setPinClickListener(PinMenuClickListener pinMenuClickListener) {
        this.mPinMenuClickListener = pinMenuClickListener;
    }

    interface PinMenuClickListener {
        /**
         * PinterestView item click
         *
         * @param checkedView view has be checked
         */
        void onMenuItemClick(View checkedView,int clickItemPos);

        /**
         * Anchor view(the view click to show pinterestView) click
         */
        void onAnchorViewClick();
    }
}