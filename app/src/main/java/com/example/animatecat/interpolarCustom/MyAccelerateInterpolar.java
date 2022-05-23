package com.example.animatecat.interpolarCustom;

import android.util.Log;

public class MyAccelerateInterpolar implements android.view.animation.Interpolator {
        private double facteur = 1;
        private float transX = 1;
        private float transY=1;

        public MyAccelerateInterpolar() {

        }

        public MyAccelerateInterpolar(double facteur,float transX,float transY) {
            this.facteur=facteur;
            this.transX=transX;
            this.transY=transY;
        }

        public float getInterpolation(float time) {
            float a= (float) (Math.pow(time,facteur));
            return a;
        }

    public float getTransX() {
        return transX;
    }

    public float getTransY() {
        return transY;
    }
}
