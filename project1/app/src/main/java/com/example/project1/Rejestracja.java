package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class Rejestracja extends AppCompatActivity{

    EditText nazwaUzytkownika;
    EditText adresEmail;
    EditText haslo;
    TextView loguj;
    Button rejestruj;
    FirebaseAuth FAuth;
    DatabaseReference refRoot;
    static String nazwaUzytkownika_;
    static String adresEmail_;
    static String haslo_;
    boolean error;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rejestracja);
        
        FAuth = FirebaseAuth.getInstance();

        if(FAuth.getCurrentUser()!=null){
            Intent intent = new Intent(this,ZapisPosilkow.class);
            startActivity(intent);
        }





        nazwaUzytkownika = (EditText) findViewById(R.id.editTextNazwaUżytkownika);
        adresEmail = (EditText) findViewById(R.id.editTextAdresEmail);
        haslo = (EditText) findViewById(R.id.editTextHaslo);
        loguj = (TextView) findViewById(R.id.textLinkToLog);
        rejestruj = (Button) findViewById(R.id.bntRejestruj);

        refRoot = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy");

        FAuth = FirebaseAuth.getInstance();

        rejestruj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nazwaUzytkownika_ = nazwaUzytkownika.getText().toString().trim();
                adresEmail_ = adresEmail.getText().toString().trim();
                haslo_ = haslo.getText().toString().trim();


                if(TextUtils.isEmpty(nazwaUzytkownika_)){
                    nazwaUzytkownika.setError("Nazwa użytkownika  jest wymagana");
                    return;
                }else {
                    refRoot.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                         if(snapshot.hasChild("nazwaUzytkownika")){

                             for(DataSnapshot ds :snapshot.getChildren()) {

                                 if (ds.child("nazwaUzytkownika").getValue().toString().equals(nazwaUzytkownika_)) {
                                     nazwaUzytkownika.setError("Podana nazwa użytkownika jest już zajęta");
                                     error = false;
                                     break;
                                 } else {
                                     error = true;
                                 }
                             }
                         }else {
                             error=true;
                         }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }



                if(TextUtils.isEmpty(adresEmail_)){
                    adresEmail.setError("Adres e-mail jest wymagany");
                    return;
                }
                if(TextUtils.isEmpty(haslo_)){
                    haslo.setError("Hasło jest wymagane");
                    return;
                }


                if(haslo_.length()<6)
                {
                    haslo.setError("Hasło musi być mieć co najmniej 6 znaków");
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(adresEmail_).matches()){
                    adresEmail.setError("Format adresu e-mail jest nieprawidłowy");
                    return;
                }

                if (error) {

                    FAuth.fetchSignInMethodsForEmail(adresEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                            if (task.getResult().getSignInMethods().size() == 0) {
                                openCelActivity();
                                finish();
                            } else {
                                Toast.makeText(Rejestracja.this, "Podany adres E-mail jest już zajęty", Toast.LENGTH_SHORT).show();
                                adresEmail.setError("Adres jest już zajęty");
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });


                }


            }
        });



        loguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogActivity();
            }
        });
    }


    public void openLogActivity(){
        Intent intent = new Intent(this, Logowanie.class);
        startActivity(intent);
    }

    public void openCelActivity(){
        Intent intent = new Intent(this, Cel.class);
        startActivity(intent);
    }

    static public String getNazwaUzytkownika() {
        return nazwaUzytkownika_;
    }

    static public String getadresEmail() {
        return adresEmail_;
    }

    static public String getHaslo(){return  haslo_;}


    @Override
    public void onBackPressed() {
    }




}










