package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


public class Notatka extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference referenceNotatka;
    String idZalogowanego;
    String data;
    String stringNotatka;
    Button zapiszNotatke;
    TextView notatkaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notatka);

        data =  getIntent().getStringExtra("data");
        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();
        zapiszNotatke = (Button) findViewById(R.id.zapiszNotatke);
        notatkaText = (TextView) findViewById(R.id.notatkaText);

        referenceNotatka = FirebaseDatabase.getInstance().getReference().child("Notatki").child(idZalogowanego).child(data);



        referenceNotatka.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChild("notatkaTekstowa")) {
                    stringNotatka = snapshot.child("notatkaTekstowa").getValue().toString();
                    if (!stringNotatka.equals(""))
                        notatkaText.setText(stringNotatka);
                }


                zapiszNotatke.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        stringNotatka = notatkaText.getText().toString();

                        if(!stringNotatka.equals(""))
                        referenceNotatka.child("notatkaTekstowa").setValue(stringNotatka);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}