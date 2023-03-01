package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.BoringLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.project1.Cel.cel;
import static com.example.project1.Cel.getCel;
import static com.example.project1.Poziom.getPoziom;
import static com.example.project1.Rejestracja.getNazwaUzytkownika;
import static com.example.project1.Rejestracja.getadresEmail;
import static com.example.project1.Rejestracja.getHaslo;
public class InformacjeFizyczne extends AppCompatActivity {

    EditText wiek;
    EditText waga;
    EditText wzrost;
    Button zapisz;

    RadioGroup wyborPlci;
    RadioButton kobieta;
    RadioButton mezczyzna;
    FirebaseAuth FAuth;

    Boolean plec;

    DatabaseReference databaseReference;
    DatabaseReference databaseRefWaga;

    FirebaseAuth mAuth;
    Date date;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacje_fizyczne);

        date = new Date();
        databaseReference = FirebaseDatabase.getInstance().getReference("Uzytkownicy");
        FAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();

        wiek = (EditText) findViewById(R.id.textDlaWieku);
        waga = (EditText) findViewById(R.id.textDlaWagi);
        wzrost = (EditText) findViewById(R.id.textDlaWzrostu);
        zapisz = (Button) findViewById(R.id.buttonZapisz);


         kobieta   = (RadioButton) findViewById(R.id.radioButtonKob);
         mezczyzna = (RadioButton) findViewById(R.id.radioButtonMen);
         wyborPlci = (RadioGroup) findViewById(R.id.radioGroup);



        wyborPlci.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.radioButtonKob:
                        plec = false;
                        break;

                    case R.id.radioButtonMen:
                        plec = true;
                        break;
                    case -1:
                        plec = true;
                        break;
                }
            }
        });




        zapisz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String wiek_ = wiek.getText().toString();
                String waga_ = waga.getText().toString();
                String wzrost_ = wzrost.getText().toString();
                int wiekInt;
                float wagaFloat;
                float wzrostFloat;



                if(TextUtils.isEmpty(wiek_))
                {
                    wiek.setError("Podaj poprawny wiek");
                    return;
                }
                else if(Integer.parseInt(wiek_)<=0  || Integer.parseInt(wiek_)>110){
                    wiek.setError("Podaj poprawny wiek");
                    return;
                }
                else {
                        wiekInt = Integer.parseInt(wiek_);
                }


                if(TextUtils.isEmpty(wzrost_)){
                    wzrost.setError("Podaj poprawny wzrost");
                    return;
                } else if( Float.valueOf(wzrost_).floatValue()<=0 ||  Float.valueOf(wzrost_).floatValue()>221) {
                    wzrost.setError("Podaj poprawny wzrost");
                    return;
                }
                else {
                    wzrostFloat = Float.valueOf(wzrost_).floatValue();
                }


                if(TextUtils.isEmpty(waga_)){
                    waga.setError("Podaj poprawną wagę");
                    return;
                }
                else if( Float.valueOf(waga_).floatValue()<=0 ||  Float.valueOf(waga_).floatValue()>250) {
                    waga.setError("Podaj poprawną wagę");
                    return;
                }
                else {
                        wagaFloat = Float.valueOf(waga_).floatValue();
                }

                FAuth.createUserWithEmailAndPassword(getadresEmail(),getHaslo()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String userId = mAuth.getUid();
                            String wiek_cel_text = String.valueOf(getCel()) + String.valueOf(wiekInt);
                            int wiek_cel = Integer.valueOf(wiek_cel_text);
                            Toast.makeText(InformacjeFizyczne.this,"Uzytkownik zostal utworzyony pomyslnie", Toast.LENGTH_SHORT).show();
                            Uzytkownik nowy = new Uzytkownik(getNazwaUzytkownika(), getadresEmail(),"", wiekInt, wagaFloat, wzrostFloat, plec, getPoziom(), getCel(),wiek_cel);
                            databaseReference.child(userId).setValue(nowy);

                            String pushId = databaseReference.push().getKey();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Waga waga = new Waga(simpleDateFormat.format(date.getTime()), wagaFloat, pushId);

                            databaseRefWaga = FirebaseDatabase.getInstance().getReference("Zmiany w wadze").child(userId);


                            databaseRefWaga.child(pushId).setValue(waga);
                            openDaneActivity();

                        }else {
                            Toast.makeText(InformacjeFizyczne.this,"Blad", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

    }

    public void openDaneActivity(){
        Intent intent = new Intent(this,DaneUzytkownika.class);
        startActivity(intent);
    }







}