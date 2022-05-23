package com.example.animatecat.interpolarCustom;

public class MyBounceInterpolator implements android.view.animation.Interpolator {
    private double mAmplitude = 2;
    private double mFrequency = 10;

    public MyBounceInterpolator() {
    }

    MyBounceInterpolator(double amp, double freq) {
        mAmplitude = amp;
        mFrequency = freq;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) * Math.cos(mFrequency * time) + 1);
    }

    public float getPixelsMoveX(float transX,float maxTransX,boolean firstBounce){
        if(firstBounce) return -transX/maxTransX*100;
        else return transX/maxTransX*100;
    }

    public float getPixelsMoveY(float transY,float maxTransY,boolean firstBounce){
        if(firstBounce) return -transY/maxTransY*100;
        else return transY/maxTransY*100;
    }
}

