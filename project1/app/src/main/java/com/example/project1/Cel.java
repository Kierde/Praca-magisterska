package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class Cel extends AppCompatActivity {

    Button przybierzWage;
    Button utrzymajWage;
    Button utracWage;
    static int cel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cel);
        przybierzWage = (Button) findViewById(R.id.buttonPrzybierz);
        utrzymajWage = (Button) findViewById(R.id.buttonUtrzymaj);
        utracWage = (Button) findViewById(R.id.buttonZmniejsz);


        utrzymajWage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cel = 2;
                finish();
                openPoziom();

            }
        });
        przybierzWage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cel = 3;
                finish();
                openPoziom();


            }
        });

        utracWage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cel = 1;
                finish();
                openPoziom();


            }
        });


    }

    public void openPoziom(){
        Intent intent = new Intent(this, Poziom.class);
        startActivity(intent);
    }

    static public int getCel() {
        return cel;
    }




}