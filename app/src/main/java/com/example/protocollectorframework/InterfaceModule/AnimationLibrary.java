package com.example.protocollectorframework.InterfaceModule;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Class accountable for necessary UI animations and hardware interactions like vibration
 */
public class AnimationLibrary {

    public static AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private Context context;

    /**
     * Constructor
     *
     * @param context: the context of the activity
     */
    public AnimationLibrary(Context context) {
        this.context = context;
    }

    /**
     * Fetch the default button click animation
     *
     * @return default button click animation
     */
    public AlphaAnimation getClickAnimation() {
        return buttonClick;
    }

    /**
     * Causes a vibration for 500 milliseconds
     */
    public void vibrate() {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
    }

    /**
     * Causes a vibration for a certain milliseconds
     *
     * @param duration: vibration duration in milliseconds
     */
    public void vibrate(long duration) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }

    /**
     * Rotates a floating action button and then changes it's drawable
     *
     * @param v:        floating action button view
     * @param rotate:   flag for rotation
     * @param rotation: degree of rotation
     * @param d:        new drawable
     * @return true if drawable changed with success, false otherwise
     */
    public boolean rotateFab(final View v, boolean rotate, float rotation, final Drawable d) {
        v.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (d != null) {
                            try {
                                FloatingActionButton fb = (FloatingActionButton) v;
                                fb.setImageDrawable(d);
                            } catch (Exception e) {
                                Log.e("Animation eror", "Not a FAB");
                            }
                        }
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(rotate ? rotation : 0f);
        return rotate;
    }

    /**
     * Show hidden view
     *
     * @param v:        target view
     * @param x:        x axis translation
     * @param y:        y axis translation
     * @param duration: animation duration in milliseconds
     */
    public void showIn(final View v, int x, int y, long duration) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(0f);
        v.setTranslationX(x);
        v.setTranslationY(y);
        v.animate()
                .setDuration(duration)
                .translationY(0)
                .translationX(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(1f)
                .start();
    }

    /**
     * Hide visible view
     *
     * @param v:        target view
     * @param x:        x axis translation
     * @param y:        y axis translation
     * @param duration: animation duration in milliseconds
     */
    public void showOut(final View v, int x, int y, long duration) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(1f);
        v.setTranslationX(0);
        v.setTranslationY(0);
        v.animate()
                .setDuration(duration)
                .translationX(x)
                .translationY(y)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.GONE);
                        super.onAnimationEnd(animation);
                    }
                }).alpha(0f)
                .start();
    }

    /**
     * Starts a blinking animation
     *
     * @param v:        target view
     * @param duration: animation duration in milliseconds
     */
    public void startBlinkAnimation(View v, long duration) {
        Animation animation = new AlphaAnimation((float) 1.0, 0);

        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());

        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        v.startAnimation(animation);
    }

    /**
     * Removes all animations associated to the view
     *
     * @param v: target view
     */
    public void removeAnimations(View v) {
        v.clearAnimation();
    }
}
