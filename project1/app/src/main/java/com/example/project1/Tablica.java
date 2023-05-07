package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.VersionInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.BoringLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tablica extends AppCompatActivity {

    BottomNavigationView tablica;
    String idZalogowanego;
    String userIdZnajomego;
    FirebaseAuth auth;

    DatabaseReference refRoot;
    DatabaseReference zapisPostow;


    RecyclerView postyView;
    CircleImageView profiloweTablica;
    TextView nazwaTablica;
    EditText post;
    Button opublikuj;
    TextView edyutjProfil;

    boolean stanPolubienia;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablica);

        tablica = (BottomNavigationView) findViewById(R.id.bottom_navigation2);
        tablica.setSelectedItemId(R.id.Tablica);

        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();
        userIdZnajomego = getIntent().getStringExtra("userid");
        refRoot  = FirebaseDatabase.getInstance().getReference();

        /////////////////////////////////////////////////////////////////////
        profiloweTablica = (CircleImageView) findViewById(R.id.profiloweTab2);
        nazwaTablica = (TextView) findViewById(R.id.nazwaTablica);
        post = (EditText) findViewById(R.id.komentarzTresc);
        opublikuj = (Button) findViewById(R.id.opublikuj1);
        edyutjProfil = (TextView) findViewById(R.id.edytujProfil1);

        postyView = (RecyclerView) findViewById(R.id.posty);
        postyView.setHasFixedSize(true);
        postyView.setLayoutManager(new LinearLayoutManager(Tablica.this));



        if(userIdZnajomego!=null){
            idZalogowanego = userIdZnajomego;
            opublikuj.setVisibility(View.GONE);
            post.setVisibility(View.GONE);
            edyutjProfil.setVisibility(View.GONE);
        }

        zapisPostow = refRoot.child("Posty").child(idZalogowanego);




        tablica.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),
                                EkranGlowny.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),
                                ZapisPosilkow.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.wiecej:
                        startActivity(new Intent(getApplicationContext(),
                                Inne.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.Tablica:
                        startActivity(new Intent(getApplicationContext(),
                                Tablica.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        refRoot.child("Uzytkownicy").child(idZalogowanego).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String profilowe = snapshot.child("zdjecieProfilowe").getValue().toString();
                String nazwaUzytkownika = snapshot.child("nazwaUzytkownika").getValue().toString();


                nazwaTablica.setText(nazwaUzytkownika);

                if(!profilowe.equals(""))
                    Picasso.get().load(profilowe).into(profiloweTablica);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        opublikuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    opublikujPost();
                }
        });


        edyutjProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEdycjaProfilu();
            }
        });


    }



    private void opublikujPost() {

        String trescPostu = post.getText().toString();

        if(!TextUtils.isEmpty(trescPostu)) {

            GregorianCalendar data = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            String ref = "Posty/" + idZalogowanego;

            DatabaseReference postPush = refRoot.child("Posty")
                    .child(idZalogowanego).push();

            String pushId = postPush.getKey();

            Map postMap = new HashMap();
            postMap.put("tresc", trescPostu);
            postMap.put("data", sdf.format(data.getTime()));

            post.getText().clear();


            Map postyUzytkownikaMap = new HashMap();
            postyUzytkownikaMap.put(ref + "/" + pushId, postMap);

            refRoot.updateChildren(postyUzytkownikaMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error != null) {
                        Log.d("CHAT_LOG", error.getMessage().toString());
                    }
                }
            });
        }
    }


    void openEdycjaProfilu(){
        Intent intent = new Intent(this, Edytuj.class);
        startActivity(intent);
    }


    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(zapisPostow, Post.class)
                .build();
        FirebaseRecyclerAdapter<Post, PostViewHolder> adapter =
                new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {

                        String idLog = auth.getUid();


                        refRoot.child("Polubienia").child(getRef(position).getKey().toString())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        long iloscPolubien =  snapshot.getChildrenCount();
                                        String iloscText = Long.toString(iloscPolubien);

                                        holder.iloscPolubien.setText("Ten post lubi "+ iloscText+" osób/a");


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });




                        holder.dodajKomentarz.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String postId = getRef(position).getKey();

                                Intent intent = new Intent(Tablica.this, DodawanieKomentarza.class);
                                intent.putExtra("postId", postId );
                                intent.putExtra("userid", idZalogowanego );
                                startActivity(intent);
                            }
                        });

                        refRoot.child("Polubienia").child(getRef(position).getKey().toString())
                                .child(idLog).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.hasChild("polubienie"))
                                stanPolubienia = (boolean) snapshot.child("polubienie").getValue();

                                if(stanPolubienia==true){
                                    holder.lubieTo.setText("Nie lubię tego");
                                }else {
                                    holder.lubieTo.setText("Lubię to");
                                }



                                holder.lubieTo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if(!stanPolubienia==true) {

                                            String polubienieRef = "Polubienia" + "/" + getRef(position).getKey().toString() + "/" + idLog;
                                            Map polubienieMap = new HashMap();
                                            polubienieMap.put("polubienie", true);
                                            Map polubienieUzytkownika = new HashMap();
                                            polubienieUzytkownika.put(polubienieRef, polubienieMap);

                                            refRoot.updateChildren(polubienieUzytkownika, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                    stanPolubienia = true;
                                                    holder.lubieTo.setText("Nie lubie tego");
                                                }
                                            });
                                        }

                                        if(stanPolubienia==true){

                                            refRoot.child("Polubienia").child(getRef(position).getKey().toString())
                                                    .child(idLog).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    stanPolubienia = false;
                                                    holder.lubieTo.setText("Lubie to");
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });







                        if(userIdZnajomego!=null){
                            holder.opcje.setVisibility(View.GONE);
                        }

                        holder.data.setText(model.getData());
                        holder.trescPosta.setText(model.getTresc());

                        refRoot.child("Uzytkownicy").child(idZalogowanego).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String profilowe = snapshot.child("zdjecieProfilowe").getValue().toString();

                                if(!profilowe.equals(""))
                                    Picasso.get().load(profilowe).into(holder.profilowe);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        holder.opcje.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence opcje[] = new CharSequence[]{"Usuń post","Edytuj post"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(Tablica.this);
                                builder.setItems(opcje, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which==0){

                                            refRoot.child("Komentarze").child(getRef(position).getKey())
                                                    .removeValue();

                                            refRoot.child("Polubienia").child(getRef(position).getKey())
                                                    .removeValue();

                                            String text = model.getTresc();
                                            Query query = zapisPostow.orderByChild("tresc").equalTo(text);
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot ds :snapshot.getChildren()){
                                                        ds.getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


                                        }

                                        if(which==1){
                                            String postId = getRef(position).getKey();
                                            Intent intent = new Intent(Tablica.this, EdytujPost.class);
                                            intent.putExtra("postId", postId );
                                            startActivity(intent);
                                        }


                                    }
                                });
                                builder.show();

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pojedynczy_post,viewGroup, false);
                        PostViewHolder viewHolder = new PostViewHolder(view);
                        return viewHolder;
                    }
                };

            postyView.setAdapter(adapter);
            adapter.startListening();



    }



    public static class PostViewHolder extends RecyclerView.ViewHolder{
        View mView;
        CircleImageView profilowe;
        TextView trescPosta;
        TextView data;
        ImageButton opcje;
        Button dodajKomentarz;
        Button lubieTo;
        TextView iloscPolubien;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            profilowe = (CircleImageView) itemView.findViewById(R.id.profiloweTab2);
            trescPosta = (TextView) itemView.findViewById(R.id.trescPostaPoj2);
            data = (TextView) itemView.findViewById(R.id.dataPostu2);
            opcje = (ImageButton) itemView.findViewById(R.id.opcje);
            dodajKomentarz = (Button) itemView.findViewById(R.id.dodajKomentarz);
            lubieTo = (Button) itemView.findViewById(R.id.lubieTo);
            iloscPolubien = (TextView) itemView.findViewById(R.id.iloscPolubien);

        }
    }
 }






