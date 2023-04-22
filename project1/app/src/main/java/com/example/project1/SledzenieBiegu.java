package com.example.project1;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.transition.MaterialSharedAxis;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import static com.example.project1.DaneUzytkownika.getWagaRet;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class SledzenieBiegu extends FragmentActivity
        implements
        OnMapReadyCallback{

    GoogleMap map;
    ImageButton startTracking;
    ImageButton stopTracking;
    TextView przebytaDroga;
    TextView spaloneKalorieBiegu;
    TextView czasTrwania;
    TextView srednieTempo;
    TextView srednieTempoBierzacegoKm;
    TextView dystansBierzacegoKm;
    boolean isPermissionGranted = false;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    ArrayList<LatLng> locations = new ArrayList<>();
    PolylineOptions polylineOptions = new PolylineOptions();
    Polyline polyline;
    LocationListener locationListener;
    Timer timer;
    TimerTask timerTask;
    double czas = 0.0;
    double czasKilometra = 0.0;
    boolean isPaused=false;
    String idZalogowanego;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    Bitmap bitmapMapy;

    ImageView test;


    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    Bieg nowyBieg = new Bieg(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0);
    KilometrBiegu kilometr = new KilometrBiegu(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sledzenie_biegu);

        nowyBieg.getKilometrBiegus().add(kilometr);
        startTracking = (ImageButton) findViewById(R.id.startTracking);
        stopTracking = (ImageButton) findViewById(R.id.stopTracking);
        przebytaDroga =(TextView)findViewById(R.id.przebytaOdleglosc);
        czasTrwania = (TextView) findViewById(R.id.czasTrwania);
        srednieTempo = (TextView) findViewById(R.id.srednieTempo);
        spaloneKalorieBiegu = (TextView) findViewById(R.id.spaloneKalorieBiegu);
        srednieTempoBierzacegoKm =(TextView) findViewById(R.id.srednieTempoBierzKm);
        dystansBierzacegoKm = (TextView) findViewById(R.id.dystansBIerzacegoKm);
        stopTracking.setVisibility(View.GONE);

        test = (ImageView) findViewById(R.id.testScreen);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();

        test.setVisibility(View.GONE);



        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            firstTimeAskForPermission();
        }else{
            isPermissionGranted=true;
        }

        if (isPermissionGranted) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
            mapFragment.getMapAsync(this);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            //co 3 sekundy
            locationRequest = new LocationRequest.Builder(500).setIntervalMillis(1000).build();

            startTracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(isPaused==false){

                        isPaused=true;
                        stopTracking.setVisibility(View.VISIBLE);
                        startTracking.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.pause));

                        locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {

                                LatLng locationCurr = new LatLng(location.getLatitude(),location.getLongitude());;
                                locations.add(locationCurr);
                                polylineOptions.add(locationCurr);
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationCurr, 16));

                                if(Math.floor(kilometr.getDystans())==1){
                                    kilometr.obliczSredniaPredkosc();
                                    kilometr.obliczSrednieTempo();
                                    kilometr = new KilometrBiegu(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0);
                                    nowyBieg.getKilometrBiegus().add(kilometr);
                                    czasKilometra=0.0;
                                }

                                kilometr.setCzas(czasKilometra);
                                nowyBieg.setCzas(czas);

                                double droga=0.0;
                                double predkoscWdanejChwili;

                                if(locations.size()>=2){

                                    for(int i=1; i<locations.size();i++) {
                                        droga += odleglosc(locations.get(i), locations.get(i - 1));
                                    }

                                    predkoscWdanejChwili = odleglosc(locations.get(locations.size()-1), locations.get(locations.size()-2));

                                    if(predkoscWdanejChwili!=0)
                                        predkoscWdanejChwili = (1/(predkoscWdanejChwili*3600)*60);
                                    nowyBieg.spaloneKalorie += oszacujWartoscMET(predkoscWdanejChwili)*getWagaRet()*(0.00027777777);
                                }
                                int iloscKilometrow = nowyBieg.getKilometrBiegus().size()-1;
                                nowyBieg.setDystans(droga);
                                kilometr.setDystans(droga-iloscKilometrow);

                                if(kilometr.getDystans()>=1.0){
                                    dystansBierzacegoKm.setText("1.00"+"\n Km");
                                }else{
                                    dystansBierzacegoKm.setText(String.format("%.2f",kilometr.getDystans())+"\n Km");
                                }
                                przebytaDroga.setText(String.format("%.2f",nowyBieg.getDystans())+"\n Km");
                                spaloneKalorieBiegu.setText(String.valueOf((int)nowyBieg.spaloneKalorie)+"\n kcal");

                                if(droga==0){
                                    srednieTempo.setText("---------------");
                                }else{
                                    nowyBieg.obliczSredniaPredkosc();
                                    nowyBieg.obliczSrednieTempo();
                                    kilometr.obliczSredniaPredkosc();
                                    kilometr.obliczSrednieTempo();
                                    srednieTempo.setText(String.format("%.2f",nowyBieg.getTempo() )+"\n min/km");
                                    srednieTempoBierzacegoKm.setText(String.format("%.2f",kilometr.getTempo() )+"\n min/km");
                                }
                                polyline = map.addPolyline(polylineOptions);
                                polyline.setColor(Color.rgb(255,127,80));
                                polyline.setWidth(20f);
                            }
                        };
                        startLocationUpdates();
                        startTimer();
                    }else {
                        stopSledzenieBiegu();
                    }
                }
            });


            stopTracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    czyZapisacDialog("new");

                    //zapiszBazieDanych();
                    //stopSledzenieBiegu();
                    //stopTracking.setVisibility(View.GONE);
                    //zrobScreenMapy();
               /*     map.clear();
                    locations.clear();
                    nowyBieg = new Bieg(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0);
                    kilometr = new KilometrBiegu(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0);
                    nowyBieg.getKilometrBiegus().add(kilometr);
                    polylineOptions = new PolylineOptions();
                    dystansBierzacegoKm.setText("0.00 \n Dystans bierzącego kilometra");
                    srednieTempoBierzacegoKm.setText("--------------- \n Średnie tempo bierzącego kilometra");
                    przebytaDroga.setText("0.00 \n Dystans");
                    spaloneKalorieBiegu.setText("0\n Kalorie");
                    srednieTempo.setText("--------------- \n Tempo");
                    czasTrwania.setText("00:00:00 \n Czas");
                    czas=0.0;
                    czasKilometra=0.0;
                    test.setVisibility(View.VISIBLE);*/
                }
            });
        }
    }

    public LatLngBounds obliczGranice(){

        double s = locations.get(0).latitude;
        double n = locations.get(0).latitude;
        double w = locations.get(0).longitude;
        double e = locations.get(0).longitude;


        for(int i=1; i<=locations.size()-1;i++){

            LatLng latLng = locations.get(i);
            s= Math.min(s, latLng.latitude);
            n = Math.max(n, latLng.latitude);
            w = Math.min(w, latLng.longitude);
            e= Math.max(e, latLng.longitude);
        }
        LatLngBounds granica = new LatLngBounds(
                new LatLng(s,w),
                new LatLng(n,e)
        );

        return granica;
    }


    public void zrobScreenMapy(){

        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.get(locations.size()-1), 12));
        //map.moveCamera(CameraUpdateFactory.newLatLngBounds(obliczGranice(), 0));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(obliczGranice().getCenter(),10));

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                map.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(@Nullable Bitmap bitmap) {
                        bitmapMapy=null;
                        bitmapMapy=bitmap;
                        test.setImageBitmap(bitmapMapy);
                    }
                });
            }
        });

    }

    public void zapiszBazieDanych(){

        String posilekRef = "Dziennik_posilkow/" + idZalogowanego + "/" + simpleDateFormat.format(gregorianCalendar.getTime()) + "/" + "Cwiczenia";
        String posilekRef1 = "Wszystkie posilki uzytkownika do monitora posilkow" +"/"+ idZalogowanego + "/" + simpleDateFormat.format(gregorianCalendar.getTime());

        DatabaseReference push = databaseReference.child("Dziennik_posilkow")
                .child(idZalogowanego).push();

        DatabaseReference push1 = databaseReference.child("Wszystkie posilki uzytkownika do monitora posilkow")
                .child(idZalogowanego).push();

        String pushId = push.getKey();
        String pushId1 = push1.getKey();

        Map posilekMap = new HashMap();

        posilekMap.put("nazwaPosilku", "Bieganie "+String.format("%.2f",nowyBieg.getDystans())+" km");
        posilekMap.put("index", pushId);

        posilekMap.put("kalorycznosc", (int) -nowyBieg.getSpaloneKalorie());
        Map czescPosilku = new HashMap();
        czescPosilku.put(posilekRef + "/" + pushId, posilekMap);

        Map wszystkieposilki = new HashMap();
        wszystkieposilki.put(posilekRef1+"/"+pushId1, posilekMap);

        databaseReference.updateChildren(czescPosilku, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

            }
        });
    }


    public void stopSledzenieBiegu(){
        startTracking.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.play_button));
        stopTimer();
        stopLocationUpdate();
        isPaused=false;
    }

    public void czyZapisacDialog(String backOrNew){

        final Dialog dialog = new Dialog(SledzenieBiegu.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.czy_zapisac_bieg);

        final Button zapiszBieg = dialog.findViewById(R.id.zapiszBieg);
        final Button wroc = dialog.findViewById(R.id.wyjdz);

        if(backOrNew.equals("new")){
            wroc.setText("nowy bieg");
            dialog.dismiss();
        }

        zapiszBieg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zrobScreenMapy();
                test.setVisibility(View.VISIBLE);
                zapiszBazieDanych();
                //otwarcie podsumowania

            }
        });


        wroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(backOrNew.equals("back")){
                    //bez zapisu
                    normalOnbackPressed();
                }

                if(backOrNew.equals("new")){
                    zapiszBazieDanych();
                    stopSledzenieBiegu();
                    stopTracking.setVisibility(View.GONE);
                    map.clear();
                    locations.clear();
                    nowyBieg = new Bieg(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0);
                    kilometr = new KilometrBiegu(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0);
                    nowyBieg.getKilometrBiegus().add(kilometr);
                    polylineOptions = new PolylineOptions();
                    dystansBierzacegoKm.setText("0.00 \n Dystans bierzącego kilometra");
                    srednieTempoBierzacegoKm.setText("--------------- \n Średnie tempo bierzącego kilometra");
                    przebytaDroga.setText("0.00 \n Dystans");
                    spaloneKalorieBiegu.setText("0\n Kalorie");
                    srednieTempo.setText("--------------- \n Tempo");
                    czasTrwania.setText("00:00:00 \n Czas");
                    czas=0.0;
                    czasKilometra=0.0;
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        czyZapisacDialog("back");
    }


    private void normalOnbackPressed(){
        super.onBackPressed();
    }

    public float oszacujWartoscMET(double sredniaPredkosc){

        float MET = 0;
        Map<Double, Float> mapPredkosciMET = new HashMap<>();

        //k-tempo min/h, v-MET
        mapPredkosciMET.put(0.0,2f);
        mapPredkosciMET.put(21.93271044,2.3f);
        mapPredkosciMET.put(14.9142431,2.9f);
        mapPredkosciMET.put(12.42853592,	3.3f);
        mapPredkosciMET.put(10.96635522,3.6f);
        mapPredkosciMET.put(9.321401939,6f);
        mapPredkosciMET.put(7.457121551,8.3f);
        mapPredkosciMET.put(7.170309184,9f);
        mapPredkosciMET.put(6.214267959,9.8f);
        mapPredkosciMET.put(5.565016083,10.5f);
        mapPredkosciMET.put(5.326515394,11f);
        mapPredkosciMET.put(4.971414367,11.5f);
        mapPredkosciMET.put(4.335535786,12.3f);
        mapPredkosciMET.put(4.142845306,12.8f);
        mapPredkosciMET.put(3.728560776,14.5f);
        mapPredkosciMET.put(3.389600705,16f);
        mapPredkosciMET.put(3.10713398,19f);
        mapPredkosciMET.put(2.868123673,19.8f);
        mapPredkosciMET.put(2.663257697,23f);

        Set<Double> keys= mapPredkosciMET.keySet();
        double[] array = new double[19];
        int i=0;

        for(Double key:keys){
            array[i]=key;
            i++;
        }

        Arrays.sort(array);
        int left =0;
        int right= 18;

        while (left<right){

            if(Math.abs(array[left]-sredniaPredkosc)
            <=Math.abs(array[right]-sredniaPredkosc)){
                right--;
            }else{
                left++;
            }
        }
        MET = mapPredkosciMET.get(array[left]);
        return MET;
    }

    private void startTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        czas++;
                        czasKilometra++;
                        czasTrwania.setText(getTimerText());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask,0,1000);
    }

    private void stopTimer(){
        timer.cancel();
    }

    private String getTimerText(){

        int rounded = (int) Math.round(czas);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);
        return fortmatTime(seconds,minutes, hours);
    }

    private String fortmatTime(int seconds, int minutes, int hours){

        return String.format("%02d",hours) + ":" + String.format("%02d",minutes) +  ":" + String.format("%02d",seconds);
    }


    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationListener, Looper.getMainLooper());
    }

    private void stopLocationUpdate(){
        fusedLocationProviderClient.removeLocationUpdates(locationListener);
    }

    private void firstTimeAskForPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(SledzenieBiegu.this, "Permission granted", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    public static double odleglosc(LatLng punkt1, LatLng punkt2) {
        double lon1=punkt1.longitude;
        double lon2=punkt2.longitude;
        double lat1=punkt1.latitude;
        double lat2=punkt2.latitude;
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));
        // Radius of earth in kilometers.
        double r = 6371;
        double odleglosc = c*r;
        return odleglosc;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        zoomToUserLocation();
    }

    private void zoomToUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        });
    }
}