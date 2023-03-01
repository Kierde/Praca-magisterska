package com.example.project1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaDrm;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class OtwarteRozmowy extends Fragment {

    RecyclerView otwarteRozmowy;
    View mainView;
    DatabaseReference databaseReferenceRozmowy;
    DatabaseReference databaseReferenceRozmowyUzytkownicy;
    DatabaseReference databaseReferenceWiadomosci;
    FirebaseAuth auth;
    String idZalogowanego;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_otwarterozmowy, container, false);
        otwarteRozmowy = (RecyclerView) mainView.findViewById(R.id.otwarteRozmowy);
        otwarteRozmowy.setHasFixedSize(true);
        otwarteRozmowy.setLayoutManager(new LinearLayoutManager(getActivity()));

        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();

        databaseReferenceRozmowyUzytkownicy = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy");
        databaseReferenceRozmowy = FirebaseDatabase.getInstance().getReference().child("Rozmowy").child(idZalogowanego);
        databaseReferenceWiadomosci = FirebaseDatabase.getInstance().getReference().child("wiadomosci").child(idZalogowanego);

        return mainView;
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerOptions<Rozmowa> options = new FirebaseRecyclerOptions.Builder<Rozmowa>()
                .setQuery(databaseReferenceRozmowy, Rozmowa.class)
                .build();
        FirebaseRecyclerAdapter<Rozmowa, RozmowaViewHolder> adapter =
                new FirebaseRecyclerAdapter<Rozmowa,RozmowaViewHolder>(options){

                    @Override
                    protected void onBindViewHolder(@NonNull RozmowaViewHolder holder, int position, @NonNull Rozmowa model) {

                        String uzytkownikListy = getRef(position).getKey();


                        databaseReferenceRozmowyUzytkownicy.child(uzytkownikListy).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String zdjecie = snapshot.child("zdjecieProfilowe").getValue().toString();
                                String nazwaUzytkownika = snapshot.child("nazwaUzytkownika").getValue().toString();

                                if(!zdjecie.equals(""))
                                    Picasso.get().load(zdjecie).into(holder.zdjecieRozmowy);

                                holder.nazwaRozmowcy.setText(nazwaUzytkownika);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                        Query ostatniaWiadomosc = databaseReferenceWiadomosci.child(uzytkownikListy).limitToLast(1);


                        ostatniaWiadomosc.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                String ostatniaWiadomosc  = snapshot.child("wiadomosc").getValue().toString();
                                if(!ostatniaWiadomosc.equals("")) {
                                    holder.ostatniaWiadomosc.setText("Ostatnia wiadomosc rozmowy: " + ostatniaWiadomosc);
                                }else {
                                    holder.ostatniaWiadomosc.setText("Brak wcześniejszych wiadomości");
                                }
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



                        holder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), Czat.class );
                                intent.putExtra("userid",uzytkownikListy );
                                startActivity(intent);

                            }
                        });




                    }


                    @NonNull
                    @Override
                    public RozmowaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pojedyncza_rozmowa,viewGroup, false);
                        RozmowaViewHolder viewHolder = new RozmowaViewHolder(view);
                        return viewHolder;

                    }
                };


        otwarteRozmowy.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RozmowaViewHolder extends RecyclerView.ViewHolder{

        View mview;
        TextView nazwaRozmowcy;
        TextView ostatniaWiadomosc;
        ImageView zdjecieRozmowy;



        public RozmowaViewHolder(@NonNull View itemView){
            super(itemView);

            mview = itemView;
            nazwaRozmowcy = (TextView) mview.findViewById(R.id.nazwaRozmowa);
            ostatniaWiadomosc = (TextView) mview.findViewById(R.id.ostatniaWiadomosc);
            zdjecieRozmowy = (ImageView)  mview.findViewById(R.id.profiloweRozmowa);
        }

    }

}


