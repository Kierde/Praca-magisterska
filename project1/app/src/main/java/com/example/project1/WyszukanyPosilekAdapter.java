package com.example.project1;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import Deserialization.Dish;

public class WyszukanyPosilekAdapter extends RecyclerView.Adapter<WyszukanyPosilekAdapter.WyszukanyPosilekViewHolder> {

    private List<Dish> wyszukanyPosilekList;
    String nazwaPosilku;
    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd-MM-yyyy");;
    GregorianCalendar dt1 = new GregorianCalendar();
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    String idZalogowanego = fAuth.getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public WyszukanyPosilekAdapter(List<Dish> wyszukanyPosilekList, String nazwaPosilku) {
        this.wyszukanyPosilekList = wyszukanyPosilekList;
        this.nazwaPosilku = nazwaPosilku;
    }

    @NonNull
    @Override
    public WyszukanyPosilekAdapter.WyszukanyPosilekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pojedynczy_wyszukany_posilek, parent, false);
        return new WyszukanyPosilekAdapter.WyszukanyPosilekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WyszukanyPosilekAdapter.WyszukanyPosilekViewHolder holder, int position) {

        Dish dish = wyszukanyPosilekList.get(position);
        holder.nazwaSzukanego.setText(dish.name);
        holder.kalorycznoscSzukanego.setText(dish.caloric + " kcal");
        holder.tluszczSzukanego.setText(dish.fat + " g");
        holder.weglowodanySzukanego.setText(dish.carbon + " g");
        holder.bialkoSzukanego.setText(dish.protein + " g");

        holder.iloscProduktu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!TextUtils.isEmpty(holder.iloscProduktu.getText().toString().trim())) {

                    float iloscProduktu = Float.parseFloat(holder.iloscProduktu.getText().toString());
                    int kcalorie = Integer.parseInt(dish.caloric);
                    int wynik = calculateHowMuchCalories(iloscProduktu, kcalorie);
                    holder.iloscKalorii.setText(Integer.toString(wynik));
                } else {
                    holder.iloscProduktu.setError("podaj produktu (w gramach)");
                }
            }
        });

        holder.dodajDoDziennika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(holder.iloscProduktu.getText().toString().trim())) {

                    String posilekRef1 = "Wszystkie posilki uzytkownika do monitora posilkow" + "/" + idZalogowanego + "/" + simpleDateFormat.format(dt1.getTime());
                    String posilekRef = "Dziennik_posilkow/" + idZalogowanego + "/" + simpleDateFormat.format(dt1.getTime()) + "/" + nazwaPosilku;

                    DatabaseReference push1 = databaseReference.child("Wszystkie posilki uzytkownika do monitora posilkow")
                            .child(idZalogowanego).push();

                    DatabaseReference push = databaseReference.child("Dziennik_posilkow")
                            .child(idZalogowanego).push();

                    String pushId = push.getKey();
                    String pushId1 = push1.getKey();


                    String kalorycznosc = holder.kalorycznoscSzukanego.getText().toString();
                    String kalorycznoscPoZmianach = kalorycznosc.replace("kcal", "");
                    Map posilekMap = new HashMap();
                    posilekMap.put("nazwaPosilku", holder.nazwaSzukanego.getText().toString());
                    posilekMap.put("index", pushId1);
                    posilekMap.put("kalorycznosc", Integer.parseInt(kalorycznoscPoZmianach.trim()));


                    Map wszystkieposilki = new HashMap();
                    wszystkieposilki.put(posilekRef1 +"/"+ pushId1, posilekMap);

                    Map czescPosilku = new HashMap();
                    czescPosilku.put(posilekRef+"/"+pushId,posilekMap);

                    databaseReference.updateChildren(wszystkieposilki, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                        }
                    });

                    databaseReference.updateChildren(czescPosilku, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                        }
                    });




                } else {
                    holder.iloscProduktu.setError("podaj wartość liczbową ilości produktu (w gramach)");
                }
            }
        });
    }

    static int calculateHowMuchCalories(float amount, int kcal){

        int result= (int) (amount*kcal)/100;
        return result;
    }


    @Override
    public int getItemCount() {
        return wyszukanyPosilekList.size();
    }

    public class WyszukanyPosilekViewHolder extends RecyclerView.ViewHolder {

        TextView nazwaSzukanego;
        TextView kalorycznoscSzukanego;
        TextView tluszczSzukanego;
        TextView weglowodanySzukanego;
        TextView bialkoSzukanego;
        EditText iloscProduktu;
        TextView iloscKalorii;
        ImageButton dodajDoDziennika;

        public WyszukanyPosilekViewHolder(View itemView) {
            super(itemView);
             nazwaSzukanego = (TextView) itemView.findViewById(R.id.nazwaSzukanego);
             kalorycznoscSzukanego =(TextView) itemView.findViewById(R.id.kalorycznoscSzukanego);
             tluszczSzukanego = (TextView) itemView.findViewById(R.id.tluszczSzukanego);
             weglowodanySzukanego =(TextView) itemView.findViewById(R.id.wegleSzukanego);
             bialkoSzukanego = (TextView) itemView.findViewById(R.id.bialkoSzukanego);
             iloscKalorii = (TextView) itemView.findViewById(R.id.ilosc_cal);
             iloscProduktu = (EditText) itemView.findViewById(R.id.ilosc_gram);
             dodajDoDziennika = (ImageButton) itemView.findViewById(R.id.dodajDoDziennika);
        }

    }
}

