package com.example.project1;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Edytuj extends AppCompatActivity {



    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    DatabaseReference rootRef;

    Spinner aktywnoscSpinner;
    Spinner celSpinner;
    Spinner plecSpinner;

    EditText waga;
    EditText wzrost;
    EditText wiek;
    Button zapis;

    String userId;

    String nazwaText;
    String adresText;
    String wagaText;
    String wzrostText;
    String wiekText;
    String plecText;
    String celText;
    String poziomText;

    int poziom;
    int cel;
    boolean plec;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edytuj);


        waga = (EditText) findViewById(R.id.editWaga);
        wzrost = (EditText) findViewById(R.id.editWzrost);
        wiek = (EditText) findViewById(R.id.editWiek);
        zapis = (Button) findViewById(R.id.wszyscyUzytkownicy);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(userId);
        rootRef = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy");

        aktywnoscSpinner = (Spinner) findViewById(R.id.spinnerAktywnosc);
        celSpinner = (Spinner) findViewById(R.id.spinnerCel);
        plecSpinner = (Spinner)findViewById(R.id.plecSpinner);

        ArrayAdapter<CharSequence> adapterPoziom = ArrayAdapter.createFromResource(this,R.array.Aktywność, android.R.layout.simple_spinner_item);
        adapterPoziom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aktywnoscSpinner.setAdapter(adapterPoziom);


        ArrayAdapter<CharSequence> adapterCel = ArrayAdapter.createFromResource(this,R.array.Cel, android.R.layout.simple_spinner_item);
        adapterCel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        celSpinner.setAdapter(adapterCel);

        ArrayAdapter<CharSequence> adapterPlec = ArrayAdapter.createFromResource(this,R.array.Płeć, android.R.layout.simple_spinner_item);
        adapterPoziom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plecSpinner.setAdapter(adapterPlec);





        aktywnoscSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    poziom = 1;
                }else if(position==1){
                    poziom = 2;
                }else if(position==2){
                     poziom = 3;
                }else if(position==3){
                     poziom = 4;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        celSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    cel = 1;
                }else if(position==1){
                    cel = 2;
                }else if(position==2) {
                    cel = 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        plecSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    plec = true;
                }else if(position==1){
                    plec = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                nazwaText = snapshot.child("nazwaUzytkownika").getValue().toString();
                adresText = snapshot.child("adresEmail").getValue().toString();
                wagaText = snapshot.child("waga").getValue().toString();
                wzrostText = snapshot.child("wzrost").getValue().toString();
                wiekText = snapshot.child("wiek").getValue().toString();
                plecText = snapshot.child("plec").getValue().toString();
                celText = snapshot.child("cel").getValue().toString();
                poziomText = snapshot.child("poziomAktywnosci").getValue().toString();
                wiek.setText(wiekText);

                float convertWaga = Float.parseFloat(wagaText);
                waga.setText(String.format("%.2f",convertWaga));

                float convertWzrost = Float.parseFloat(wzrostText);
                wzrost.setText(String.format("%.2f",convertWzrost));

                int poziomInt = Integer.parseInt(poziomText);
                int celInt = Integer.parseInt(celText);
                int poz;

                if(plecText.equals("true"))
                {
                     poz = 0;
                }else{
                    poz = 1;
                }


                celSpinner.setSelection(celInt-1);
                aktywnoscSpinner.setSelection(poziomInt-1);
                plecSpinner.setSelection(poz);



                zapis.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String wagaEditText = waga.getText().toString().trim();

                        if(TextUtils.isEmpty(wagaEditText)){
                            waga.setError("To pole nie może być puste!");
                            return;
                        }else if(Float.valueOf(wagaEditText).floatValue()<=0 ||  Float.valueOf(wagaEditText).floatValue()>250){
                            waga.setError("Podaj poprawną wagę");
                            return;
                        } else {
                            float wagaFloat = Float.valueOf(wagaEditText).floatValue();
                            databaseReference.child("waga").setValue(wagaFloat);
                        }

                        String wzrostEidtText = wzrost.getText().toString().trim();

                        if(TextUtils.isEmpty(wzrostEidtText)){
                            wzrost.setError("To pole nie może być puste!");
                            return;
                        }else if(Float.valueOf(wzrostEidtText).floatValue()<=0 ||  Float.valueOf(wzrostEidtText).floatValue()>221){
                            wzrost.setError("Podaj poprawny wzrost");
                            return;
                        }else {
                            float wzrostFloat = Float.valueOf(wzrostEidtText).floatValue();
                            databaseReference.child("wzrost").setValue(wzrostFloat);
                        }

                        String wiekEditText = wiek.getText().toString().trim();

                        if(TextUtils.isEmpty(wiekEditText)){
                            wiek.setError("To pole nie może być puste!");
                            return;
                        }else if(Integer.parseInt(wiekEditText)<=0  || Integer.parseInt(wiekEditText)>110){
                            wiek.setError("Podaj poprawny wiek");
                            return;
                        } else {
                            int wiekInt = Integer.parseInt(wiekEditText);
                            databaseReference.child("wiek").setValue(wiekInt);

                            String text_wiek_cel = String.valueOf(cel)+String.valueOf(wiekInt);
                            int cel_wiek = Integer.valueOf(text_wiek_cel);

                            databaseReference.child("wiek_cel").setValue(cel_wiek);
                        }


                        databaseReference.child("poziomAktywnosci").setValue(poziom);
                        databaseReference.child("cel").setValue(cel);
                        databaseReference.child("plec").setValue(plec);


                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







    }


}