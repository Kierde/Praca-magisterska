package com.example.project1;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

public class Inne extends AppCompatActivity {

    Button  daneUzytkownika;
    Button wylogujSie;
    Button wszyscyUzytkownicy;
    Button spolecznosc;
    Button inneWykresy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inne);

        daneUzytkownika = (Button) findViewById(R.id.informacjeProfil);
        wylogujSie = (Button) findViewById(R.id.wylogujSie);
        wszyscyUzytkownicy = (Button) findViewById(R.id.wszyscyUzytkownicy);
        spolecznosc = (Button) findViewById(R.id.spolecznosc);
        inneWykresy = (Button) findViewById(R.id.inneWykresy);

        daneUzytkownika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDaneActivity();
            }
        });

        wylogujSie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                wyslijDoLogowania();

            }
        });

        spolecznosc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSpolecznosc();
            }
        });



        wszyscyUzytkownicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWszyscyUzytkownicyActivity();
            }
        });


        inneWykresy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opeInneWykresy();
            }
        });


    }


    public void opeInneWykresy(){

        Intent intent = new Intent(this, KolejneWykresyTestActivity.class);
        startActivity(intent);

    }


    public void openDaneActivity(){

        Intent intent = new Intent(this, DaneUzytkownika.class);
        startActivity(intent);
    }

    public void openWszyscyUzytkownicyActivity(){

        Intent intent = new Intent(this, WszyscyUzytkownicy.class);
        startActivity(intent);
    }

    public void openSpolecznosc(){

        Intent intent = new Intent(this, KomunikatorUzytkownika.class);
        startActivity(intent);
    }


    public void wyslijDoLogowania(){
        Intent intent = new Intent(this, Logowanie.class);
        startActivity(intent);
        finish();
    }








}