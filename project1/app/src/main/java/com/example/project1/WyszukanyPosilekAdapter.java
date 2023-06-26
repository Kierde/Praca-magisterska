package com.example.project1;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import Deserialization.Dish;

public class WyszukanyPosilekAdapter extends RecyclerView.Adapter<WyszukanyPosilekAdapter.WyszukanyPosilekViewHolder> {

    private List<Dish> wyszukanyPosilekList;
    private List<Posilek> bazaPosilkowUzytkownika;
    String nazwaPosilku;
    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd-MM-yyyy");;
    GregorianCalendar dt1;
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    String idZalogowanego = fAuth.getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public WyszukanyPosilekAdapter(List<Dish> wyszukanyPosilekList, List<Posilek> bazaPosilkowUzytkownika,String nazwaPosilku,GregorianCalendar dt1) {
        this.wyszukanyPosilekList = wyszukanyPosilekList;
        this.nazwaPosilku = nazwaPosilku;
        this.bazaPosilkowUzytkownika = bazaPosilkowUzytkownika;
        this.dt1 = dt1;
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


        if(wyszukanyPosilekList!=null) {
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
                            holder.iloscKalorii.setText(String.valueOf((wynik)));
                        } else {
                            holder.iloscProduktu.setError("Podaj ilość produktu (w gramach)");
                        }
                }
            });
        }

        if(bazaPosilkowUzytkownika!=null){

            Posilek posilek = bazaPosilkowUzytkownika.get(position);
            holder.nazwaSzukanego.setText(posilek.getNazwaPosilku());
            holder.kalorycznoscSzukanego.setText(posilek.getKalorycznosc() + " kcal");
            holder.tluszczSzukanego.setText(processWith0(String.valueOf(posilek.getTluszcz()))+ " g");
            holder.weglowodanySzukanego.setText(processWith0(String.valueOf(posilek.getWeglowodany())) + " g");
            holder.bialkoSzukanego.setText(processWith0(String.valueOf(posilek.getBialko())) + " g");


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
                            int kcalorie = posilek.getKalorycznosc();
                            int wynik = calculateHowMuchCalories(iloscProduktu, kcalorie);
                            holder.iloscKalorii.setText(Integer.toString(wynik));
                        } else {
                            holder.iloscProduktu.setError("Podaj ilość produktu (w gramach)");
                        }
                }
            });
        }

        holder.dodajDoDziennika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(holder.iloscProduktu.getText().toString().trim())) {

                    //animacja przycisku
                    Animation animation = new AlphaAnimation(1.0f, 0.0f);
                    animation.setDuration(300);
                    holder.dodajDoDziennika.startAnimation(animation);

                    String posilekRef1 = "Wszystkie posilki uzytkownika do monitora posilkow" + "/" + idZalogowanego + "/" + simpleDateFormat.format(dt1.getTime());
                    DatabaseReference push1 = databaseReference.child("Wszystkie posilki uzytkownika do monitora posilkow")
                            .child(idZalogowanego).push();
                    String pushId1 = push1.getKey();
                    String kalorycznosc = holder.iloscKalorii.getText().toString();
                    String kalorycznoscPoZmianach = kalorycznosc.replace("kcal", "");
                    Map posilekMap = new HashMap();
                    posilekMap.put("nazwaPosilku", holder.nazwaSzukanego.getText().toString());
                    posilekMap.put("index", pushId1);
                    posilekMap.put("kalorycznosc", Integer.parseInt(kalorycznoscPoZmianach.trim()));
                    Map wszystkieposilki = new HashMap();
                    wszystkieposilki.put(posilekRef1 +"/"+ pushId1, posilekMap);

                    DatabaseReference referencePosilek = databaseReference.child("Dziennik_posilkow").child(idZalogowanego).child(simpleDateFormat.format(dt1.getTime()))
                            .child(nazwaPosilku);
                    String index = referencePosilek.push().getKey();

                    float bialko = calculateMacro(Float.parseFloat(holder.iloscProduktu.getText().toString()),Float.parseFloat(processStringWithG(holder.bialkoSzukanego.getText().toString())));
                    float weglowodany = calculateMacro(Float.parseFloat(holder.iloscProduktu.getText().toString()),Float.parseFloat(processStringWithG(holder.weglowodanySzukanego.getText().toString())));
                    float tluszcz = calculateMacro(Float.parseFloat(holder.iloscProduktu.getText().toString()),Float.parseFloat(processStringWithG(holder.tluszczSzukanego.getText().toString())));
                    Posilek posilek = new Posilek(Integer.parseInt(kalorycznoscPoZmianach.trim()), holder.nazwaSzukanego.getText().toString(),pushId1, bialko, weglowodany, tluszcz);
                    referencePosilek.child(index).setValue(posilek);

                    databaseReference.updateChildren(wszystkieposilki, new DatabaseReference.CompletionListener() {
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
        int result =0;
        if(amount>0 && kcal>0){
            result= (int) (amount*kcal)/100;
            return result;
        }else{
            return 0;
        }
    }

    static float calculateMacro(float amount, float grams){

        float result= (float) (amount*grams)/100;
        return result;
    }

    public String processStringWithG(String stringToProcess){
        String kalorycznoscPoZmianach = stringToProcess.replace("g", "");
        return kalorycznoscPoZmianach.trim();
    }

    public String processWith0(String stringToProcess){
        String stringPozmianach = stringToProcess.replace(".0", "");
        return stringPozmianach.trim();
    }

    @Override
    public int getItemCount() {

        int size =0;
        if(wyszukanyPosilekList!=null)
             size =wyszukanyPosilekList.size();
        if(bazaPosilkowUzytkownika!=null) {
            size = bazaPosilkowUzytkownika.size();
        }
        return size;
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

