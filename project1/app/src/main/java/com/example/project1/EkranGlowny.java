package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.project1.DaneUzytkownika.getKal;

public class EkranGlowny extends AppCompatActivity {

    DatabaseReference databaseReferenceMain;
    DatabaseReference databaseReferenceRoot;
    FirebaseAuth auth;
    TextView labelKalorii;
    String idZalogowanego;
    ProgressBar progresKalorycznosci;
    TextView monitorKalorii;
    SimpleDateFormat simpleDateFormat;
    GregorianCalendar dt1 = new GregorianCalendar();
    ArrayList<Posilek> lista;

    BottomNavigationView bottomNavigationView;
    Button dodajWage;
    Button edycjaWag;
    EditText wagaText;
    int suma;

    LineChart wagaLineChart;
    String[] datyWag;
    LineDataSet lineDataSet;
    ArrayList<ILineDataSet> dataSets;
    LineData data;

    TextView wagaPoczatkowa;
    TextView wagaBiezaca;
    ImageButton inneWykresy;
    ImageButton zdjecieWagi;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ekran_glowny);


        bottomNavigationView = findViewById(R.id.bottom_navigation2);
        bottomNavigationView.setSelectedItemId(R.id.dashboard);

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


        inneWykresy = (ImageButton) findViewById(R.id.graphs);

        inneWykresy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInneWykresy();
            }
        });

        dodajWage = (Button) findViewById(R.id.dodajwage);
        edycjaWag = (Button) findViewById(R.id.edycjaWag);
        zdjecieWagi = (ImageButton) findViewById(R.id.zdjecieWagi);
        wagaText = (EditText) findViewById(R.id.waga1);
        wagaBiezaca = (TextView) findViewById(R.id.wagaBiezaca);
        wagaPoczatkowa = (TextView) findViewById(R.id.wagaPoczatkowa);

        ///wykres
        wagaLineChart =(LineChart) findViewById(R.id.barChartAktynwosci);
        wagaLineChart.setTouchEnabled(true);
        wagaLineChart.setPinchZoom(true);
        lista = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();
        databaseReferenceMain = FirebaseDatabase.getInstance().getReference().child("Wszystkie posilki uzytkownika do monitora posilkow").child(idZalogowanego).child(simpleDateFormat.format(dt1.getTime()));
        databaseReferenceRoot = FirebaseDatabase.getInstance().getReference();
        labelKalorii = (TextView) findViewById(R.id.kalorieInfo);
        monitorKalorii = (TextView) findViewById(R.id.monitorKalorii);
        progresKalorycznosci = (ProgressBar) findViewById(R.id.progrsKalorii);
        progresKalorycznosci.setScaleY(4f);


        databaseReferenceMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                labelKalorii.setTextColor(Color.GRAY);

                Iterator<DataSnapshot> posilki = snapshot.getChildren().iterator();

                while (posilki.hasNext()) {
                    DataSnapshot item = posilki.next();
                    Posilek posilek = item.getValue(Posilek.class);
                    lista.add(posilek);
                }
                for(int i = 0; i<=lista.size()-1; i++){

                    int kal = lista.get(i).getKalorycznosc();
                    suma = suma + kal;
                }
                String sumaText =  String.valueOf(suma);
                monitorKalorii.setText(sumaText+"/"+String.format("%.2f",getKal()));
                int wartosc = (int) ((suma*100)/getKal());
                progresKalorycznosci.setProgress(wartosc);

                if(wartosc<100){
                    labelKalorii.setText("Pozostałe kalorie do spożycia: ");
                }else if(wartosc>=100){
                    labelKalorii.setText("Przekroczyłeś limit kaloryczny na dzisiejszy dzień!");
                    labelKalorii.setTextColor(Color.RED);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edycjaWag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEdycjaWag();
                finish();
            }
        });


        zdjecieWagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zrobZdjecieWagi();
            }
        });


        dodajWage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String wagaRef = "Zmiany w wadze/" + idZalogowanego;
                String waga = wagaText.getText().toString();


                if(!TextUtils.isEmpty(waga)) {

                    ///pobrać wszystkie wagi i sprawdzić
                    List listToCheck = Arrays.asList(datyWag);
                    String todayDate = simpleDateFormat.format(dt1.getTime());

                    if(!listToCheck.contains(todayDate)){

                        DatabaseReference push = databaseReferenceMain.child("Zmiany w wadze")
                                .child(idZalogowanego).push();

                        String pushId = push.getKey();

                        Map wagaWlasciwosci = new HashMap();
                        String data_ = simpleDateFormat.format(new Date().getTime());

                        if(!waga.equals("")){
                            wagaWlasciwosci.put("waga", Float.parseFloat(waga));
                            wagaWlasciwosci.put("data", data_);
                            wagaWlasciwosci.put("index", pushId );
                            wagaWlasciwosci.put("zdjecie sylwetki", "");

                            Map wagaMap = new HashMap();
                            wagaMap.put(wagaRef+"/"+pushId, wagaWlasciwosci);

                            databaseReferenceRoot.updateChildren(wagaMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                    Intent intent = getIntent();
                                    overridePendingTransition(0, 0);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(intent);
                                }
                            });
                        }
                    }else {
                        wagaText.setError("Waga na dzień "+todayDate+" jest już dodana!");
                    }
                }else {
                    wagaText.setError("Podaj wagę do dodania");
                }
            }
        });

        Query ostatniaWaga = databaseReferenceRoot.child("Zmiany w wadze").child(idZalogowanego).limitToLast(1);

        ostatniaWaga.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String waga = snapshot.child("waga").getValue().toString();
                Float wagaFloat = Float.parseFloat(waga);
                wagaBiezaca.setText("WAGA BIEŻĄCA:"+"\n\t\t\t\t\t\t"+String.format("%.2f",wagaFloat)+ " kg");
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


        Query wagaObecna = databaseReferenceRoot.child("Zmiany w wadze").child(idZalogowanego).limitToFirst(1);

        wagaObecna.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String waga = snapshot.child("waga").getValue().toString();
                Float wagaFloat = Float.parseFloat(waga);
                wagaPoczatkowa.setText("WAGA POCZĄTKOWA:"+"\n\t\t\t\t\t\t\t"+ String.format("%.2f",wagaFloat)+" kg");
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


        databaseReferenceRoot.child("Zmiany w wadze").child(idZalogowanego).addValueEventListener(new ValueEventListener() {
                @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ArrayList<Entry> zapisaneWagi = new ArrayList<>();

                    datyWag = new String[(int) snapshot.getChildrenCount()];
                int i = 0;
                for (DataSnapshot myDataSnapshot : snapshot.getChildren()){
                    Waga wagaValue = myDataSnapshot.getValue(Waga.class);
                    zapisaneWagi.add(new Entry(i,wagaValue.getWaga()));
                    datyWag[i] = wagaValue.getData();
                    i++;
                }
                lineDataSet = new LineDataSet(zapisaneWagi, "ZMIANA WAGI");
                dataSets = new ArrayList<>();
                dataSets.add(lineDataSet);
                data = new LineData(dataSets);
                wagaLineChart.setData(data);
                wagaLineChart.invalidate();
                configAxis();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void configAxis(){
        int lineColor = Color.rgb(52,235,55);
        int dotsColor = Color.rgb(235, 52, 79);
        lineDataSet.setColors(new int[]{lineColor});
        lineDataSet.setValueTextSize(13f);
        lineDataSet.setCircleColor(dotsColor);
        lineDataSet.setLineWidth(4f);
        XAxis xAxis = wagaLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setTextSize(11f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(datyWag));
        xAxis.setLabelRotationAngle(-45);
        YAxis yAxis = wagaLineChart.getAxisLeft();
        yAxis.setTextSize(15f);
        yAxis.setGranularity(1f);
        Legend legend = wagaLineChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(15f);
        wagaLineChart.setAutoScaleMinMaxEnabled(true);
        wagaLineChart.setExtraTopOffset(35f);
    }


    public void openEdycjaWag(){
        Intent intent = new Intent(this,EdycjaWag.class);
        startActivity(intent);
    }

    public void openInneWykresy(){
        Intent intent = new Intent(this, InneWykresy.class);
        startActivity(intent);
    }


    private void zrobZdjecieWagi() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
        }
    }

    private void dispTextFromImage(FirebaseVisionText firebaseVisionText) {

        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if(blockList.size()==0){
            Toast.makeText(this, "Nie znaleziono tekstu", Toast.LENGTH_SHORT);
        }else{

            for(FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
                String text = block.getText();
                int i = text.length();
                if(i>0) {
                    String poModyfikacji = text.substring(0, i - 2) + "." + text.substring(i - 2, i);
                    String poModyfikacji2 = poModyfikacji.replaceAll("\\s", "");
                    wagaText.setText(poModyfikacji2);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
            firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    dispTextFromImage(firebaseVisionText);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

}






