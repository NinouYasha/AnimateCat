package com.example.animatecat.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.animatecat.R;


// On cree ici une classe etendue de View qui implemente SensorEventListener
// la solution choisie est plutot de faire en sorte que MainActivity implemente SensorEventListener
// Si on utilisait cette classe view il faudrait ajouter ce composant cree en utilisant le composant View dans mainactivity.xml
// Cette solution consiste a redessiner l image chaque fois que les variables x et y de position changent
// On va plutot translater limageview chaque fois que x et y changent, cela demande par contre de maitriser la vitesse


public class Cat extends View implements SensorEventListener {

    //Sorte de pinceau, celui la pour une image
    private Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap little_cat;

    private int longueur;
    private int largeur;
    private int positionX;
    private int positionY;

    //
    public Cat(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // invoquée automatiquement par le système à chaque
    //fois qu’il a besoin d’afficher ou de rafraichir le View
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // placer limage sur le compo graph au centre
        canvas.drawBitmap(little_cat,positionX,positionY,p);
    }

    // invoquee une seule fois car on a bloque le mode portrait
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // On recupere limage
        little_cat= BitmapFactory.decodeResource(getResources(), R.drawable.cat);


        largeur=little_cat.getWidth();
        longueur=little_cat.getHeight();

        //calcul du centre
        // w et h representent largeur et longueur de tout le compo graph
        positionX=(w-largeur)/2;
        positionY=(h-longueur)/2;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x=sensorEvent.values[0];
        float y=sensorEvent.values[1];
        // si on suit cet exemple, calculer en fonction une position (X,Y) qu'on mettra dans  positionX et positionY
        // onDraw repositionnera limage
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
