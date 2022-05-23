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
    private static final int CAT_TOP=0;
    private int intCatRight;
    private int intCatBottom;
    private Toast toastCatEdge;
    private boolean boolCatEdge;
    private boolean booldCatLoaded;
    Timer timer;
    private boolean boolTimerTilt;
    private boolean boolAngleChanged;
    private boolean boolCatAnimated;
    private boolean boolBounceInterpolar;

    //test
    int translation;
    int droite;
    float gauche;
    float fcty;




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
        boolCatAnimated=false;
        boolBounceInterpolar=false;

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

        if(booldCatLoaded&&boolBounceInterpolar==false) {

            // Recuperer angle
            norm_Of_g = Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] + sensorEvent.values[2] * sensorEvent.values[2]);
            // Pour rester dans le plan x,y
            z_normalise = (float) (sensorEvent.values[2] / norm_Of_g);
            intCurrentInclination = (int) Math.round(Math.toDegrees(Math.acos(z_normalise)) / 5) * 5;



            if ((intCurrentInclination != intPreviousInclination)) {
                boolAngleChanged =true;

                if (boolTimerTilt == true) {

                    if((intCurrentInclination==0)&&boolCatAnimated==true){
                        ivCat.animate().cancel();
                        boolCatAnimated=false;
                    }
                    else if(intCurrentInclination!=0) {

                        boolTimerTilt = false;

                        boolAngleChanged = false;

                        boolCatAnimated = true;

                        bt.setText("ANGLE");

                        float[] tabPixelsTranslation = getPixelsTranslations(sensorEvent.values[0], sensorEvent.values[1], ivCat.getX(), ivCat.getY());
                        ivCat.animate().translationYBy(tabPixelsTranslation[1]);
                        ivCat.animate().translationXBy(tabPixelsTranslation[0]);


                        myAccelerateInterpolar = new MyAccelerateInterpolar(Math.abs(intCurrentInclination) * 0.1f,tabPixelsTranslation[0],tabPixelsTranslation[1]);

                        int duration = (int) (10000 / Math.abs(intCurrentInclination));

                        ivCat.animate().setDuration(duration);
                        ivCat.animate().setInterpolator(myAccelerateInterpolar);
                        ivCat.animate().start();
                        intPreviousInclination = intCurrentInclination;
                    }//else if(intCurrentInclination==0)
                } //if(boolTiltTimer) true
//
            }//intCurrentInclination != intPreviousInclination)
        }//if(booldCatLoaded)
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    //TESSSST
    public void onClick(View view) {

        //flPositionY = (int) ivCat.getY();
        flCatHeight = ivCat.getHeight();
        MyAccelerateInterpolar lala = new MyAccelerateInterpolar();
       //int move_distance= (int) (flScreenHeight-flPositionY-flCatHeight*2);
        //move_distance= positionX;
        //ivCat.animate().translationYBy((float) (move_distance));
        ivCat.animate().setInterpolator(lala);
        ivCat.animate().start();









    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onAnimationEnd(Animator animator) {
        float posX = ivCat.getX();
        float posY = ivCat.getY();

        if(animator.getInterpolator()==myBounceInterpolator){
            boolBounceInterpolar=false;
        }

        if(animator.getInterpolator()==myAccelerateInterpolar){
            if(posX==CAT_LEFT||posX==intCatRight||posY==intCatBottom||posY==CAT_TOP){
                boolBounceInterpolar = true;
                myBounceInterpolator=new MyBounceInterpolator();
                float transX = myBounceInterpolator.getPixelsMoveX(myAccelerateInterpolar.getTransX(),flScreenWidth,true);
                float transY = myBounceInterpolator.getPixelsMoveX(myAccelerateInterpolar.getTransY(),flScreenHeight,true);
                ivCat.animate().translationXBy(transX);
                ivCat.animate().translationYBy(transY);
                ivCat.animate().setDuration(10000 / Math.abs(intCurrentInclination));
                ivCat.animate().start();
            }

        }
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
        flCatHeight=ivCat.getHeight();
        ivCat.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        intCatRight = (int) (flScreenWidth - flCatWidth);
        intCatBottom= (int) (flScreenHeight - flCatHeight*2);
        booldCatLoaded =true;
    }

    public float[] getPixelsTranslations(float ax, float ay,float posX,float posY){
        float[] tab_pixels_translation = new float[2];
        float abs_ax=Math.abs(ax);
        float abs_ay=Math.abs(ay);
        float val;

        if(abs_ax<abs_ay){

            if(ay>0){
                tab_pixels_translation[1]= flScreenHeight-posY-flCatHeight*2;

            }
            else if(ay<0){
                tab_pixels_translation[1]=  -posY;
            }
            val= Math.abs(tab_pixels_translation[1])*abs_ax/abs_ay;
            tab_pixels_translation[0]=  val;
            if(ax>0) {
                if(val>=posX){
                    tab_pixels_translation[0]=-posX;
                }
                else tab_pixels_translation[0]=-val;

            }
            else if(ax<0){
                if((posX+val)>=intCatRight){
                    tab_pixels_translation[0]= flScreenWidth-posX-flCatWidth;
                }
                else tab_pixels_translation[0]=val;
            }
            else tab_pixels_translation[0]=val;
        }
        else if(abs_ax>abs_ay){
            if(ax>0){
                tab_pixels_translation[0]= -posX;
            }
            if(ax<0){
                tab_pixels_translation[0]= flScreenWidth-posX-flCatWidth;
            }
            val= Math.abs(tab_pixels_translation[0])*abs_ay/abs_ax;

            if(ay<0) {
                if(val>=posY){
                    tab_pixels_translation[1]=-posY;
                }
                else tab_pixels_translation[1]=-val;

            }
            else if(ay>0){
                if((posY+val)>=intCatBottom){
                    tab_pixels_translation[1]= flScreenHeight-posY-flCatHeight*2;
                }
                else tab_pixels_translation[1]=val;
            }
            else tab_pixels_translation[1]=val;

        }
        else{
            if(ax>0){
                tab_pixels_translation[0]= -posX;
            }
            else if(ax==0){
                tab_pixels_translation[0]= 0;
            }
            else{
                tab_pixels_translation[0]= flScreenWidth-posX-flCatWidth;
            }
            if(ay>0){
                tab_pixels_translation[1]= flScreenHeight-posY-flCatHeight*2;
            }
            else if(ay==0){
                tab_pixels_translation[1]= 0;
            }
            else{
                tab_pixels_translation[1]=  -posY;
            }
        }
        translation= (int) tab_pixels_translation[0];
        droite= (int) (flScreenWidth-posX-flCatWidth);
        gauche=-ivCat.getX();
        fcty=Math.abs(tab_pixels_translation[1])*abs_ax/abs_ay;
        return tab_pixels_translation;

    }

}