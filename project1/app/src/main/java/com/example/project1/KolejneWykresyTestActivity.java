package com.example.project1;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.project1.Posilek;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.security.auth.callback.Callback;

import okhttp3.internal.cache.DiskLruCache;

public class KolejneWykresyTestActivity extends AppCompatActivity {

    //LineChart lineChart;
    BarChart barChartAktynosc;
    String[] datyWag;
    ArrayList<BarEntry> entries;
    BarDataSet barDataSet;
    BarData barData;

    DatabaseReference rootRef;
    String idZalogowanego;
    FirebaseAuth auth;
    SimpleDateFormat simpleDateFormat;
    int sumaKalCwiczen;
    GregorianCalendar gr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kolejne_wykresy_test);

        barChartAktynosc = (BarChart) findViewById(R.id.barChartAktynwosci);
        auth = FirebaseAuth.getInstance();
        entries = new ArrayList<>();
        idZalogowanego = auth.getUid();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        rootRef = FirebaseDatabase.getInstance().getReference();
        gr= new GregorianCalendar();

        drawExercisesChart(7);

    }


    void drawExercisesChart(int howMuchDays){

        rootRef.child("Dziennik_posilkow").child(idZalogowanego)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        datyWag = new String[howMuchDays];

                        for(int i=0;i<howMuchDays;i++){
                            sumaKalCwiczen=0;
                            datyWag[i] = simpleDateFormat.format(gr.getTime());
                            gr.add(Calendar.DATE,-1);
                            if(snapshot.hasChild(datyWag[i])){
                                DataSnapshot snapDnia = snapshot.child(datyWag[i]);

                                if(snapDnia.hasChild("Cwiczenia")){

                                    Iterator<DataSnapshot> cwiczenia = snapDnia.child("Cwiczenia").getChildren().iterator();

                                    while (cwiczenia.hasNext()){

                                        DataSnapshot cwiczeniePosilek = cwiczenia.next();
                                        Posilek cwiczenieObj = cwiczeniePosilek.getValue(Posilek.class);
                                        int kcal = cwiczenieObj.getKalorycznosc();
                                        sumaKalCwiczen=sumaKalCwiczen+kcal;
                                    }
                                    Log.d("sumaCwiczen", String.valueOf(sumaKalCwiczen));
                                    entries.add(new BarEntry(i,-sumaKalCwiczen));
                                }else {
                                    entries.add(new BarEntry(i, 0));
                                }
                            }else{
                                entries.add(new BarEntry(i,0));
                            }
                            Log.d("daty", datyWag[i]);
                        }

                        barDataSet = new BarDataSet(entries,"Spalone kalorie");
                        barData = new BarData(barDataSet);
                        barChartAktynosc.setData(barData);
                        barChartAktynosc.invalidate();
                        configAxis();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }







    void configAxis(){
        int colorOfBar = Color.rgb(252, 102, 3);
        barDataSet.setValueTextSize(13f);
        barDataSet.setColor(colorOfBar);
        XAxis xAxis =  barChartAktynosc.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setTextSize(11f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(datyWag));
        YAxis yAxis = barChartAktynosc.getAxisLeft();
        yAxis.setTextSize(15f);
        yAxis.setGranularity(1f);
        Legend legend = barChartAktynosc.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(15f);
        legend.setCustom(new LegendEntry[]{new LegendEntry("Spalone kalorie",Legend.LegendForm.SQUARE,10f,2f,null, colorOfBar)});
        barChartAktynosc.setAutoScaleMinMaxEnabled(true);
        barChartAktynosc.setExtraTopOffset(40f);
    }

}