package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Poziom extends AppCompatActivity {

    Button brak;
    Button mala;
    Button duza;
    Button bardzoDuza;

    static int poziom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poziom);

        brak = (Button) findViewById(R.id.buttonBrak);
        mala = (Button) findViewById(R.id.buttonMała);
        duza = (Button) findViewById(R.id.buttonDuża);
        bardzoDuza = (Button) findViewById(R.id.buttonBardzoDuża);


        brak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poziom = 1;
                finish();
                openInfoActivity();
            }
        });

        mala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poziom = 2;
                finish();
                openInfoActivity();
            }
        });

        duza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poziom = 3;
                finish();
                openInfoActivity();
            }
        });

        bardzoDuza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poziom = 4;
                finish();
                openInfoActivity();
            }
        });


    }

    public void openInfoActivity(){
        Intent intent = new Intent(this, InformacjeFizyczne.class);
        startActivity(intent);
    }

    public static int getPoziom(){
        return poziom;
    }



}