package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import Deserialization.Dish;

public class BazaProduktowUzytkownikow extends AppCompatActivity {

    private final List<Posilek> posilkiUzytkownikow = new ArrayList<>();
    private WyszukanyPosilekAdapter dodaneDoBazyWspAdapter;
    RecyclerView dodane;
    DatabaseReference databaseReferenceMain;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baza_produktow_uzytkownikow);

        databaseReferenceMain = FirebaseDatabase.getInstance().getReference();
        String nazwaPosilku = getIntent().getStringExtra("nazwaPosilku");
        dodaneDoBazyWspAdapter = new WyszukanyPosilekAdapter(null,posilkiUzytkownikow,nazwaPosilku);
        dodane = (RecyclerView)findViewById(R.id.wyszukaneRecyclerView);
        dodane.setHasFixedSize(true);
        dodane.setLayoutManager(new LinearLayoutManager(this));
        dodane.setAdapter(dodaneDoBazyWspAdapter);

       // Posilek posilek = new Posilek(100,"huj","gkgokgo", 1.2f, 2.2f,3.3f);
        //posilkiUzytkownikow.add(posilek);
        dodaneDoBazyWspAdapter.notifyDataSetChanged();
        wczytajBazePosilkow();

    }


    private void wczytajBazePosilkow(){

        databaseReferenceMain.child("Baza_posilkow_uzytkonikow")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Posilek posilek = snapshot.getValue(Posilek.class);
                        posilkiUzytkownikow.add(posilek);
                        dodaneDoBazyWspAdapter.notifyDataSetChanged();
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







}