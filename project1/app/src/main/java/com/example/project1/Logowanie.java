package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.nio.channels.NonReadableChannelException;

public class Logowanie extends AppCompatActivity {

    EditText adresEmail;
    EditText haslo;
    TextView rejestruj;
    TextView przypomnij;
    Button loguj;
    FirebaseAuth FAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logowanie);

        adresEmail = (EditText) findViewById(R.id.editTextEmail1);
        haslo = (EditText) findViewById(R.id.editTextHaslo1);
        rejestruj = (TextView) findViewById(R.id.textLinkToReje);
        przypomnij = (TextView) findViewById(R.id.textViewPrzypomnij);
        loguj = (Button) findViewById(R.id.bntLoguj);

        FAuth = FirebaseAuth.getInstance();

        loguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String adresEmail_ = adresEmail.getText().toString().trim();
                String haslo_ = haslo.getText().toString().trim();

                if (TextUtils.isEmpty(adresEmail_)) {
                    adresEmail.setError("Adres e-mail jest wymagany");
                    return;
                }
                if (TextUtils.isEmpty(haslo_)) {
                    haslo.setError("Hasło jest wymagane");
                    return;
                }

                //autoryzacja użytkownika
                FAuth.signInWithEmailAndPassword(adresEmail_, haslo_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Logowanie.this, "Zalogowałeś się pomyślnie", Toast.LENGTH_SHORT).show();
                            openZapisPosilkow();
                        }else {
                            Toast.makeText(Logowanie.this, "Poddałeś zły adres e-mail lub hasło", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //przejście do rejestracji

        rejestruj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRejActivity();

            }
        });

        //przypomnienie hasla

        przypomnij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView emailRes = new EditText(v.getContext());
                AlertDialog.Builder hasloResetDialog = new AlertDialog.Builder(v.getContext());
                hasloResetDialog.setTitle("Reset hasła");
                hasloResetDialog.setMessage("Podaj e-mail żeby otrzymać link do resetu hasła");
                hasloResetDialog.setView(emailRes);

                hasloResetDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String rMail = emailRes.getText().toString();
                        FAuth.sendPasswordResetEmail(rMail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Logowanie.this,"Link do resetu hasła został wysłany", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Logowanie.this,"Wiadomość z linkiem nie została wysłana" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                hasloResetDialog.setNegativeButton("wróć", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                hasloResetDialog.create().show();
            }
        });

    }

    public void openRejActivity () {
        Intent intent = new Intent(this, Rejestracja.class);
        startActivity(intent);
    }

    public void openZapisPosilkow(){
        Intent intent = new Intent(this,DaneUzytkownika.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }


}