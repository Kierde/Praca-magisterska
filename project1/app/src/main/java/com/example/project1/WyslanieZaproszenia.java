package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WyslanieZaproszenia extends AppCompatActivity {


    DatabaseReference databaseReference;

    FirebaseAuth mAuth;
    TextView nazwaUzytkownika;
    TextView wiek;
    TextView cel;
    TextView poziomRuchu;
    ImageView profilowe;

    String textNazwa;
    String textProfilowe;
    String textWiek;
    String textPoziom;
    String textCel;

    DatabaseReference databaseReferenceZaproszenia;
    Button zapros;
    Button odrzuc;
    String stanZnajomości;
    String idZalogowanego;
    DatabaseReference databaseReferenceZnajomi;


    String useridZapraszany;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wyslanie_zaproszenia);

        //id wyświetlanego użytkoniwnika do zaproszenia
        useridZapraszany = getIntent().getStringExtra("userid");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(useridZapraszany);
        databaseReferenceZaproszenia = FirebaseDatabase.getInstance().getReference().child("Zaproszenia do znajomych");
        databaseReferenceZnajomi = FirebaseDatabase.getInstance().getReference().child("Znajomi");

        //id zalogowanego użytkownika
        mAuth = FirebaseAuth.getInstance();
        idZalogowanego  = mAuth.getUid();

        nazwaUzytkownika = (TextView) findViewById(R.id.nazwaZap);
        wiek = (TextView) findViewById(R.id.wiekZap);
        cel = (TextView) findViewById(R.id.celZap);
        poziomRuchu = (TextView) findViewById(R.id.poziomZap);
        profilowe = (ImageView) findViewById(R.id.profZap);

        zapros = (Button) findViewById(R.id.buttonZapros);
        odrzuc = (Button) findViewById(R.id.odrzucZaproszenie);
        odrzuc.setVisibility(View.GONE);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                 textNazwa = snapshot.child("nazwaUzytkownika").getValue().toString();
                 textProfilowe = snapshot.child("zdjecieProfilowe").getValue().toString();
                 textPoziom = snapshot.child("poziomAktywnosci").getValue().toString();
                 textCel = snapshot.child("cel").getValue().toString();
                 textWiek = snapshot.child("wiek").getValue().toString();


                if(!textProfilowe.equals(""))
                    Picasso.get().load(textProfilowe).into(profilowe);

                    nazwaUzytkownika.setText("Nazwa użytwkonika: "+ textNazwa);
                    wiek.setText("Wiek: "+textWiek+ " lat");


                    if(textCel.equals("1")){
                        cel.setText("Status diety: utracenie wagi ");
                    }else if(textCel.equals("2")){
                        cel.setText("Status diety: utrzymanie wagi");
                    }else if(textCel.equals("3")){
                        cel.setText("Status diety: przybranie na wadze");
                    }

                    if(textPoziom.equals("1")){
                        poziomRuchu.setText("Poziom aktywności fizycznej: siedzący");
                    }else if(textCel.equals("2")){
                        poziomRuchu.setText("Poziom aktywności fizycznej: mało aktywny");
                    }else if(textCel.equals("3")){
                        poziomRuchu.setText("Poziom aktywności fizycznej: aktywny");
                    }else if(textCel.equals("4")){
                        poziomRuchu.setText("Poziom aktywności fizycznej: bardzo aktywny");
                    }

                    //sprwadzenie czy dany użytkownik którego chciałbym zaprosić nie zaprosił mnie wcześniej
                    databaseReferenceZaproszenia.child(idZalogowanego).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            if(snapshot.hasChild(useridZapraszany)){

                                String stan_zaproszenia = snapshot.child(useridZapraszany).child("stan_zaproszenia").getValue().toString();
                                if(stan_zaproszenia.equals("otrzymane")) {
                                    stanZnajomości = "prośba_otrzymana";
                                    zapros.setText("Akceptuj zaproszenie");
                                    odrzuc.setVisibility(View.VISIBLE);
                                }
                            }else{
                                stanZnajomości = "nie_znajomi";
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                //sprwadzenie czy już nie są znajomymi - usuwanie znajomych
                databaseReferenceZnajomi.child(idZalogowanego).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChild(useridZapraszany)){
                            stanZnajomości = "znajomi";
                            zapros.setText("Usuń znajomego");
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        // odczytanie czy użytkownik został już wcześniej zaproszony, przycik zaproś lub analuj
        databaseReferenceZaproszenia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                if(snapshot1.child(idZalogowanego).hasChild(useridZapraszany)) {

                    String stanZaproszenia = snapshot1.child(idZalogowanego).child(useridZapraszany).
                            child("stan_zaproszenia").getValue().toString();

                        if (stanZaproszenia.equals("wysłane")) {
                            stanZnajomości = "prośba_wysłana";
                            zapros.setText("Anuluj zaproszenie do znajomych");
                        }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        zapros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                zapros.setEnabled(false);

                //stan znajomości - nie znajomi
                if(stanZnajomości.equals("nie_znajomi")){

                    databaseReferenceZaproszenia.child(idZalogowanego).
                            child(useridZapraszany).child("stan_zaproszenia").
                            setValue("wysłane").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            databaseReferenceZaproszenia.child(useridZapraszany).
                                    child(idZalogowanego).child("stan_zaproszenia")
                                    .setValue("otrzymane").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    zapros.setEnabled(true);
                                    stanZnajomości = "prośba_wysłana";
                                    zapros.setText("Anuluj zaproszenie do znajomych");
                                }
                            });
                        }
                    });
                }

                //stan znajomości  "prośba wysłana"
                if(stanZnajomości.equals("prośba_wysłana")){

                    databaseReferenceZaproszenia.child(idZalogowanego).
                            child(useridZapraszany).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            databaseReferenceZaproszenia.child(useridZapraszany).
                                    child(idZalogowanego).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    zapros.setEnabled(true);
                                    stanZnajomości = "nie_znajomi";
                                    zapros.setText("Zaproś do znajomych");

                                }
                            });
                        }
                    });

                }

                if(stanZnajomości.equals("prośba_otrzymana")){

                    SimpleDateFormat SDFormat = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = new Date();
                    String data = SDFormat.format(date);
                        ///dodanie do bazy danych do zalogowanego - zapraszanego jako przyjeciela
                    databaseReferenceZnajomi.child(idZalogowanego).child(useridZapraszany).child("data").setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            /// dodanie do baazy danych do zapraszanego - zalogowanego jako przyjeciela
                            databaseReferenceZnajomi.child(useridZapraszany).child(idZalogowanego).child("data").setValue(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            databaseReferenceZaproszenia.child(idZalogowanego).
                                                    child(useridZapraszany).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            databaseReferenceZaproszenia.child(useridZapraszany).
                                                                    child(idZalogowanego).removeValue()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            zapros.setEnabled(true);
                                                                            stanZnajomości = "znajomi";
                                                                            zapros.setText("Usuń ze znajomych");
                                                                            odrzuc.setVisibility(View.GONE);

                                                                        }
                                                                    });
                                                        }
                                                    });



                                        }
                                    });


                        }
                    });

                }

                if(stanZnajomości.equals("znajomi")){

                    databaseReferenceZnajomi.child(idZalogowanego).
                            child(useridZapraszany).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    databaseReferenceZnajomi.child(useridZapraszany).
                                            child(idZalogowanego).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    zapros.setEnabled(true);
                                                    stanZnajomości = "nie_znajomi";
                                                    zapros.setText("Zaproś do znajomych");

                                                }
                                            });
                                }
                            });
                }

            }
        });



        odrzuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(stanZnajomości.equals("prośba_otrzymana")){

                    databaseReferenceZaproszenia.child(idZalogowanego).
                            child(useridZapraszany).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReferenceZaproszenia.child(useridZapraszany).
                                    child(idZalogowanego).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    zapros.setEnabled(true);
                                    stanZnajomości = "nie_znajomi";
                                    zapros.setText("Zaproś do znajomych");
                                    odrzuc.setVisibility(View.GONE);
                                }
                            });
                        }
                    });

                }

            }
        });




    }
}