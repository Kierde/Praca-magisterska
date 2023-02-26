package com.example.project1;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;



public class WiadomoscAdapter extends RecyclerView.Adapter<WiadomoscAdapter.WiadomoscViewHolder> {

    private List<Wiadomosc> listaWiadomosci;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy");



    public WiadomoscAdapter(List<Wiadomosc> listaWiadomosci) {
        this.listaWiadomosci = listaWiadomosci;
    }

    @NonNull
    @Override
    public WiadomoscViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pojedyncza_wiadomosc, parent, false);
        return new WiadomoscViewHolder(view);
    }

    //ustawianie wartosci
    @Override
    public void onBindViewHolder(@NonNull WiadomoscViewHolder holder, int position) {

        //pojedyńczy eleement

        String idZalogowanego = auth.getUid();
        Wiadomosc wiadomosc = listaWiadomosci.get(position);


        String odUzytkownika  = wiadomosc.getOd();

        if(odUzytkownika.equals(idZalogowanego)) {
            holder.textWiadomosci.setBackgroundColor(Color.WHITE);
            holder.textWiadomosci.setTextColor(Color.BLACK);

        }else {
            holder.textWiadomosci.setBackgroundColor(R.drawable.tlo_wiadomosci);
            holder.textWiadomosci.setTextColor(Color.WHITE);
        }

        holder.textWiadomosci.setText(wiadomosc.getWiadomosc());


        reference.child(idZalogowanego).child("zdjecieProfilowe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(odUzytkownika.equals(idZalogowanego)){
                    String zdjecieUrl = snapshot.getValue().toString();
                    if(!zdjecieUrl.equals(""))
                    Picasso.get().load(zdjecieUrl).into(holder.zdjecieProfiloweRozmowcy);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(!odUzytkownika.equals(idZalogowanego)){

            reference.child(odUzytkownika).child("zdjecieProfilowe").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String zdjecieUrlRozmowcy = snapshot.getValue().toString();
                    if(!zdjecieUrlRozmowcy.equals(""))
                    Picasso.get().load(zdjecieUrlRozmowcy).into(holder.zdjecieProfiloweRozmowcy);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaWiadomosci.size();
    }



    public class WiadomoscViewHolder extends RecyclerView.ViewHolder{


        ImageView zdjecieProfiloweRozmowcy;
        TextView textWiadomosci;


        public WiadomoscViewHolder(View itemView){
            super(itemView);

            zdjecieProfiloweRozmowcy = (ImageView) itemView.findViewById(R.id.profiloweRozmowa);
            textWiadomosci = (TextView) itemView.findViewById(R.id.tekstwiadomosci);
        }

    }



}
