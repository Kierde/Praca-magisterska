package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import Deserialization.Dish;

public class BazaProduktowUzytkownikow extends Fragment {

    View mainView;
    private final List<Posilek> posilkiUzytkownikow = new ArrayList<>();
    private WyszukanyPosilekAdapter dodaneDoBazyWspAdapter;
    RecyclerView dodane;
    DatabaseReference databaseReferenceMain;
    ImageButton przeszukajBaze;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.activity_baza_produktow_uzytkownikow, container, false);

        databaseReferenceMain = FirebaseDatabase.getInstance().getReference();
        dodane = (RecyclerView)mainView.findViewById(R.id.wyszukaneRecyclerView);
        dodaneDoBazyWspAdapter = new WyszukanyPosilekAdapter(null,posilkiUzytkownikow,((WyszukaneBazaPosilkow)getActivity()).nazwaPosilku);
        dodane.setHasFixedSize(true);
        dodane.setLayoutManager(new LinearLayoutManager(getActivity()));

        posilkiUzytkownikow.clear();
        wczytajBazePosilkow();
        dodane.setAdapter(dodaneDoBazyWspAdapter);

        return mainView;
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