package com.brucetoo.pinterestview;

import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;

/**
 * Created by florentchampigny on 19/04/2016.
 * 用户获取或者设置view的属性值
 * 或者执行属性动画,支持链式处理不同的view
 */
public class DurX {

    View view;

    public DurX(View view) {
        this.view = view;
    }

    /**
     * 添加一个需要属性变化的view
     * @param view
     * @return
     */
    public static DurX putOn(View view) {
        return new DurX(view);
    }

    /**
     * 切换需要属性变化的view
     * @param view
     * @return
     */
    public DurX andPutOn(View view) {
        this.view = view;
        return this;
    }

    /**
     * 监听view被绘制完成后的回调
     * @param sizeListener 获取view size的回调
     */
    public void waitForSize(final Listeners.Size sizeListener) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                if (view != null) {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (sizeListener != null) {
                        sizeListener.onSize(DurX.this);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 获取view在全局中的top值 也就是Y值
     * @return
     */
    public float getY() {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return rect.top;
    }

    /**
     * 获取当前view的x坐标  实际值= translationX + getLeft
     * @return
     */
    public float getX() {
        return ViewCompat.getX(view);
    }

    public DurX alpha(float alpha) {
        if (view != null) {
            ViewCompat.setAlpha(view, alpha);
        }
        return this;
    }

    public DurX scaleX(float scale) {
        if (view != null) {
            ViewCompat.setScaleX(view, scale);
        }
        return this;
    }

    public DurX scaleY(float scale) {
        if (view != null) {
            ViewCompat.setScaleY(view, scale);
        }
        return this;
    }

    public DurX scale(float scale) {
        if (view != null) {
            ViewCompat.setScaleX(view, scale);
            ViewCompat.setScaleY(view, scale);
        }
        return this;
    }

    public DurX translationX(float translation) {
        if (view != null) {
            ViewCompat.setTranslationX(view, translation);
        }
        return this;
    }

    public DurX translationY(float translation) {
        if (view != null) {
            ViewCompat.setTranslationY(view, translation);
        }
        return this;
    }

    public DurX translation(float translationX, float translationY) {
        if (view != null) {
            ViewCompat.setTranslationX(view, translationX);
            ViewCompat.setTranslationY(view, translationY);
        }
        return this;
    }

    public DurX pivotX(float percent) {
        if (view != null) {
            ViewCompat.setPivotX(view, view.getWidth() * percent);
        }
        return this;
    }

    public DurX pivotY(float percent) {
        if (view != null) {
            ViewCompat.setPivotY(view, view.getHeight() * percent);
        }
        return this;
    }

    public DurX visible() {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public DurX invisible() {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
        return this;
    }

    public DurX gone() {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 属性变化的动画效果
     * @return
     */
    public DurXAnimator animate() {
        return new DurXAnimator(this);
    }


    /**
     * 动画执行的监听
     * 相当于使用弱引用将所有的监听都包裹起来,
     * 重新定义自己的监听
     */
    static class DurXAnimatorListener implements ViewPropertyAnimatorListener {

        WeakReference<DurXAnimator> reference;

        public DurXAnimatorListener(DurXAnimator durXAnimator) {
            //类用弱引用包裹起来,利于回收
            this.reference = new WeakReference<>(durXAnimator);
        }

        @Override
        public void onAnimationStart(View view) {
            DurXAnimator durXAnimator = reference.get();
            if (durXAnimator != null && durXAnimator.startListener != null) {
                Listeners.Start startListener = durXAnimator.startListener.get();
                if (startListener != null) {
                    startListener.onStart();
                }
            }
        }

        @Override
        public void onAnimationEnd(View view) {
            DurXAnimator durXAnimator = reference.get();
            if (durXAnimator != null && durXAnimator.endListener != null) {
                Listeners.End endListener = durXAnimator.endListener.get();
                if (endListener != null) {
                    endListener.onEnd();
                }
            }
        }

        @Override
        public void onAnimationCancel(View view) {

        }
    }

    static class DurXAnimatorUpdate implements ViewPropertyAnimatorUpdateListener {

        WeakReference<DurXAnimator> reference;

        public DurXAnimatorUpdate(DurXAnimator durXAnimator) {
            this.reference = new WeakReference<>(durXAnimator);
        }

        @Override
        public void onAnimationUpdate(View view) {
            DurXAnimator durXAnimator = reference.get();
            if (durXAnimator != null && durXAnimator.updateListener != null) {
                Listeners.Update updateListener = durXAnimator.updateListener.get();
                if (updateListener != null) {
                    updateListener.update();
                }
            }
        }
    }


    /**
     * 该内部类用来执行属性动画
     */
    public static class DurXAnimator {
        //属性动画执行的兼容类 -- 但是发现源码在 < 14的版本中全是空实现
        final ViewPropertyAnimatorCompat animator;
        final DurX durX;

        //所有的监听状态都用弱引用包裹起来,且全部监听都是分开的 可以只监听关心的回调
        WeakReference<Listeners.Start> startListener;
        WeakReference<Listeners.End> endListener;
        WeakReference<Listeners.Update> updateListener;

        /**
         * 初始化动画执行类
         * @param durX
         */
        DurXAnimator(DurX durX) {
            this.animator = ViewCompat.animate(durX.view);
            this.durX = durX;
            //设置监听
            this.animator.setListener(new DurXAnimatorListener(this));
        }

        public DurXAnimator alpha(float alpha) {
            animator.alpha(alpha);
            return this;
        }

        public DurXAnimator alpha(float from, float to) {
            //先设置起点的alpha值
            durX.alpha(from);
            return alpha(to);//在执行动画到to值
        }

        public DurXAnimator scaleX(float scale) {
            animator.scaleX(scale);
            return this;
        }

        public DurXAnimator scaleX(float from, float to) {
            durX.scaleX(from);
            return scaleX(to);
        }

        public DurXAnimator scaleY(float scale) {
            animator.scaleY(scale);
            return this;
        }

        public DurXAnimator scaleY(float from, float to) {
            durX.scaleY(from);
            return scaleY(to);
        }

        public DurXAnimator scale(float scale) {
            animator.scaleX(scale);
            animator.scaleY(scale);
            return this;
        }

        public DurXAnimator scale(float from, float to) {
            durX.scale(from);
            return scale(to);
        }

        public DurXAnimator translationX(float translation) {
            animator.translationX(translation);
            return this;
        }

        public DurXAnimator translationX(float from, float to) {
            durX.translationX(from);
            return translationX(to);
        }

        public DurXAnimator translationY(float translation) {
            animator.translationY(translation);
            return this;
        }

        public DurXAnimator translationY(float from, float to) {
            durX.translationY(from);
            return translationY(to);
        }

        public DurXAnimator translation(float translationX, float translationY) {
            animator.translationX(translationX);
            animator.translationY(translationY);
            return this;
        }

        public DurXAnimator rotation(float rotation) {
            animator.rotation(rotation);
            return this;
        }

        public DurXAnimator duration(long duration) {
            animator.setDuration(duration);
            return this;
        }

        public DurXAnimator startDelay(long duration) {
            animator.setStartDelay(duration);
            return this;
        }

        public DurXAnimator interpolator(Interpolator interpolator){
            animator.setInterpolator(interpolator);
            return this;
        }

        public DurXAnimator end(Listeners.End listener) {
            endListener = new WeakReference<>(listener);
            return this;
        }

        public DurXAnimator update(Listeners.Update listener) {
            updateListener = new WeakReference<>(listener);
            animator.setUpdateListener(new DurXAnimatorUpdate(this));
            return this;
        }

        public DurXAnimator start(Listeners.Start listener) {
            startListener = new WeakReference<>(listener);
            return this;
        }

        public DurX pullOut() {
            return durX;
        }

        /**
         * 执行完动画后再执行另一个view的动画,所有的属性值都可以改变 重新执行
         * @param view
         * @return
         */
        public DurXAnimator thenAnimate(View view) {
            DurX durX = new DurX(view);
            DurXAnimator durXAnimator = durX.animate();
            durXAnimator.startDelay(animator.getStartDelay() + animator.getDuration());
            return durXAnimator;
        }

        /**
         * 同时执行另一个view的动画
         * @param view
         * @return
         */
        public DurXAnimator andAnimate(View view) {
            DurX durX = new DurX(view);
            DurXAnimator durXAnimator = durX.animate();
            durXAnimator.startDelay(animator.getStartDelay());
            return durX.animate();
        }
    }

    public static class Listeners {
        interface End {
            void onEnd();
        }

        interface Start {
            void onStart();
        }

        interface Size {
            void onSize(DurX durX);
        }

        interface Update {
            void update();
        }
    }


}
