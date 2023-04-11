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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class SledzenieBiegu extends FragmentActivity
        implements
        OnMapReadyCallback{

    GoogleMap map;
    Button startTracking;
    Button stopTracking;
    TextView przebytaDroga;
    TextView czasTrwania;
    TextView srednieTempo;

    boolean isPermissionGranted = false;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    ArrayList<LatLng> locations = new ArrayList<>();
    PolylineOptions polylineOptions = new PolylineOptions();
    LocationListener locationListener;

    double droga =0.0;
    Timer timer;
    TimerTask timerTask;
    double czas =0.0;
    double sredniaPredkosc=0.0;
    double tempo=0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sledzenie_biegu);

        startTracking = (Button) findViewById(R.id.startTracking);
        stopTracking = (Button) findViewById(R.id.stopTracking);
        przebytaDroga =(TextView)findViewById(R.id.przebytaOdleglosc);
        czasTrwania = (TextView) findViewById(R.id.czasTrwania);
        srednieTempo = (TextView) findViewById(R.id.srednieTempo);
        checkMyPermission();
        stopTracking.setEnabled(false);

        if (isPermissionGranted) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
            mapFragment.getMapAsync(this);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            //co 30 sekund albo co 5 metrów
            locationRequest = new LocationRequest.Builder(500).setIntervalMillis(3000).setMinUpdateDistanceMeters(5f).build();

            startTracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startTracking.setEnabled(false);
                   locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            Log.d("Lat,Long", ""+location.getLatitude()+" "+location.getLongitude());
                            LatLng locationCurr = new LatLng(location.getLatitude(),location.getLongitude());;
                            locations.add(locationCurr);
                            polylineOptions.add(locationCurr);
                            droga=0.0;
                            sredniaPredkosc=0.0;
                            tempo=0.0;


                            if(locations.size()>=2){

                                for(int i=1; i<locations.size();i++) {
                                    droga += odleglosc(locations.get(i), locations.get(i - 1));
                                }
                                Log.d("droga i czas"," "+droga+" "+czas);
                            }
                            przebytaDroga.setText(String.format("%.3f",droga)+" Km");

                             sredniaPredkosc= droga/czas;
                             tempo=(1/(3600*sredniaPredkosc))*60;

                            srednieTempo.setText(String.format("%.2f",tempo )+" min/km");
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
                        czasTrwania.setText(getTimerText());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask,0,1000);
    }

    private void stopTimer(){

        czas =0.0;
        startTracking.setEnabled(true);
        stopTracking.setEnabled(false);
        timer.cancel();
        czasTrwania.setText(fortmatTime(0,0,0));
    }

    private String getTimerText(){

        int rounded = (int) Math.round(czas);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);
        return fortmatTime(seconds,minutes, hours);
    }

    private String fortmatTime(int seconds, int minutes, int hours){

        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
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

    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(SledzenieBiegu.this, "Permission granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
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
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
    }
}