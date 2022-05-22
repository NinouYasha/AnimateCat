package com.example.animatecat;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.animatecat.bean.MovingCat;
import com.example.animatecat.interpolarCustom.MyAccelerateInterpolar;
import com.example.animatecat.interpolarCustom.MyBounceInterpolator;

import java.util.Timer;
import java.util.TimerTask;

// INFO sensors
// https://developer.android.com/reference/android/hardware/SensorEvent
//https://mathias-seguy.developpez.com/tutoriels/android/utiliser-capteurs/

//INFO physique corps plan incline
// https://www.alloprof.qc.ca/fr/eleves/bv/physique/le-mouvement-d-un-corps-sur-un-plan-incline-p1086
public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener, Animator.AnimatorListener, ViewTreeObserver.OnGlobalLayoutListener {


    int n=0;

    //*************************
// composants graphiques  *
//*************************
    ImageView ivCat;
    private Button bt;

//*************************
// CAPTEURS               *
//*************************
    private SensorManager sensorManager;
    private Sensor sensor;


//*************************
// DONNEES                *
//*************************
    // cle intent
    public final static String NIOU = "NIOU";

    //donnees
    private MovingCat movingCat;
    private Bitmap bitmapCat;
    private float flPositionX;
    private float flPositionY;
    private float flScreenHeight;
    private float flScreenWidth;
    private float flCatHeight;
    private float flCatWidth;
    private MyAccelerateInterpolar myAccelerateInterpolar;
    private MyBounceInterpolator myBounceInterpolator;
    private int intPreviousInclination;
    private int intCurrentInclination;
    private float flMoveToDistance;
    private static final int CAT_LEFT=0;
    private int intCatRight;
    private Toast toastCatEdge;
    private boolean boolCatEdge;
    private boolean booldCatLoaded;
    Timer timer;
    private boolean boolTimerTilt;
    private boolean boolAngleChanged;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intPreviousInclination =0;

        // Recuperation du choix du chat du formulaire
        movingCat = (MovingCat) getIntent().getExtras().getSerializable(NIOU);

        // Recuperation des composants graphiques
        ivCat =findViewById(R.id.iv_cat);
        bt=findViewById(R.id.button2);
        bt.setOnClickListener(this);

        // Recuperation hauteur et largeur ecran
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        flScreenHeight = displayMetrics.heightPixels;
        flScreenWidth = displayMetrics.widthPixels;

        // Chargement de limage du chat
        bitmapCat = BitmapFactory.decodeResource(getResources(), movingCat.getPicture_cat());
        ivCat.setImageBitmap(bitmapCat);
        booldCatLoaded =false;
        ivCat.getViewTreeObserver().addOnGlobalLayoutListener(this);

        // Message chat au bord
        toastCatEdge = Toast.makeText(this, movingCat.getName()+" va tomber !", Toast.LENGTH_SHORT);
        boolCatEdge =false;

        // animation
        ivCat.animate().setListener(this);

        //capteurs
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

//        List<Sensor> l = sensorManager.getSensorList(Sensor.TYPE_ALL);
//        for (Sensor sensor:l)
//              {
//            Log.w("niou",sensor.getName()+"...."+sensor.getStringType());
//        }

        boolAngleChanged =false;
        boolTimerTilt=false;

    }



    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),sensorManager.SENSOR_DELAY_GAME);

        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(boolAngleChanged) boolTimerTilt=true;
            }
        }, 100, 100);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        timer.cancel();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        double norm_Of_g;
        float z_normalise;

        if(booldCatLoaded) {

            // Recuperer angle
            norm_Of_g = Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] + sensorEvent.values[2] * sensorEvent.values[2]);
            // Pour rester dans le plan x,y
            z_normalise = (float) (sensorEvent.values[2] / norm_Of_g);
            intCurrentInclination = (int) Math.round(Math.toDegrees(Math.acos(z_normalise)) / 15) * 15;

            // Si ecran penche vers gauche ou vers haut
            if (sensorEvent.values[0] > 0) {
                intCurrentInclination =-intCurrentInclination;
            }

            if ((intCurrentInclination != intPreviousInclination) && intCurrentInclination != 0) {
                boolAngleChanged =true;

                if (boolTimerTilt == true) {

                    Log.w("nia","pos_y "+ivCat.getY());

                    boolTimerTilt=false;

                    boolAngleChanged =false;

                    bt.setText("ANGLE");

                    flPositionX = ivCat.getX();

                    myAccelerateInterpolar = new MyAccelerateInterpolar(Math.abs(intCurrentInclination) / 10f);

                    if (sensorEvent.values[0] < 0) {
                        flMoveToDistance = flScreenWidth - flPositionX - flCatWidth;
                        ivCat.animate().translationXBy(flMoveToDistance);

                    } else {
                        flMoveToDistance = flPositionX;
                        ivCat.animate().translationXBy(-flMoveToDistance);
                    }

                    //iv_cat.animate().rotationBy(200);

                    ivCat.animate().setInterpolator(myAccelerateInterpolar);
                    ivCat.animate().start();
                    intPreviousInclination = intCurrentInclination;
                } //if(boolTiltTimer) true

            }//intCurrentInclination != intPreviousInclination) && intCurrentInclination != 0
        }//if(booldCatLoaded)
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    //TESSSST
    public void onClick(View view) {

        flPositionX = (int) ivCat.getX();
        flCatWidth = ivCat.getWidth();
        MyAccelerateInterpolar lala = new MyAccelerateInterpolar();
        //int move_distance= screen_width-positionX-cat_width;
        //move_distance= positionX;
        ivCat.animate().translationXBy((float) (100));
        ivCat.animate().setInterpolator(lala);
        ivCat.animate().start();








    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onAnimationEnd(Animator animator) {
        // scream cat
        // si bord

//        if(ivCat.getAnimation().getInterpolator()== myAccelerateInterpolar){
//            int intPositionX= (int) ivCat.getX();
//            bt.setText("FINI");
//            Log.w("tag", intCurrentInclination +" inclinaison");
//
//            if((intPositionX==CAT_LEFT || intPositionX== intCatRight)&&!boolCatEdge){
//                toastCatEdge.show();
//                boolCatEdge =true;
//            }
//            else if(boolCatEdge){
//                toastCatEdge.cancel();
//                boolCatEdge =false;
//            }
//
//        } //if(ivCat.getAnimation().getInterpolator()== myAccelerateInterpolar)




    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    @Override
    public void onGlobalLayout() {
        flCatWidth = ivCat.getWidth();
        ivCat.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        intCatRight = (int) (flScreenWidth - flCatWidth);
        booldCatLoaded =true;
    }
}