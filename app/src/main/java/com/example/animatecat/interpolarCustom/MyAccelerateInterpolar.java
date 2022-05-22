package com.example.animatecat.interpolarCustom;

import android.util.Log;

public class MyAccelerateInterpolar implements android.view.animation.Interpolator {
        double facteur = 1;

        public MyAccelerateInterpolar() {

        }

        public MyAccelerateInterpolar(double facteur) {
            this.facteur=facteur;
        }

        public float getInterpolation(float time) {
            float a= (float) (Math.pow(time,facteur));
            return a;
        }
}
