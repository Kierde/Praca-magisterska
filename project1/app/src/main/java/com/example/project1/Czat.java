package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Czat extends AppCompatActivity {

    DatabaseReference databaseReferenceUzytkownicy;
    DatabaseReference mainRef;
    FirebaseAuth auth;
    String idZalogowany;
    String idUserRozmawiany;
    TextView nazwa;
    ImageView profilowe;
    ImageButton wyslij;
    EditText wiadomosc;

    private final List<Wiadomosc> listaWiadomosci = new ArrayList<>();
    RecyclerView czatLog;
    private WiadomoscAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_czat);

        auth = FirebaseAuth.getInstance();
        idZalogowany = auth.getUid();
        idUserRozmawiany = getIntent().getStringExtra("userid");
        nazwa = (TextView) findViewById(R.id.nazwa1);
        profilowe = (ImageView) findViewById(R.id.profilowe1);
        wyslij = (ImageButton) findViewById(R.id.wyslij);
        wiadomosc = (EditText) findViewById(R.id.wiadomosc);

        databaseReferenceUzytkownicy = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(idUserRozmawiany);

        //może być nie potrzebne

        mainRef = FirebaseDatabase.getInstance().getReference();

        adapter = new WiadomoscAdapter(listaWiadomosci);
        czatLog = (RecyclerView) findViewById(R.id.czatLog);
        czatLog.setHasFixedSize(true);
        czatLog.setLayoutManager(new LinearLayoutManager(this));
        czatLog.setAdapter(adapter);
        wyczytajWiadomosci();



        databaseReferenceUzytkownicy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String nazwaUzytkownika = snapshot.child("nazwaUzytkownika").getValue().toString();
                String zdjecieProfilowe = snapshot.child("zdjecieProfilowe").getValue().toString();

                nazwa.setText(nazwaUzytkownika);
                if(!zdjecieProfilowe.equals(""))
                    Picasso.get().load(zdjecieProfilowe).into(profilowe);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mainRef.child("czat").child(idZalogowany).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.hasChild(idUserRozmawiany)){

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Rozmowy/"+ idZalogowany+"/" + idUserRozmawiany, chatAddMap);
                    chatUserMap.put("Rozmowy/"+ idUserRozmawiany+"/" + idZalogowany, chatAddMap);

                    mainRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                            if(error !=null){
                                Log.d("CHAT_LOG", error.getMessage().toString());
                            }
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        wyslij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wyslijWiadomosc();
            }
        });

    }


    private void wyslijWiadomosc() {

        String wiadomoscTekstowa = wiadomosc.getText().toString();

        if(!TextUtils.isEmpty(wiadomoscTekstowa)){

            String zalogowanyRef  = "wiadomosci/"+ idZalogowany + "/" + idUserRozmawiany;
            String rozmowcaRef = "wiadomosci/"+ idUserRozmawiany + "/" + idZalogowany;

            DatabaseReference userPush = mainRef.child("wiadomosci")
                    .child(idZalogowany).child(idUserRozmawiany).push();

            String pushId = userPush.getKey();

            Map wiadomoscMap = new HashMap();
            wiadomoscMap.put("wiadomosc", wiadomoscTekstowa);
            wiadomoscMap.put("typ", "text");
            wiadomoscMap.put("od", idZalogowany);

            Map wiadomosciUzytkownikaMap = new HashMap();
            wiadomosciUzytkownikaMap.put(zalogowanyRef+"/"+pushId, wiadomoscMap);
            wiadomosciUzytkownikaMap.put(rozmowcaRef+"/"+pushId, wiadomoscMap);
            wiadomosc.getText().clear();

            mainRef.updateChildren(wiadomosciUzytkownikaMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if(error != null){
                        Log.d("CHAT_LOG", error.getMessage().toString());
                    }
                }
            });
        }
    }

    private void wyczytajWiadomosci(){

        mainRef.child("wiadomosci").child(idZalogowany).child(idUserRozmawiany)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        Wiadomosc wiadomosc = snapshot.getValue(Wiadomosc.class);
                        listaWiadomosci.add(wiadomosc);
                        adapter.notifyDataSetChanged();

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