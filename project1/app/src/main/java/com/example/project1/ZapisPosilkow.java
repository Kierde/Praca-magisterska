package com.example.project1;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZapisPosilkow extends AppCompatActivity {



    BottomNavigationView bottomNavigationView;
    FirebaseAuth auth;
    DatabaseReference databaseReferenceMain;
    ImageButton nastepnaData;
    ImageButton poprzedniaData;
    TextView data;
    String idZalogowanego;

    TextView label;

    Button dodajSniadanie;
    Button dodajObiad;
    Button dodajKolacje;
    Button dodajPrzekaski;
    Button dodajCwczenia;
    ImageButton dodajNotatke;

    SimpleDateFormat simpleDateFormat;


    GregorianCalendar dt1;

    RecyclerView sniadanie;
    private final List<Posilek> listaSniadania = new ArrayList<>();
    private PosilekAdapter sniadanieAdapter;

    RecyclerView obiad;
    private final List<Posilek> listaObiad = new ArrayList<>();
    private PosilekAdapter obiadAdapter;

    RecyclerView kolacja;
    private final List<Posilek> listaKolacja = new ArrayList<>();
    private PosilekAdapter kolacjaAdapter;

    RecyclerView przekaski;
    private final List<Posilek> listaPrzekaski = new ArrayList<>();
    private PosilekAdapter przekaskiAdapter;

    RecyclerView cwiczenia;
    private final List<Posilek> listaCwiczen = new ArrayList<>();
    private PosilekAdapter cwiczeniaAdapter;

    String userIdZnajomego;

    DatePickerDialog picker;
    Calendar calendar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zapis_posilkow);



        bottomNavigationView = findViewById(R.id.bottom_navigation2);
        bottomNavigationView.setSelectedItemId(R.id.home);




        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),
                                EkranGlowny.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),
                                ZapisPosilkow.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.wiecej:
                        startActivity(new Intent(getApplicationContext(),
                                Inne.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.Tablica:
                        startActivity(new Intent(getApplicationContext(),
                                Tablica.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });





        calendar = Calendar.getInstance();
        label = (TextView)findViewById(R.id.textView9);
        dodajNotatke = (ImageButton) findViewById(R.id.dodajNotatke);
        dodajSniadanie = (Button) findViewById(R.id.dodajSniadanie);
        dodajObiad = (Button) findViewById(R.id.dodajObiad);
        dodajKolacje = (Button) findViewById(R.id.dodajKolacje);
        dodajPrzekaski = (Button) findViewById(R.id.dodajPrzekaski);
        dodajCwczenia = (Button) findViewById(R.id.dodajCwiczenia);
        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();
        userIdZnajomego = getIntent().getStringExtra("userid");

        //patrzenie na zapis posiłków znajomego
        if(userIdZnajomego!=null) {
            idZalogowanego = userIdZnajomego;
            dodajNotatke.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
            dodajNotatke.setVisibility(View.GONE);
            dodajSniadanie.setVisibility(View.GONE);
            dodajObiad.setVisibility(View.GONE);
            dodajKolacje.setVisibility(View.GONE);
            dodajPrzekaski.setVisibility(View.GONE);
            dodajCwczenia.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);
        }

        databaseReferenceMain = FirebaseDatabase.getInstance().getReference();

        nastepnaData = (ImageButton) findViewById(R.id.nastepnyDzien);
        poprzedniaData = (ImageButton) findViewById(R.id.poprzedniDzien);
        data = (TextView) findViewById(R.id.data);

        //śniadanie
        sniadanie = (RecyclerView) findViewById(R.id.sniadanie);
        sniadanieAdapter = new PosilekAdapter(listaSniadania);
        sniadanie.setHasFixedSize(true);
        sniadanie.setLayoutManager(new LinearLayoutManager(this));
        sniadanie.setAdapter(sniadanieAdapter);
        //obiad
        obiad = (RecyclerView) findViewById(R.id.obiad);
        obiadAdapter = new PosilekAdapter(listaObiad);
        obiad.setHasFixedSize(true);
        obiad.setLayoutManager(new LinearLayoutManager(this));
        obiad.setAdapter(obiadAdapter);
        //kolacja
        kolacja = (RecyclerView) findViewById(R.id.kolacja);
        kolacjaAdapter = new PosilekAdapter(listaKolacja);
        kolacja.setHasFixedSize(true);
        kolacja.setLayoutManager(new LinearLayoutManager(this));
        kolacja.setAdapter(kolacjaAdapter);
        //przeksaski
        przekaski = (RecyclerView) findViewById(R.id.przekaski);
        przekaskiAdapter = new PosilekAdapter(listaPrzekaski);
        przekaski.setHasFixedSize(true);
        przekaski.setLayoutManager(new LinearLayoutManager(this));
        przekaski.setAdapter(przekaskiAdapter);
        //ćwiczenia
        cwiczenia = (RecyclerView)findViewById(R.id.cwiczenia);
        cwiczeniaAdapter = new PosilekAdapter(listaCwiczen);
        cwiczenia.setHasFixedSize(true);
        cwiczenia.setLayoutManager(new LinearLayoutManager(this));
        cwiczenia.setAdapter(cwiczeniaAdapter);



        //format daty
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dt1 = new GregorianCalendar();


        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GregorianCalendar calendar = new GregorianCalendar();
                int day = calendar.get(calendar.DAY_OF_MONTH);
                int month = calendar.get(calendar.MONTH);
                int year = calendar.get(calendar.YEAR);



                picker = new DatePickerDialog(ZapisPosilkow.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                
                                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                                gregorianCalendar.set(year, monthOfYear, dayOfMonth);
                               // long diff = ChronoUnit.DAYS.between(dt1.toInstant(),gregorianCalendar.toInstant());
                               //dt1.add(Calendar.DATE, (int) diff);
                                dt1.set(year, monthOfYear, dayOfMonth);
                                odswierzenieDanych();
                                data.setText(simpleDateFormat.format(gregorianCalendar.getTime()));
                            }
                        }, year, month, day);
                picker.show();

            }
        });

        // i=1 śniadanie
        wczytajPosilek("Sniadanie", 1);
        // i=2 obiad
        wczytajPosilek("Obiad", 2);
        //i=3 kolacja
        wczytajPosilek("Kolacja", 3);
        //i=4 przekaski
        wczytajPosilek("Przekaski", 4);
        //i=5 spalone kalorie podczas ćwiczeń
        wczytajPosilek("Cwiczenia", 5);


        String dataText = simpleDateFormat.format(dt1.getTime());
        data.setText(dataText);

        nastepnaData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dt1.add(Calendar.DATE, 1);

                String dataText = simpleDateFormat.format(dt1.getTime());
                sniadanieAdapter.zmienDate(dt1);
                obiadAdapter.zmienDate(dt1);
                kolacjaAdapter.zmienDate(dt1);
                przekaskiAdapter.zmienDate(dt1);
                cwiczeniaAdapter.zmienDate(dt1);



                data.setText(dataText);
                odswierzenieDanych();
            }
        });

        poprzedniaData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               dt1.add(Calendar.DATE, -1);

                String dataText = simpleDateFormat.format(dt1.getTime());
                sniadanieAdapter.zmienDate(dt1);
                obiadAdapter.zmienDate(dt1);
                kolacjaAdapter.zmienDate(dt1);
                cwiczeniaAdapter.zmienDate(dt1);
                przekaskiAdapter.zmienDate(dt1);


                data.setText(dataText);
                odswierzenieDanych();
            }
        });


        dodajSniadanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajPosilek("Sniadanie");
            }
        });


        dodajObiad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajPosilek("Obiad");
            }
        });

        dodajKolacje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajPosilek("Kolacja");
            }
        });

        dodajPrzekaski.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajPosilek("Przekaski");
            }
        });

        dodajCwczenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajPosilek("Cwiczenia");
            }
        });

        dodajNotatke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotatka();
            }
        });



    }

    private void dodajPosilek(String nazwaPosilku){

        final Dialog dialog = new Dialog(ZapisPosilkow.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        if(nazwaPosilku.equals("Sniadanie")||nazwaPosilku.equals("Obiad")||nazwaPosilku.equals("Kolacja")||nazwaPosilku.equals("Przekaski"))
        dialog.setContentView(R.layout.dodawanie_posilkow_dialog_custom);
        else
            dialog.setContentView(R.layout.dodawanie_cwiczen);

        final EditText nazwaPosi = dialog.findViewById(R.id.nazwaPosilkudialog);
        final EditText kalorycznosc = dialog.findViewById(R.id.iloscKaloriidiaog);
        final Button dodajPosilek = dialog.findViewById(R.id.dodajPosilekdiaglog);


        dodajPosilek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nazwaPosilkuText = nazwaPosi.getText().toString();
                String kalorycznoscText = kalorycznosc.getText().toString();

                if ((!TextUtils.isEmpty(nazwaPosilkuText)) && (!TextUtils.isEmpty(kalorycznoscText))) {



                    String posilekRef = "Dziennik_posilkow/" + idZalogowanego + "/" + simpleDateFormat.format(dt1.getTime()) + "/" + nazwaPosilku;
                    String posilekRef1 = "Wszystkie posilki uzytkownika do monitora posilkow" +"/"+ idZalogowanego + "/" + simpleDateFormat.format(dt1.getTime());


                    DatabaseReference push = databaseReferenceMain.child("Dziennik_posilkow")
                            .child(idZalogowanego).push();

                    DatabaseReference push1 = databaseReferenceMain.child("Wszystkie posilki uzytkownika do monitora posilkow")
                            .child(idZalogowanego).push();


                    String pushId = push.getKey();
                    String pushId1 = push1.getKey();

                    Map posilekMap = new HashMap();
                    posilekMap.put("nazwaPosilku", nazwaPosilkuText);
                    posilekMap.put("index", pushId);


                    if(nazwaPosilku.equals("Cwiczenia")) {
                        posilekMap.put("kalorycznosc", -Integer.parseInt(kalorycznoscText));
                    }else {
                        posilekMap.put("kalorycznosc", Integer.parseInt(kalorycznoscText));
                    }



                    Map czescPosilku = new HashMap();
                    czescPosilku.put(posilekRef + "/" + pushId, posilekMap);

                    Map wszystkieposilki = new HashMap();
                    wszystkieposilki.put(posilekRef1+"/"+pushId1, posilekMap);


                    dialog.dismiss();
                    databaseReferenceMain.updateChildren(czescPosilku, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                        }
                    });

                    databaseReferenceMain.updateChildren(wszystkieposilki, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                        }
                    });


                }
            }
        });

        dialog.show();

    }


    private void wczytajPosilek(String nazwaPosilku, int i) {


        databaseReferenceMain.child("Dziennik_posilkow").child(idZalogowanego).child(simpleDateFormat.format(dt1.getTime())).child(nazwaPosilku)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Posilek posilek = snapshot.getValue(Posilek.class);
                        if(i==1) {
                            listaSniadania.add(posilek);
                            sniadanieAdapter.notifyDataSetChanged();
                        }else if(i==2){
                            listaObiad.add(posilek);
                            obiadAdapter.notifyDataSetChanged();
                        }else if(i==3){
                            listaKolacja.add(posilek);
                            kolacjaAdapter.notifyDataSetChanged();
                        }else if(i==4){
                            listaPrzekaski.add(posilek);
                            przekaskiAdapter.notifyDataSetChanged();
                        }else if(i==5){
                            listaCwiczen.add(posilek);
                            cwiczeniaAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void openNotatka(){

        Intent intent = new Intent(ZapisPosilkow.this, Notatka.class );
        intent.putExtra("data", simpleDateFormat.format(dt1.getTime()));
        startActivity(intent);
    }

    public void odswierzenieDanych(){
        listaSniadania.clear();
        sniadanieAdapter.notifyDataSetChanged();
        listaObiad.clear();
        obiadAdapter.notifyDataSetChanged();
        listaKolacja.clear();
        kolacjaAdapter.notifyDataSetChanged();
        listaPrzekaski.clear();
        przekaskiAdapter.notifyDataSetChanged();
        listaCwiczen.clear();
        cwiczeniaAdapter.notifyDataSetChanged();
        wczytajPosilek("Sniadanie",1);
        wczytajPosilek("Obiad", 2);
        wczytajPosilek("Kolacja", 3);
        wczytajPosilek("Przekaski", 4);
        wczytajPosilek("Cwiczenia", 5);
    }










}

