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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.project1.DaneUzytkownika.getWagaRet;


import java.io.ByteArrayOutputStream;
import java.io.File;
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

    boolean biegNieWystarowal = false;
    GoogleMap map;
    FirebaseStorage storage;
    StorageReference storageReference;
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
    boolean zapisywanie = false;
    String idZalogowanego;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    Bitmap bitmapMapy;
    Uri zdjecieMapy;
    String index;
    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    Bieg nowyBieg = new Bieg(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0, "","");
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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();

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
            //co 1 sekundy
            locationRequest = new LocationRequest.Builder(500).setIntervalMillis(1000).build();

            startTracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    biegNieWystarowal=true;
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
                                    predkoscWdanejChwili = predkoscWdanejChwili*3600;
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


    public void zrobScreenMapyZapiszStorage(){

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(obliczGranice().getCenter(),12));

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                map.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(@Nullable Bitmap bitmap) {
                        bitmapMapy=null;
                        bitmapMapy=bitmap;
                        zdjecieMapy = getImageUri(SledzenieBiegu.this, bitmapMapy);

                        DatabaseReference dataref = databaseReference.child("Biegi").child(idZalogowanego).child(index).child("mapaBiegu");

                        StorageReference riversRef = storageReference.child("Zjdecia_mapy_biegu/"+idZalogowanego+"/"+index);

                        riversRef.putFile(zdjecieMapy)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        riversRef.getDownloadUrl().
                                                addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        dataref.setValue(uri.toString());
                                                        ContentResolver contentResolver = getContentResolver ();
                                                        contentResolver.delete(zdjecieMapy,null,null);
                                                            Intent intent = new Intent(SledzenieBiegu.this, PodsumowanieBiegu.class );
                                                            intent.putExtra("indexBiegu",index );
                                                            //intent.putExtra("data",simpleDateFormat.format(gregorianCalendar.getTime()));
                                                            startActivity(intent);
                                                            finish();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                                        Toast.makeText(SledzenieBiegu.this, "Wczytywanie podsumowania...", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                });
            }
        });
    }

    public void zapiszBazieDanych(){

        DatabaseReference referencePosilek = databaseReference.child("Biegi").child(idZalogowanego);
        index = referencePosilek.push().getKey();
        nowyBieg.setIndex(index);
        referencePosilek.child(index).setValue(nowyBieg);

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

        databaseReference.updateChildren(wszystkieposilki, new DatabaseReference.CompletionListener() {
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "temp", null);
        return Uri.parse(path);
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
                stopSledzenieBiegu();
                isPaused=true;
                stopTracking.setVisibility(View.GONE);
                startTracking.setVisibility(View.GONE);
                zapiszBazieDanych();
                zrobScreenMapyZapiszStorage();
                zapisywanie=true;
                dialog.dismiss();
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

                    stopSledzenieBiegu();
                    stopTracking.setVisibility(View.GONE);
                    map.clear();
                    locations.clear();
                    nowyBieg = new Bieg(0.0,0.0,0.0,0.0,simpleDateFormat.format(gregorianCalendar.getTime()),0,"","");
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
                    dialog.dismiss();

                }
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {

        if(zapisywanie!=false){

        }else{
            if(biegNieWystarowal){
                czyZapisacDialog("back");
            }else{
                normalOnbackPressed();
            }
        }
    }


    private void normalOnbackPressed(){
        super.onBackPressed();
    }

    public float oszacujWartoscMET(double sredniaPredkosc){

        float MET = 0;
        Map<Double, Float> mapPredkosciMET = new HashMap<>();

        //k-tempo km/h, v-MET
        mapPredkosciMET.put(0.0,2f);
        mapPredkosciMET.put(2.7356,2.3f);
        mapPredkosciMET.put(4.023,2.9f);
        mapPredkosciMET.put(4.8276,	3.3f);
        mapPredkosciMET.put(5.47128,3.6f);
        mapPredkosciMET.put(6.4368,6f);
        mapPredkosciMET.put(8.046,8.3f);
        mapPredkosciMET.put(8.36784,9f);
        mapPredkosciMET.put(9.6552,9.8f);
        mapPredkosciMET.put(10.78164,10.5f);
        mapPredkosciMET.put(11.2644,11f);
        mapPredkosciMET.put(12.069,11.5f);
        mapPredkosciMET.put(13.83912,12.3f);
        mapPredkosciMET.put(14.4828,12.8f);
        mapPredkosciMET.put(16.092,14.5f);
        mapPredkosciMET.put(17.7012,16f);
        mapPredkosciMET.put(19.3104,19f);
        mapPredkosciMET.put(20.9196,19.8f);
        mapPredkosciMET.put(22.5288,23f);

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

    public static String fortmatTime(int seconds, int minutes, int hours){

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