package com.example.project1;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HistoriaBiegow extends AppCompatActivity {

    private final List<Bieg> listaBiegow = new ArrayList<>();
    private BiegAdapter biegAdapter;
    RecyclerView biegRecyclerView;
    FirebaseAuth auth;
    DatabaseReference databaseReferenceMain;
    String idZalogowanego;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia_biegow);


        databaseReferenceMain = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();

        biegRecyclerView = (RecyclerView) findViewById(R.id.historiaBIegowRecycler);
        biegAdapter = new BiegAdapter(listaBiegow);
        biegRecyclerView.setHasFixedSize(true);
        biegRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        biegRecyclerView.setAdapter(biegAdapter);

        wczytajBiegi();
    }

    private void wczytajBiegi() {

        databaseReferenceMain.child("Biegi").child(idZalogowanego)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Bieg bieg = snapshot.getValue(Bieg.class);
                        listaBiegow.add(bieg);
                        biegAdapter.notifyDataSetChanged();
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