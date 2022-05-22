package com.example.animatecat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.animatecat.bean.MovingCat;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    //composants graphiques
    private EditText ed_name;
    private EditText ed_poids;
    private ImageView iv_cat1;
    private ImageView iv_cat2;
    private ImageView iv_cat3;
    private Button bt_valider;

    //donnees
    private MovingCat movingCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        ed_name=findViewById(R.id.editTextTextPersonName);
        ed_poids=findViewById(R.id.editTextNumber);
        iv_cat1=findViewById(R.id.id_cat1);
        iv_cat1.setOnClickListener(this);
        iv_cat2=findViewById(R.id.id_cat2);
        iv_cat2.setOnClickListener(this);
        iv_cat3=findViewById(R.id.id_cat3);
        iv_cat3.setOnClickListener(this);
        bt_valider=findViewById(R.id.button);
        bt_valider.setOnClickListener(this);


        //Instanciation
        movingCat =new MovingCat(2,"Possum");

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.id_cat1:
                movingCat.setPicture_cat(R.drawable.cat);
                break;
            case R.id.id_cat2:
                movingCat.setPicture_cat(R.drawable.cat2);
                break;
            case R.id.id_cat3:
                movingCat.setPicture_cat(R.drawable.cat3);
                break;
            case R.id.button:
                if((""+ed_name.getText()).length()>0) {
                    movingCat.setName("" + ed_name.getText());
                }
                if((""+ed_poids.getText()).length()>0) {
                    movingCat.setPoids(Float.valueOf("" + ed_poids.getText()).floatValue());
                }
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra(com.example.animatecat.MainActivity.NIOU, movingCat);
                startActivity(intent);
                break;
        }

    }
}