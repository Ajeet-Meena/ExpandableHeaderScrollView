package io.stackbit.stackbit.ViewHelper;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Ajeet Kumar Meena on 26/03/16.
 */
public class DropDownAnim extends Animation {
    private final int targetHeight;
    private final View view;
    private final boolean down;
    private int initialHeight;

    public DropDownAnim(View view, int targetHeight, boolean down) {
        this.view = view;
        this.targetHeight = targetHeight;
        this.down = down;
        this.initialHeight = this.view.getMeasuredHeight();
    }

    public DropDownAnim(View view,int initialHeight, int targetHeight, boolean down) {
        this.view = view;
        this.targetHeight = targetHeight;
        this.down = down;
        this.initialHeight = initialHeight;
    }


    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;
        if (down) {
            newHeight = (int) ((initialHeight) + ((targetHeight-initialHeight) * interpolatedTime));
        } else {
            newHeight = (int) (targetHeight + ((initialHeight - targetHeight)) * (1 - interpolatedTime));
        }
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public int getInitialHeight() {
        return initialHeight;
    }

    public void setInitialHeight(int initialHeight) {
        this.initialHeight = initialHeight;
    }
}