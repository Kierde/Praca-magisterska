package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


import org.eazegraph.lib.utils.Utils;
import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class WszyscyUzytkownicy extends AppCompatActivity {


    RecyclerView listUzytkownikow;
    DatabaseReference reference;
    EditText wyszukajNazwa;
    EditText wiekStart;
    EditText wiekStop;
    ImageButton wyszukajPrzycisk;
    ImageButton wyszukaj;
    Button pokazWszystkich;
    String nazwaGet;
    Spinner statusDiety;
    CheckBox wyszukiwanieCelem;
    CheckBox wyszukiwanieWiekiem;

    int cel;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wszyscy_uzytkownicy);

        wyszukajPrzycisk = (ImageButton) findViewById(R.id.wyszukajButton);
        wyszukaj = (ImageButton) findViewById(R.id.wyszukaj);
        pokazWszystkich = (Button) findViewById(R.id.pokażWszystkich);
        wyszukajNazwa = (EditText) findViewById(R.id.wyszukajNazwa);

        wiekStart = (EditText)findViewById(R.id.wiekStart);
        wiekStop = (EditText) findViewById(R.id.wiekStop);

        wyszukiwanieWiekiem = (CheckBox) findViewById(R.id.checkboxWiek);
        wyszukiwanieCelem = (CheckBox) findViewById(R.id.checkBoxCel);


        statusDiety = (Spinner) findViewById(R.id.szukanieCelem);
        ArrayAdapter<CharSequence> adapterCel = ArrayAdapter.createFromResource(this,R.array.Cel, android.R.layout.simple_spinner_item);
        adapterCel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusDiety.setAdapter(adapterCel);

        listUzytkownikow = (RecyclerView) findViewById(R.id.listaUzytkownikow);
        listUzytkownikow.setHasFixedSize(true);
        listUzytkownikow.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy");
        wczytajUzytkownikow(reference);



        wyszukajPrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nazwaGet = wyszukajNazwa.getText().toString();

                if(!TextUtils.isEmpty(nazwaGet)) {
                    Query query = reference.orderByChild("nazwaUzytkownika").equalTo(nazwaGet);

                    wczytajUzytkownikow(query);
                }else {
                    wczytajUzytkownikow(reference);
                }
            }
        });

        pokazWszystkich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wczytajUzytkownikow(reference);
            }
        });


        statusDiety.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    cel = 1;
                }else if(position==1){
                    cel = 2;
                }else if(position==2){
                    cel = 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        wyszukaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String wiekStartText = wiekStart.getText().toString();
                String wiekStopText = wiekStop.getText().toString();

                if (wyszukiwanieWiekiem.isChecked() && !wyszukiwanieCelem.isChecked()) {

                    if (TextUtils.isEmpty(wiekStartText) || TextUtils.isEmpty(wiekStopText)) {
                        wiekStart.setError("uzupełnij pola!");
                        wiekStop.setError("uzupełnij pola!");
                        return;
                    } else if (Integer.parseInt(wiekStartText) > Integer.parseInt(wiekStopText)) {
                        wiekStop.setError("Pole musi być mniejsze od dolnej granicy wieku");
                        wiekStart.setError("Pole musi być większe od górnej granicy wieku");
                    } else {

                        Query queryWieku = reference.orderByChild("wiek").startAt(Integer.parseInt(wiekStartText)).endAt(Integer.parseInt(wiekStopText));

                        wczytajUzytkownikow(queryWieku);
                    }
                }
                if (wyszukiwanieCelem.isChecked() && !wyszukiwanieWiekiem.isChecked()) {

                    if (cel == 1) {
                        Query query1 = reference.orderByChild("cel").equalTo(1);
                        wczytajUzytkownikow(query1);

                    } else if (cel == 2) {

                        Query query2 = reference.orderByChild("cel").equalTo(2);
                        wczytajUzytkownikow(query2);
                    } else if (cel == 3) {

                        Query query3 = reference.orderByChild("cel").equalTo(3);
                        wczytajUzytkownikow(query3);
                    }
                }

                if (wyszukiwanieWiekiem.isChecked() && wyszukiwanieCelem.isChecked()) {

                    if (TextUtils.isEmpty(wiekStartText) || TextUtils.isEmpty(wiekStopText)) {
                        wiekStart.setError("uzupełnij pola!");
                        wiekStop.setError("uzupełnij pola!");
                        return;
                    } else if (Integer.parseInt(wiekStartText) > Integer.parseInt(wiekStopText)) {
                        wiekStop.setError("Pole musi być mniejsze od dolnej granicy wieku");
                        wiekStart.setError("Pole musi być większe od górnej granicy wieku");
                    }else{
                        String celText = Integer.toString(cel);
                        String start = celText+wiekStartText;
                        String stop = celText+wiekStopText;

                        int startWarunek = Integer.valueOf(start);
                        int stopWarunek = Integer.valueOf(stop);

                        Query query4 = reference.orderByChild("wiek_cel").startAt(startWarunek).endAt(stopWarunek);
                        wczytajUzytkownikow(query4);
                    }
                }
            }
        });

    }

    private void wczytajUzytkownikow(Query query) {

        FirebaseRecyclerOptions<Uzytkownik> options = new FirebaseRecyclerOptions.Builder<Uzytkownik>()
                .setQuery(query, Uzytkownik.class)
                .build();
        FirebaseRecyclerAdapter<Uzytkownik, UzytkownicyViewHolder> adapter =
                new FirebaseRecyclerAdapter<Uzytkownik, UzytkownicyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UzytkownicyViewHolder holder, int position, @NonNull Uzytkownik model) {


                        holder.nazwa.setText("Nazwa użytkownika: " + model.getNazwaUzytkownika());
                        holder.wiek.setText("Wiek: " + model.getWiek() + " lat");


                        if (!model.getZdjecieProfilowe().equals(""))
                            Picasso.get().load(model.getZdjecieProfilowe()).into(holder.zdjcieProfilowe);

                        if (model.getCel() == 1) {
                            holder.status.setText("Status diety: utracenie wagi");
                        } else if (model.getCel() == 2) {
                            holder.status.setText("Status diety: utrzymanie wagi");
                        } else if (model.getCel() == 3) {
                            holder.status.setText("Status diety: przybranie wagi");
                        }
                        String userId = getRef(position).getKey();


                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profilIntent = new Intent(WszyscyUzytkownicy.this, WyslanieZaproszenia.class);
                                profilIntent.putExtra("userid", userId);
                                startActivity(profilIntent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public UzytkownicyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pojedynczy_uzytkownik,viewGroup, false);
                        UzytkownicyViewHolder viewHolder = new UzytkownicyViewHolder(view);
                        return viewHolder;
                    }
                };


        listUzytkownikow.setAdapter(adapter);
        adapter.startListening();
    }



    public static class UzytkownicyViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView nazwa;
        TextView wiek;
        TextView status;
        CircleImageView zdjcieProfilowe;


        public UzytkownicyViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            nazwa = itemView.findViewById(R.id.nazwaPojUzytkownika);
            wiek = itemView.findViewById(R.id.wiekPoj);
            status = itemView.findViewById(R.id.statusPoj);
            zdjcieProfilowe = itemView.findViewById(R.id.profiloweRozmowa);
        }
    }




}


