package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.VectorEnabledTintResources;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;


import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.StackedBarChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.StackedBarModel;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;


public class InneWykresy extends AppCompatActivity {
    

    StackedBarChart mStackedBarChart;
    PieChart mPieChart;
    DatabaseReference rootRef;
    String idZalogowanego;
    FirebaseAuth auth;
    Toolbar zmianaDanych;
    TextView nazwaOkresu;


    TextView dataStart;
    TextView dataStop;
    DatePickerDialog picker;

    GregorianCalendar gregorianCalendarOdStacked;
    SimpleDateFormat simpleDateFormat;

    GregorianCalendar gregorianCalendarStart;
    GregorianCalendar gregorianCalendarStop;






    int sumaKalSniadanie = 0;
    int sumaKalObiad = 0;
    int sumaKalKolacja = 0;
    int sumaKalPrzekaski = 0;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inne_wykresy);



        dataStart = (TextView) findViewById(R.id.dataStart);
        dataStop = (TextView) findViewById(R.id.dataStop);


        zmianaDanych = (Toolbar) findViewById(R.id.zmianaDanych);
        zmianaDanych.inflateMenu(R.menu.menu_czas);

        nazwaOkresu = (TextView) findViewById(R.id.labelNazwaOkresu);


        mStackedBarChart = (StackedBarChart) findViewById(R.id.stackedbarchart);
        mPieChart = (PieChart) findViewById(R.id.wykreskolowy);
        rootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();
        gregorianCalendarOdStacked = new GregorianCalendar();

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        wyswietlWykresStaked(7,gregorianCalendarOdStacked,true);

        GregorianCalendar[] gregorianCalendars = new GregorianCalendar[7];

        for(int j = 0; j<7; j++){
            GregorianCalendar gr = new GregorianCalendar();
            gregorianCalendars[j] = gr;
            gr.add(Calendar.DATE, -j);
        }

        wczytajOkresCzasuPie(gregorianCalendars);


        dataStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GregorianCalendar calendar = new GregorianCalendar();

                int day = calendar.get(calendar.DAY_OF_MONTH);
                int month = calendar.get(calendar.MONTH);
                int year = calendar.get(calendar.YEAR);

                picker = new DatePickerDialog(InneWykresy.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                gregorianCalendarStart = new GregorianCalendar();
                                gregorianCalendarStart.set(year, monthOfYear, dayOfMonth);

                                dataStart.setText(simpleDateFormat.format(gregorianCalendarStart.getTime()));

                            }
                        }, year, month, day);
                picker.show();


            }
        });



        dataStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GregorianCalendar calendar = new GregorianCalendar();

                int day = calendar.get(calendar.DAY_OF_MONTH);
                int month = calendar.get(calendar.MONTH);
                int year = calendar.get(calendar.YEAR);

                picker = new DatePickerDialog(InneWykresy.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                gregorianCalendarStop = new GregorianCalendar();
                                gregorianCalendarStop.set(year, monthOfYear, dayOfMonth);

                                dataStop.setText(simpleDateFormat.format(gregorianCalendarStop.getTime()));

                            }
                        }, year, month, day);
                picker.show();

            }
        });







        zmianaDanych.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.ostatniTydzien){

                    nazwaOkresu.setText("Ostatni tydzień");

                    mStackedBarChart.clearChart();
                    GregorianCalendar gr1 = new GregorianCalendar();
                    wyswietlWykresStaked(7,gr1,true);
                    mStackedBarChart.startAnimation();

                    mPieChart.clearChart();

                    GregorianCalendar[] gregorianCalendars = new GregorianCalendar[7];

                    for(int j = 0; j<7; j++){
                        GregorianCalendar gr = new GregorianCalendar();
                        gregorianCalendars[j] = gr;
                        gr.add(Calendar.DATE, -j);
                    }

                    wczytajOkresCzasuPie(gregorianCalendars);


                    mPieChart.startAnimation();


                }else if(item.getItemId()==R.id.ostatnieTrzydziesciDni){

                    nazwaOkresu.setText("Ostatnie 30 dni");

                    mStackedBarChart.clearChart();
                    GregorianCalendar gr2 = new GregorianCalendar();
                    wyswietlWykresStaked(30,gr2,true);

                    mStackedBarChart.startAnimation();


                    mPieChart.clearChart();

                    GregorianCalendar[] gregorianCalendars = new GregorianCalendar[30];

                    for(int j = 0; j<30; j++){
                        GregorianCalendar gr = new GregorianCalendar();
                        gregorianCalendars[j] = gr;
                        gr.add(Calendar.DATE, -j);
                    }

                    wczytajOkresCzasuPie(gregorianCalendars);
                    mPieChart.startAnimation();



                }else if(item.getItemId()==R.id.ostatnieDziewiedzisiatDni){

                    nazwaOkresu.setText("Ostatnie 90 dni");

                    mStackedBarChart.clearChart();
                    GregorianCalendar gr3 = new GregorianCalendar();
                    wyswietlWykresStaked(90,gr3,true);
                    mStackedBarChart.startAnimation();


                    mPieChart.clearChart();

                    GregorianCalendar[] gregorianCalendars = new GregorianCalendar[90];

                    for(int j = 0; j<90; j++){
                        GregorianCalendar gr = new GregorianCalendar();
                        gregorianCalendars[j] = gr;
                        gr.add(Calendar.DATE, -j);
                    }
                    wczytajOkresCzasuPie(gregorianCalendars);
                    mPieChart.startAnimation();

                }else if(item.getItemId()==R.id.zdefiniowanaData){

                    long diff = 0;
                    String start = dataStart.getText().toString();
                    String stop = dataStop.getText().toString();

                    if(!start.equals("")&&!stop.equals(""))
                        diff = ChronoUnit.DAYS.between(gregorianCalendarStart.toInstant(),gregorianCalendarStop.toInstant())+1;

                    if(TextUtils.isEmpty(start)||TextUtils.isEmpty(start)) {
                        dataStart.setError("podaj datę");
                        dataStop.setError("podaj datę");
                    } else if(diff<=0){
                        dataStop.setError("Data musi być datą późniejszą niż data obok");
                        dataStart.setError("Data musi być datą wcześniejszą niż data obok");
                    } else {

                        nazwaOkresu.setText(start + " - " + stop);


                        mStackedBarChart.clearChart();
                        wyswietlWykresStaked((int) diff, gregorianCalendarStart,false);
                        mStackedBarChart.startAnimation();


                        mPieChart.clearChart();
                        GregorianCalendar[] gregorianCalendars = new GregorianCalendar[(int) diff];

                        for (int j = 0; j < diff; j++) {
                            GregorianCalendar gr = new GregorianCalendar();
                            gregorianCalendars[j] = gr;
                            gr.add(Calendar.DATE, j);
                        }

                        wczytajOkresCzasuPie(gregorianCalendars);
                        mPieChart.startAnimation();

                    }
                }
                return false;
            }
        });


        mStackedBarChart.startAnimation();
        mPieChart.startAnimation();
    }


    void wczytajOkresCzasuStacked(GregorianCalendar gregorianCalendar1, StackedBarModel wyk1) {


        rootRef.child("Dziennik_posilkow").child(idZalogowanego).child(simpleDateFormat.format(gregorianCalendar1.getTime())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int sumaKalSniadanie = 0;
                int sumaKalObiad = 0;
                int sumaKalKolacja = 0;
                int sumaKalPrzekaski = 0;


                if (snapshot.hasChild("Sniadanie")) {

                    Iterator<DataSnapshot> sniadanie = snapshot.child("Sniadanie").getChildren().iterator();


                    while (sniadanie.hasNext()) {
                        DataSnapshot sniadaniePosile = sniadanie.next();
                        Posilek posilek1 = sniadaniePosile.getValue(Posilek.class);
                        int kal = posilek1.getKalorycznosc();
                        sumaKalSniadanie = sumaKalSniadanie + kal;
                        wyk1.addBar(new BarModel(sumaKalSniadanie, Color.BLUE));

                    }
                }

                if (snapshot.hasChild("Obiad")) {

                    Iterator<DataSnapshot> obiad = snapshot.child("Obiad").getChildren().iterator();

                    while (obiad.hasNext()) {
                        DataSnapshot obiadPosile = obiad.next();
                        Posilek posilek2 = obiadPosile.getValue(Posilek.class);
                        int kal = posilek2.getKalorycznosc();
                        sumaKalObiad = sumaKalObiad + kal;
                        wyk1.addBar(new BarModel(sumaKalObiad, Color.RED));
                    }

                }
                if (snapshot.hasChild("Kolacja")) {

                    Iterator<DataSnapshot> kolacja = snapshot.child("Kolacja").getChildren().iterator();

                    while (kolacja.hasNext()) {
                        DataSnapshot kolacjaPosile = kolacja.next();
                        Posilek posilek3 = kolacjaPosile.getValue(Posilek.class);
                        int kal = posilek3.getKalorycznosc();
                        sumaKalKolacja = sumaKalKolacja + kal;
                        wyk1.addBar(new BarModel(sumaKalKolacja, Color.YELLOW));
                    }

                }
                if (snapshot.hasChild("Przekaski")) {

                    Iterator<DataSnapshot> przekaski = snapshot.child("Przekaski").getChildren().iterator();

                    while (przekaski.hasNext()) {
                        DataSnapshot przekaskiPosile = przekaski.next();
                        Posilek posilek4 = przekaskiPosile.getValue(Posilek.class);
                        int kal = posilek4.getKalorycznosc();
                        sumaKalPrzekaski = sumaKalPrzekaski + kal;
                        wyk1.addBar(new BarModel(sumaKalPrzekaski, Color.BLACK));
                    }
                }

                mStackedBarChart.addBar(wyk1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void wyswietlWykresStaked(int iloscDni, GregorianCalendar gregorianCalendar, boolean tyl){


        if(tyl) {
            for (int i = 1; i <= iloscDni; i++) {

                StackedBarModel wyk1 = new StackedBarModel(simpleDateFormat.format(gregorianCalendar.getTime()));
                wczytajOkresCzasuStacked(gregorianCalendar, wyk1);
                gregorianCalendar.add(Calendar.DATE, -1);
            }
        }else {

            for (int i = 1; i <= iloscDni; i++) {

                StackedBarModel wyk1 = new StackedBarModel(simpleDateFormat.format(gregorianCalendar.getTime()));
                wczytajOkresCzasuStacked(gregorianCalendar, wyk1);
                gregorianCalendar.add(Calendar.DATE, 1);
            }

        }

    }



    void wczytajOkresCzasuPie(GregorianCalendar[] gregorianCalendars) {

        rootRef.child("Dziennik_posilkow").child(idZalogowanego).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                GregorianCalendar gregorianCalendarPie;

                int size = gregorianCalendars.length;

                for (int i = 0; i < size; i++){


                    gregorianCalendarPie = gregorianCalendars[i];

                    if(snapshot.hasChild(simpleDateFormat.format(gregorianCalendarPie.getTime()))) {



                        if (snapshot.child(simpleDateFormat.format(gregorianCalendarPie.getTime())).hasChild("Sniadanie")) {

                            Iterator<DataSnapshot> sniadanie = snapshot.child((simpleDateFormat.format(gregorianCalendarPie.getTime()))).child("Sniadanie").getChildren().iterator();

                            while (sniadanie.hasNext()) {
                                DataSnapshot sniadaniePosile = sniadanie.next();
                                Posilek posilek1 = sniadaniePosile.getValue(Posilek.class);
                                int kal = posilek1.getKalorycznosc();
                                sumaKalSniadanie = sumaKalSniadanie + kal;

                            }


                        }
                        if (snapshot.child(simpleDateFormat.format(gregorianCalendarPie.getTime())).hasChild("Obiad")) {

                            Iterator<DataSnapshot> obiad = snapshot.child((simpleDateFormat.format(gregorianCalendarPie.getTime()))).child("Obiad").getChildren().iterator();

                            while (obiad.hasNext()) {
                                DataSnapshot obiadPosilek = obiad.next();
                                Posilek posilek2= obiadPosilek.getValue(Posilek.class);
                                int kal = posilek2.getKalorycznosc();
                                sumaKalObiad = sumaKalObiad + kal;

                            }

                        }
                        if (snapshot.child(simpleDateFormat.format(gregorianCalendarPie.getTime())).hasChild("Kolacja")) {

                            Iterator<DataSnapshot> kolacja = snapshot.child((simpleDateFormat.format(gregorianCalendarPie.getTime()))).child("Kolacja").getChildren().iterator();

                            while (kolacja.hasNext()) {
                                DataSnapshot kolacjaPosilek = kolacja.next();
                                Posilek posilek3 = kolacjaPosilek.getValue(Posilek.class);
                                int kal = posilek3.getKalorycznosc();
                                sumaKalKolacja = sumaKalKolacja + kal;

                            }

                        }
                        if (snapshot.child(simpleDateFormat.format(gregorianCalendarPie.getTime())).hasChild("Przekaski")) {

                            Iterator<DataSnapshot> przekaski = snapshot.child((simpleDateFormat.format(gregorianCalendarPie.getTime()))).child("Przekaski").getChildren().iterator();

                            while (przekaski.hasNext()) {
                                DataSnapshot przekaskiPosilek = przekaski.next();
                                Posilek posilek4 = przekaskiPosilek.getValue(Posilek.class);
                                int kal = posilek4.getKalorycznosc();
                                sumaKalPrzekaski = sumaKalPrzekaski + kal;

                            }
                        }
                    }

                }



                mPieChart.addPieSlice(new PieModel("przekąski", sumaKalPrzekaski, Color.BLACK));
                mPieChart.addPieSlice(new PieModel("śniadanie", sumaKalSniadanie, Color.BLUE));
                mPieChart.addPieSlice(new PieModel("obiad", sumaKalObiad, Color.RED));
                mPieChart.addPieSlice(new PieModel("kolacja", sumaKalKolacja, Color.YELLOW));

                 sumaKalSniadanie = 0;
                 sumaKalObiad = 0;
                 sumaKalKolacja = 0;
                 sumaKalPrzekaski = 0;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }








}















