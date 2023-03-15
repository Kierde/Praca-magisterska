package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.project1.WyszukanyPosilekAdapter.calculateHowMuchCalories;
import static com.example.project1.WyszukanyPosilekAdapter.calculateMacro;
import static java.lang.Float.parseFloat;

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

    Button wszukajSniadanie;
    Button wyszukajObiad;
    Button wyszukajKolacje;
    Button wyszukajPrzekaski;

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
        wszukajSniadanie = (Button) findViewById(R.id.wyszukajSniadanie);
        wyszukajObiad = (Button) findViewById(R.id.wyszukajObiad);
        wyszukajKolacje = (Button) findViewById(R.id.wyszukajKolacje);
        wyszukajPrzekaski = (Button) findViewById(R.id.wyszukajPrzekaski);

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

        wszukajSniadanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWyszukanie("Sniadanie");
            }
        });


        dodajObiad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajPosilek("Obiad");
            }
        });

        wyszukajObiad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWyszukanie("Obiad");
            }
        });

        dodajKolacje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajPosilek("Kolacja");
            }
        });

        wyszukajKolacje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWyszukanie("Kolacja");
            }
        });

        dodajPrzekaski.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajPosilek("Przekaski");
            }
        });

        wyszukajPrzekaski.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWyszukanie("Przekaski");
            }
        });

        dodajCwczenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajCwiczenie();
            }
        });

        dodajNotatke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotatka();
            }
        });
    }


    private void dodajCwiczenie(){

        final Dialog dialog = new Dialog(ZapisPosilkow.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dodawanie_cwiczen);

        final EditText nazwaCwiczenia = dialog.findViewById(R.id.nazwaCwiczenia);
        final EditText spaloneKalorie = dialog.findViewById(R.id.spaloneKalorie1);
        final Button dodajCwiczenie = dialog.findViewById(R.id.dodajCwiczenie1);


        dodajCwiczenie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nazwaCwiczeniaText=  nazwaCwiczenia.getText().toString().trim();
                String spaloneKalorieText = spaloneKalorie.getText().toString().trim();

                String posilekRef = "Dziennik_posilkow/" + idZalogowanego + "/" + simpleDateFormat.format(dt1.getTime()) + "/" + "Cwiczenia";
                String posilekRef1 = "Wszystkie posilki uzytkownika do monitora posilkow" +"/"+ idZalogowanego + "/" + simpleDateFormat.format(dt1.getTime());

                DatabaseReference push = databaseReferenceMain.child("Dziennik_posilkow")
                        .child(idZalogowanego).push();

                DatabaseReference push1 = databaseReferenceMain.child("Wszystkie posilki uzytkownika do monitora posilkow")
                        .child(idZalogowanego).push();

                String pushId = push.getKey();
                String pushId1 = push1.getKey();

                Map posilekMap = new HashMap();

                posilekMap.put("nazwaPosilku", nazwaCwiczeniaText);
                posilekMap.put("index", pushId);

                posilekMap.put("kalorycznosc", -Integer.parseInt(spaloneKalorieText));
                Map czescPosilku = new HashMap();
                czescPosilku.put(posilekRef + "/" + pushId, posilekMap);

                Map wszystkieposilki = new HashMap();
                wszystkieposilki.put(posilekRef1+"/"+pushId1, posilekMap);


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
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    private void dodajPosilek(String nazwaPosilku){

        final Dialog dialog = new Dialog(ZapisPosilkow.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dodawanie_posilkow_dialog_custom);

        final EditText nazwaPosi = dialog.findViewById(R.id.nazwaCwiczenia);
        final EditText kalorycznosc = dialog.findViewById(R.id.spaloneKalorie1);
        final EditText weglowodany = dialog.findViewById(R.id.iloscWeglodialog);
        final EditText bialko = dialog.findViewById(R.id.iloscBialkadialog);
        final EditText tluszcz = dialog.findViewById(R.id.iloscTluszczudialog);
        final EditText iloscGramDoDodania = dialog.findViewById(R.id.ilosc_produktu);
        final TextView iloscKcalDoDodania = dialog.findViewById(R.id.iloscKaloriiDoBazy);
        final Button dodajPosilek = dialog.findViewById(R.id.dodajCwiczenie1);


        iloscGramDoDodania.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if((!TextUtils.isEmpty(kalorycznosc.getText().toString().trim()))&&(!TextUtils.isEmpty(iloscGramDoDodania.getText().toString().trim()))){

                    float iloscProduktu =0;
                    int kalorycznoscProduktu=0;
                    iloscProduktu = parseFloat(iloscGramDoDodania.getText().toString().trim());
                    kalorycznoscProduktu = Integer.parseInt(kalorycznosc.getText().toString().trim());
                    int kcal =calculateHowMuchCalories(iloscProduktu,kalorycznoscProduktu);
                    iloscKcalDoDodania.setText(String.valueOf(kcal));
                }else{
                    iloscKcalDoDodania.setText(String.valueOf(0));
                }

            }
        });


        dodajPosilek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nazwaPosilkuText = nazwaPosi.getText().toString().trim();
                String kalorycznoscText = kalorycznosc.getText().toString().trim();
                String weglowodanyText = weglowodany.getText().toString().trim();
                String bialoText = bialko.getText().toString().trim();
                String tluszczText = tluszcz.getText().toString().trim();
                String iloscProduktu = iloscGramDoDodania.getText().toString().trim();


                if ((!TextUtils.isEmpty(nazwaPosilkuText)) && (!TextUtils.isEmpty(kalorycznoscText))
                        && !TextUtils.isEmpty(weglowodanyText) && !TextUtils.isEmpty(bialoText) && !TextUtils.isEmpty(tluszczText)) {

                    databaseReferenceMain.child("Baza_posilkow_uzytkonikow").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (!snapshot.hasChild(nazwaPosilkuText)){

                                DatabaseReference referencePosilek = databaseReferenceMain.child("Dziennik_posilkow").child(idZalogowanego).child(simpleDateFormat.format(dt1.getTime()))
                                        .child(nazwaPosilku);
                                String index = referencePosilek.push().getKey();

                                float bialko = calculateMacro(parseFloat(iloscProduktu), parseFloat(bialoText));
                                float weglowodany=calculateMacro(parseFloat(iloscProduktu), parseFloat(weglowodanyText));
                                float tluszcz=calculateMacro(parseFloat(iloscProduktu), parseFloat(tluszczText));
                                Posilek posilek = new Posilek(Integer.parseInt(iloscKcalDoDodania.getText().toString().trim()), nazwaPosilkuText, index, bialko, weglowodany, tluszcz);
                                referencePosilek.child(index).setValue(posilek);

                                //baza danych dla wszystkich użytkoników
                                DatabaseReference dlaWszystkichUzytkownikow = databaseReferenceMain.child("Baza_posilkow_uzytkonikow");
                                Posilek posilek1 = new Posilek(Integer.parseInt(kalorycznoscText), nazwaPosilkuText, index, Float.parseFloat(bialoText), Float.parseFloat(weglowodanyText), Float.parseFloat(tluszczText));
                                dlaWszystkichUzytkownikow.child(nazwaPosilkuText).setValue(posilek1);


                                String posilekRef1 = "Wszystkie posilki uzytkownika do monitora posilkow" + "/" + idZalogowanego + "/" + simpleDateFormat.format(dt1.getTime());
                                DatabaseReference push1 = databaseReferenceMain.child("Wszystkie posilki uzytkownika do monitora posilkow")
                                        .child(idZalogowanego).push();

                                String pushId1 = push1.getKey();

                                Map posilekMap = new HashMap();
                                posilekMap.put("nazwaPosilku", nazwaPosilkuText);
                                posilekMap.put("index", pushId1);

                                posilekMap.put("kalorycznosc", Integer.parseInt(kalorycznoscText));



                                Map wszystkieposilki = new HashMap();
                                wszystkieposilki.put(posilekRef1 + "/" + pushId1, posilekMap);


                                dialog.dismiss();

                                databaseReferenceMain.updateChildren(wszystkieposilki, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                    }
                                });
                            }else{
                                nazwaPosi.setError("Posiłek o podanej nazwie istnieje już w bazie danych");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

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

    public void openWyszukanie(String nazwaPosilku){

        Intent intent = new Intent(ZapisPosilkow.this,WyszukaneBazaPosilkow.class);
        intent.putExtra("nazwaPosilku", nazwaPosilku);
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

