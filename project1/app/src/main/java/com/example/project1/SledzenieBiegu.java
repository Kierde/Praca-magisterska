package com.example.project1;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    Button startTracking;
    Button stopTracking;
    TextView przebytaDroga;
    TextView spaloneKalorieBiegu;
    TextView czasTrwania;
    TextView srednieTempo;
    boolean isPermissionGranted = false;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    ArrayList<LatLng> locations = new ArrayList<>();
    PolylineOptions polylineOptions = new PolylineOptions();
    LocationListener locationListener;
    Timer timer;
    TimerTask timerTask;
    double czas = 0.0;
    double czasKilometra = 0.0;
    boolean biegWystarowany = false;
    double spaloneKalorie =0.0;

    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    Bieg nowyBieg = new Bieg(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0);
    KilometrBiegu kilometr = new KilometrBiegu(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sledzenie_biegu);

        startTracking = (Button) findViewById(R.id.startTracking);
        stopTracking = (Button) findViewById(R.id.stopTracking);
        przebytaDroga =(TextView)findViewById(R.id.przebytaOdleglosc);
        czasTrwania = (TextView) findViewById(R.id.czasTrwania);
        srednieTempo = (TextView) findViewById(R.id.srednieTempo);
        spaloneKalorieBiegu = (TextView) findViewById(R.id.spaloneKalorieBiegu);
        stopTracking.setEnabled(false);


       // Log.d("MET"," "+oszacujWartoscMET(7.3));

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


                    if(!biegWystarowany){
                        nowyBieg.getKilometrBiegus().add(kilometr);
                        biegWystarowany=true;
                    }
                    startTracking.setEnabled(false);

                   locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            LatLng locationCurr = new LatLng(location.getLatitude(),location.getLongitude());;
                            locations.add(locationCurr);
                            polylineOptions.add(locationCurr);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationCurr, 16));


                            if(Math.floor(kilometr.getDystans())==1){
                                Log.d("Kilometr", "OSIĄGNIĘTY"+kilometr.getDystans());
                                kilometr.obliczSredniaPredkosc();
                                kilometr.obliczSrednieTempo();
                                KilometrBiegu kilometr = new KilometrBiegu(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0);
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

                                spaloneKalorie += oszacujWartoscMET(predkoscWdanejChwili)*getWagaRet()*(0.00027777777);
                                Log.d("spalone", " "+spaloneKalorie);
                            }
                            int iloscKilometrow = nowyBieg.getKilometrBiegus().size()-1;
                            nowyBieg.setDystans(droga);
                            kilometr.setDystans(droga-iloscKilometrow);
                            przebytaDroga.setText(String.format("%.2f",nowyBieg.getDystans())+"\n Km");
                            spaloneKalorieBiegu.setText(String.valueOf((int)spaloneKalorie)+"\n kcal");


                            if(droga==0){
                                srednieTempo.setText("---------------");
                            }else{
                                nowyBieg.obliczSredniaPredkosc();
                                nowyBieg.obliczSrednieTempo();
                                srednieTempo.setText(String.format("%.2f",nowyBieg.getTempo() )+"\n min/km");
                            }
                            Polyline polyline = map.addPolyline(polylineOptions);
                            polyline.setColor(Color.rgb(255,127,80));
                            polyline.setWidth(20f);
                        }
                    };
                    startLocationUpdates();
                    startTimer();
                    stopTracking.setEnabled(true);
                }
            });


            stopTracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startTracking.setEnabled(true);
                    stopTimer();
                    stopLocationUpdate();
                }
            });
        }
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
            Log.d("Str", " "+array[i]);
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

        startTracking.setEnabled(false);
        stopTracking.setEnabled(true);
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

        startTracking.setEnabled(true);
        stopTracking.setEnabled(false);
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