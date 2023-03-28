package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Deserialization.Dish;

public class BazaProduktowUzytkownikow extends Fragment {

    View mainView;
    private final List<Posilek> posilkiUzytkownikow = new ArrayList<>();
    private WyszukanyPosilekAdapter dodaneDoBazyWspAdapter;
    RecyclerView dodane;
    DatabaseReference databaseReferenceMain;
    ImageButton przeszukajBaze;
    EditText przeszukajNazwa;
    DatabaseReference reference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.activity_baza_produktow_uzytkownikow, container, false);
        przeszukajBaze = (ImageButton) mainView.findViewById(R.id.wyszukajProdukt);
        przeszukajNazwa = (EditText) mainView.findViewById(R.id.nazwaSzukanegoProduktu);

        reference = FirebaseDatabase.getInstance().getReference().child("Baza_posilkow_uzytkonikow");
        databaseReferenceMain = FirebaseDatabase.getInstance().getReference();
        dodane = (RecyclerView)mainView.findViewById(R.id.wyszukaneRecyclerView);
        dodaneDoBazyWspAdapter = new WyszukanyPosilekAdapter(null,posilkiUzytkownikow,((WyszukaneBazaPosilkow)getActivity()).nazwaPosilku,((WyszukaneBazaPosilkow)getActivity()).gregorianCalendar);
        dodane.setHasFixedSize(true);
        dodane.setLayoutManager(new LinearLayoutManager(getActivity()));
        wczytajBazePosilkow();
        dodane.setAdapter(dodaneDoBazyWspAdapter);


        przeszukajBaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(300);
                przeszukajBaze.startAnimation(animation);

                String przeszukajNazwaText = przeszukajNazwa.getText().toString();

                if(!TextUtils.isEmpty(przeszukajNazwaText)){

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){
                                posilkiUzytkownikow.clear();

                                for(DataSnapshot ds:snapshot.getChildren()){

                                    Posilek posilek = ds.getValue(Posilek.class);
                                    if(posilek.getNazwaPosilku().contains(przeszukajNazwaText))
                                        posilkiUzytkownikow.add(posilek);
                                        dodaneDoBazyWspAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    wczytajBazePosilkow();
                    przeszukajNazwa.setError("Podaj nazwę produktu, który chceszy wyszukać!");
                }
            }
        });

        return mainView;
    }



    private void wczytajBazePosilkow(){

        posilkiUzytkownikow.clear();
        databaseReferenceMain.child("Baza_posilkow_uzytkonikow")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Posilek posilek = snapshot.getValue(Posilek.class);
                        assert posilek != null;
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