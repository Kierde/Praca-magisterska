package com.example.project1;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class KolejneWykresy extends AppCompatActivity {

    BarChart barChartAktynosc;
    BarChart barChartSpoztychKalorii;
    BarChart barChartMakroSkladniki;
    String[] daty;
    DatabaseReference rootRef;
    String idZalogowanego;
    FirebaseAuth auth;
    SimpleDateFormat simpleDateFormat;
    Button ost7dni;
    Button ost30dni;
    Button ost90dni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kolejne_wykresy);

        barChartAktynosc = (BarChart) findViewById(R.id.barChartAktynwosci);
        barChartAktynosc.setTouchEnabled(true);
        barChartAktynosc.setPinchZoom(true);
        barChartSpoztychKalorii = (BarChart) findViewById(R.id.barSpozytychKalorii);
        barChartSpoztychKalorii.setTouchEnabled(true);
        barChartSpoztychKalorii.setPinchZoom(true);
        barChartMakroSkladniki = (BarChart) findViewById(R.id.macroSkladniki);
        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        rootRef = FirebaseDatabase.getInstance().getReference();
        ost7dni= (Button) findViewById(R.id.ost7dni);
        ost30dni= (Button) findViewById(R.id.ost30dni);
        ost90dni= (Button) findViewById(R.id.ost90dni);
        drawExercisesChart(7);
        drawSpozytchKaloriiChart(7);
        drawMacroIngreChart(7);
        ost7dni.setEnabled(false);
        ost7dni.setBackgroundColor(Color.rgb(50,205,50));
        ost7dni.setTextColor(Color.rgb(0,0,0));
        ost30dni.setTextColor(Color.rgb(0,0,0));
        ost90dni.setTextColor(Color.rgb(0,0,0));

        ost7dni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawExercisesChart(7);
                drawSpozytchKaloriiChart(7);
                drawMacroIngreChart(7);
                ost7dni.setEnabled(false);
                ost30dni.setEnabled(true);
                ost90dni.setEnabled(true);
                ost7dni.setBackgroundColor(Color.rgb(50,205,50));
                ost30dni.setBackgroundResource(android.R.drawable.btn_default);
                ost90dni.setBackgroundResource(android.R.drawable.btn_default);
            }
        });

        ost30dni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawExercisesChart(30);
                drawSpozytchKaloriiChart(30);
                drawMacroIngreChart(30);
                ost7dni.setEnabled(true);
                ost30dni.setEnabled(false);
                ost90dni.setEnabled(true);
                ost30dni.setBackgroundColor(Color.rgb(50,205,50));
                ost7dni.setBackgroundResource(android.R.drawable.btn_default);
                ost90dni.setBackgroundResource(android.R.drawable.btn_default);
            }
        });

        ost90dni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawExercisesChart(90);
                drawSpozytchKaloriiChart(90);
                drawMacroIngreChart(90);
                ost7dni.setEnabled(true);
                ost30dni.setEnabled(true);
                ost90dni.setEnabled(false);
                ost90dni.setBackgroundColor(Color.rgb(50,205,50));
                ost30dni.setBackgroundResource(android.R.drawable.btn_default);
                ost7dni.setBackgroundResource(android.R.drawable.btn_default);
            }
        });
    }

    void drawMacroIngreChart(int howMuchDays){

        ArrayList<BarEntry> bialko = new ArrayList<>();
        ArrayList<BarEntry> weglowodany = new ArrayList<>();
        ArrayList<BarEntry> tluszcz = new ArrayList<>();

        rootRef.child("Dziennik_posilkow").child(idZalogowanego)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(int i=0;i<howMuchDays;i++){

                            float sumaBialka = 0;
                            float sumaWeglowodanow = 0;
                            float sumaTluszcz = 0;

                            if(snapshot.hasChild(daty[i])) {

                                DataSnapshot snapshot1 = snapshot.child(daty[i]);

                                if (snapshot1.hasChild("Sniadanie")) {

                                    Iterator<DataSnapshot> posilki = snapshot1.child("Sniadanie").getChildren().iterator();

                                    while (posilki.hasNext()) {

                                        DataSnapshot sniadaniePosilek = posilki.next();
                                        Posilek sniadanieObj = sniadaniePosilek.getValue(Posilek.class);
                                        float bialko_ = sniadanieObj.getBialko();
                                        float weglowodany_ = sniadanieObj.getWeglowodany();
                                        float tluszcz_ = sniadanieObj.getTluszcz();
                                        sumaBialka += bialko_;
                                        sumaWeglowodanow += weglowodany_;
                                        sumaTluszcz += tluszcz_;
                                    }
                                }

                                if (snapshot1.hasChild("Obiad")) {

                                    Iterator<DataSnapshot> posilki = snapshot1.child("Obiad").getChildren().iterator();

                                    while (posilki.hasNext()) {

                                        DataSnapshot obiadPosilek = posilki.next();
                                        Posilek obiadObj = obiadPosilek.getValue(Posilek.class);
                                        float bialko_ = obiadObj.getBialko();
                                        float weglowodany_ = obiadObj.getWeglowodany();
                                        float tluszcz = obiadObj.getTluszcz();
                                        sumaBialka += bialko_;
                                        sumaWeglowodanow += weglowodany_;
                                        sumaTluszcz += tluszcz;
                                    }
                                }

                                if (snapshot1.hasChild("Kolacja")) {

                                    Iterator<DataSnapshot> posilki = snapshot1.child("Kolacja").getChildren().iterator();

                                    while (posilki.hasNext()) {

                                        DataSnapshot kolacjaPosilek = posilki.next();
                                        Posilek kolacjaObj = kolacjaPosilek.getValue(Posilek.class);
                                        float bialko_ = kolacjaObj.getBialko();
                                        float weglowodany_ = kolacjaObj.getWeglowodany();
                                        float tluszcz_ = kolacjaObj.getTluszcz();
                                        sumaBialka += bialko_;
                                        sumaWeglowodanow += weglowodany_;
                                        sumaTluszcz += tluszcz_;
                                    }
                                }

                                if (snapshot1.hasChild("Przekaski")) {

                                    Iterator<DataSnapshot> posilki = snapshot1.child("Przekaski").getChildren().iterator();

                                    while (posilki.hasNext()) {

                                        DataSnapshot przekaskiPosilek = posilki.next();
                                        Posilek przekaskiObj = przekaskiPosilek.getValue(Posilek.class);
                                        float bialko_ = przekaskiObj.getBialko();
                                        float weglowodany_ = przekaskiObj.getWeglowodany();
                                        float tluszcz_ = przekaskiObj.getTluszcz();
                                        sumaBialka += bialko_;
                                        sumaWeglowodanow += weglowodany_;
                                        sumaTluszcz += tluszcz_;
                                    }
                                }
                            }



                            Log.d("sum",daty[i]);
                            Log.d("sumBialko", String.valueOf(sumaBialka));
                            Log.d("sumWegli", String.valueOf(sumaWeglowodanow));
                            Log.d("sumTluszczu", String.valueOf(sumaTluszcz));
                             bialko.add(new BarEntry(i,sumaBialka));
                             weglowodany.add(new BarEntry(i,sumaWeglowodanow));
                             tluszcz.add(new BarEntry(i,sumaTluszcz));
                        }

                        int yellow = Color.rgb(255,255,0);
                        int blue = Color.rgb(0,191,255);
                        int red = Color.rgb(255, 0, 0);

                        BarDataSet bialkoDataSet = new BarDataSet(bialko, "Białko");
                        bialkoDataSet.setColor(red);
                        bialkoDataSet.setValueTextSize(13f);
                        BarDataSet weglowodanyDataSet = new BarDataSet(weglowodany,"Węglowodany");
                        weglowodanyDataSet.setValueTextSize(13f);
                        weglowodanyDataSet.setColor(blue);
                        BarDataSet tluszczDataSet = new BarDataSet(tluszcz,"Tłuszcz");
                        tluszczDataSet.setValueTextSize(13f);
                        tluszczDataSet.setColor(yellow);
                        BarData data = new BarData(bialkoDataSet,weglowodanyDataSet,tluszczDataSet);
                        barChartMakroSkladniki.setData(data);
                        data.setBarWidth(0.06f);
                        float barSpace = 0.09f;
                        float groupSpace = 0.5f;
                        barChartMakroSkladniki.groupBars(0, groupSpace, barSpace);
                        configAxis(barChartMakroSkladniki);
                        barChartMakroSkladniki.getLegend().setTextSize(10f);
                        barChartMakroSkladniki.setExtraTopOffset(40f);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    void drawSpozytchKaloriiChart(int howMuchDays){

        ArrayList<BarEntry> spozyteKalorie = new ArrayList<>();

        rootRef.child("Dziennik_posilkow").child(idZalogowanego)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(int i=0;i<howMuchDays;i++){

                            int sumaKaloriiDnia =0;

                            if(snapshot.hasChild(daty[i])) {

                                DataSnapshot snapshot1 = snapshot.child(daty[i]);

                                if (snapshot1.hasChild("Sniadanie")) {

                                    Iterator<DataSnapshot> posilki = snapshot1.child("Sniadanie").getChildren().iterator();

                                    while (posilki.hasNext()) {

                                        DataSnapshot sniadaniePosilek = posilki.next();
                                        Posilek sniadanieObj = sniadaniePosilek.getValue(Posilek.class);
                                        int kcal = sniadanieObj.getKalorycznosc();
                                        sumaKaloriiDnia = sumaKaloriiDnia + kcal;
                                    }
                                }

                                if (snapshot1.hasChild("Obiad")) {

                                    Iterator<DataSnapshot> posilki = snapshot1.child("Obiad").getChildren().iterator();

                                    while (posilki.hasNext()) {

                                        DataSnapshot obiadPosilek = posilki.next();
                                        Posilek obiadObj = obiadPosilek.getValue(Posilek.class);
                                        int kcal = obiadObj.getKalorycznosc();
                                        sumaKaloriiDnia = sumaKaloriiDnia + kcal;
                                    }
                                }

                                if (snapshot1.hasChild("Kolacja")) {

                                    Iterator<DataSnapshot> posilki = snapshot1.child("Kolacja").getChildren().iterator();

                                    while (posilki.hasNext()) {

                                        DataSnapshot kolacjaPosilek = posilki.next();
                                        Posilek kolacjaObj = kolacjaPosilek.getValue(Posilek.class);
                                        int kcal = kolacjaObj.getKalorycznosc();
                                        sumaKaloriiDnia = sumaKaloriiDnia + kcal;
                                    }
                                }

                                if (snapshot1.hasChild("Przekaski")) {

                                    Iterator<DataSnapshot> posilki = snapshot1.child("Przekaski").getChildren().iterator();

                                    while (posilki.hasNext()) {

                                        DataSnapshot przekaskiPosilek = posilki.next();
                                        Posilek przekaskiObj = przekaskiPosilek.getValue(Posilek.class);
                                        int kcal = przekaskiObj.getKalorycznosc();
                                        sumaKaloriiDnia = sumaKaloriiDnia + kcal;
                                    }
                                }
                            }
                            spozyteKalorie.add(new BarEntry(i,sumaKaloriiDnia));
                        }

                        BarDataSet barDataSet = new BarDataSet(spozyteKalorie,"Spożyte kalorie");
                        barDataSet.setValueTextSize(13f);
                        int orange = Color.rgb(34,139,34);
                        barDataSet.setColor(orange);
                        BarData barData = new BarData(barDataSet);
                        barChartSpoztychKalorii.setData(barData);
                        barChartSpoztychKalorii.invalidate();
                        configAxis(barChartSpoztychKalorii);
                        graphLegend("Spożyte kalorie", orange, barChartSpoztychKalorii);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    void drawExercisesChart(int howMuchDays){

       GregorianCalendar gr = new GregorianCalendar();
        ArrayList<BarEntry> entries = new ArrayList<>();

        rootRef.child("Dziennik_posilkow").child(idZalogowanego)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        daty = new String[howMuchDays];

                        for(int i=0;i<howMuchDays;i++){
                           int sumaKalCwiczen=0;
                            daty[i] = simpleDateFormat.format(gr.getTime());
                            gr.add(Calendar.DATE,-1);
                            if(snapshot.hasChild(daty[i])){
                                DataSnapshot snapDnia = snapshot.child(daty[i]);

                                if(snapDnia.hasChild("Cwiczenia")){

                                    Iterator<DataSnapshot> cwiczenia = snapDnia.child("Cwiczenia").getChildren().iterator();

                                    while (cwiczenia.hasNext()){

                                        DataSnapshot cwiczeniePosilek = cwiczenia.next();
                                        Posilek cwiczenieObj = cwiczeniePosilek.getValue(Posilek.class);
                                        int kcal = cwiczenieObj.getKalorycznosc();
                                        sumaKalCwiczen=sumaKalCwiczen+kcal;
                                    }
                                }
                            }
                            entries.add(new BarEntry(i,-sumaKalCwiczen));
                        }
                        BarDataSet barDataSet = new BarDataSet(entries,"Spalone kalorie");
                        barDataSet.setValueTextSize(13f);
                        int orange = Color.rgb(252, 102, 3);
                        barDataSet.setColor(orange);
                        BarData barData = new BarData(barDataSet);
                        barChartAktynosc.setData(barData);
                        barChartAktynosc.invalidate();
                        configAxis(barChartAktynosc);
                        graphLegend("Spalone kalorie", orange, barChartAktynosc);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    void configAxis(BarChart barChart){
        XAxis xAxis =  barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setTextSize(11f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(daty));
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setTextSize(15f);
        yAxis.setGranularity(1f);
    }

    void graphLegend(String label, int color, BarChart barChart){
        Legend legend = barChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(15f);
        legend.setCustom(new LegendEntry[]{new LegendEntry(label,Legend.LegendForm.SQUARE,10f,2f,null, color)});
        barChart.setAutoScaleMinMaxEnabled(true);
        barChart.setExtraTopOffset(40f);
    }

}