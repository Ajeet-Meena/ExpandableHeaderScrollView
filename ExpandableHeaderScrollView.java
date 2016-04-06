package io.stackbit.stackbit.ViewHelper;

import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by Ajeet Kumar Meena on 26/03/16.
 */
public class ExpandableHeaderScrollView extends ScrollView implements ViewTreeObserver.OnScrollChangedListener {

    Service context;

    // Default Height of header
    private static final int DEFAULT_HEADER_HEIGHT_IN_DP = 240;
    // Default header height in pixel
    private final int defaultHeightInPx = dpToPx(DEFAULT_HEADER_HEIGHT_IN_DP);
    // Current Height of header
    private int currentHeightInPx;
    // Height of header till it should be close
    private int closingHeightInPx;
    // Screen height in pixel
    private int screenHeightInPx;

    private int actionTouchDownRawY = 0;
    private int actionTouchDownImageHeight = 0;
    private VelocityTracker velocityTracker = null;
    private float currentVelocity;
    private View headerView;
    private LinearLayout parentLayout;
    private float scrollY;
    private boolean isScrollEvent;
    private boolean isHeaderEvent;

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (context != null) {
                context.stopSelf();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public ExpandableHeaderScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ExpandableHeaderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableHeaderScrollView(Context context) {
        super(context);
    }

    public void addHeader(Service context, LinearLayout parentView, View headerView) {
        this.context = context;
        this.headerView = headerView;
        this.parentLayout = parentView;
        initHeader();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("onTouchEvent", "isScrollEvent: " + isScrollEvent + " isHeaderEvent: " + isHeaderEvent);
        if (isScrollEvent && !isHeaderEvent) {
            super.onTouchEvent(ev);
        } else if (isHeaderEvent && !isScrollEvent) {
            handleMotionEvent(ev);
        } else if (isHeaderEvent) {
            super.onTouchEvent(ev);
            handleMotionEvent(ev);
        }
        return true;
    }

    public void initHeader() {
        this.currentHeightInPx = dpToPx(DEFAULT_HEADER_HEIGHT_IN_DP);
        this.screenHeightInPx = getScreenHeightInPixel(context);
        this.closingHeightInPx = ((this.screenHeightInPx * 2) / 3);
        this.velocityTracker = VelocityTracker.obtain();
        getViewTreeObserver().addOnScrollChangedListener(this);
//        post(new Runnable() {
//            @Override
//            public void run() {
//                scrollTo(0, 1);
//            }
//        });
        this.isHeaderEvent = true;
        this.isScrollEvent = false;
    }

    public boolean handleMotionEvent(MotionEvent motionEvent) {
        int index = motionEvent.getActionIndex();
        int pointerId = motionEvent.getPointerId(index);
        final int rawY = (int) motionEvent.getRawY();
        currentHeightInPx = headerView.getMeasuredHeight();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                actionTouchDownRawY = rawY;
                actionTouchDownImageHeight = headerView.getMeasuredHeight();
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(motionEvent);
                break;
            case MotionEvent.ACTION_UP:
                Log.d("ActionUp", "currentVelocity: " + currentVelocity);
                int DEFAULT_CLOSING_VELOCITY = 300;
                if (currentVelocity > DEFAULT_CLOSING_VELOCITY) {
                    DropDownAnim dropDownAnim = new DropDownAnim(headerView, screenHeightInPx, true);
                    float distance = (((screenHeightInPx - headerView.getMeasuredHeight()) / getResources().getDisplayMetrics().density));
                    float duration = (distance / currentVelocity) * 2000;
                    if (duration <=0 || duration> 750) {
                        dropDownAnim.setDuration(200);
                    } else {
                        dropDownAnim.setDuration((int) (duration));
                    }
                    dropDownAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                    dropDownAnim.setAnimationListener(animationListener);
                    headerView.startAnimation(dropDownAnim);
                    break;
                }
                if (headerView.getMeasuredHeight() > closingHeightInPx) {
                    DropDownAnim dropDownAnim = new DropDownAnim(headerView, screenHeightInPx, true);
                    int duration = (int) ((screenHeightInPx - headerView.getMeasuredHeight()) / getResources().getDisplayMetrics().density);
                    if (duration > 0) {
                        dropDownAnim.setDuration(((duration) * 2));
                    } else {
                        dropDownAnim.setDuration(150);
                    }
                    dropDownAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.stopSelf();
                        }
                    }, dropDownAnim.getDuration());
                    headerView.requestLayout();
                    headerView.startAnimation(dropDownAnim);
                } else {
                    DropDownAnim dropDownAnim = new DropDownAnim(headerView, defaultHeightInPx, false);
                    int duration = (int) ((headerView.getMeasuredHeight() - defaultHeightInPx) / getResources().getDisplayMetrics().density);
                    if (duration > 0) {
                        dropDownAnim.setDuration(((duration) * 2));
                    } else {
                        dropDownAnim.setDuration(150);
                    }
                    dropDownAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                    headerView.startAnimation(dropDownAnim);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(motionEvent);
                velocityTracker.computeCurrentVelocity(1000);
                currentVelocity = VelocityTrackerCompat.getYVelocity(velocityTracker, pointerId);
                Log.d("Logs", "rawY: " + rawY + " actionTouchDownRawY: " + actionTouchDownRawY);
                Log.d("Logs", "velocityY: " + currentVelocity);
                if (scrollY == 0 && (currentHeightInPx <= defaultHeightInPx) && currentVelocity < -0.5) {
                    isScrollEvent = true;
                    isHeaderEvent = false;
                    return true;
                }
                if (actionTouchDownImageHeight + (rawY - actionTouchDownRawY) <= 0) {
                    return true;
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, actionTouchDownImageHeight + (rawY - actionTouchDownRawY));
                headerView.setLayoutParams(params);
                parentLayout.updateViewLayout(headerView, params);
                break;
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.recycle();
                break;
        }
        parentLayout.invalidate();
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("EHSV onInterceptEvent", ev.toString());
        return super.onInterceptTouchEvent(ev);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public int getScreenHeightInPixel(Service context) {
        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return height;
    }

    @Override
    public void onScrollChanged() {
        scrollY = getScrollY();
        Log.d("ScrollY", "ScrollY: " + scrollY);
        if (scrollY <= 0) {
            isHeaderEvent = true;
            isScrollEvent = false;
        } else {
            isScrollEvent = true;
            isHeaderEvent = false;
        }
    }

    public void initOnStartAnimation() {
        DropDownAnim dropDownAnim = new DropDownAnim(headerView, screenHeightInPx, defaultHeightInPx, false);
        int duration = (int) ((screenHeightInPx - defaultHeightInPx) / getResources().getDisplayMetrics().density);
        if (duration > 0) {
            dropDownAnim.setDuration((duration * 2) / 3);
        } else {
            dropDownAnim.setDuration(150);
        }
        dropDownAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        headerView.startAnimation(dropDownAnim);
    }
}
