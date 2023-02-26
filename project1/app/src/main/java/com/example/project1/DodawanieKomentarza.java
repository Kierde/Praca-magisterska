package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DodawanieKomentarza extends AppCompatActivity {

    DatabaseReference refRoot;
    DatabaseReference zapisKomentarzy;
    FirebaseAuth auth;

    String idZalogowanego;
    String idZnajomego;
    String idPosta;

    RecyclerView komentarze;

    CircleImageView zdjecieProfiloweCircle;
    TextView trescKomentowanegoPosta;
    TextView dataKomentowanegoPosta;
    Button opublikujKomentarz;
    Button lubieTo;
    EditText trescKomentarza;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodawanie_komentarza);

        zdjecieProfiloweCircle = (CircleImageView) findViewById(R.id.profiloweTab2);
        trescKomentowanegoPosta = (TextView) findViewById(R.id.trescPostaPoj2);
        dataKomentowanegoPosta = (TextView) findViewById(R.id.dataPostu2);
        opublikujKomentarz = (Button) findViewById(R.id.opublikuj1);
        trescKomentarza = (EditText) findViewById(R.id.komentarzTresc);




        komentarze = (RecyclerView) findViewById(R.id.komentarze);
        komentarze.setHasFixedSize(true);
        komentarze.setLayoutManager(new LinearLayoutManager(DodawanieKomentarza.this));



        idPosta = getIntent().getStringExtra("postId");
        auth = FirebaseAuth.getInstance();
        refRoot = FirebaseDatabase.getInstance().getReference();
        idZalogowanego = auth.getUid();
        zapisKomentarzy = FirebaseDatabase.getInstance().getReference()
                .child("Komentarze").child(idPosta);
        idZnajomego = getIntent().getStringExtra("userid");

       if(idZnajomego!=null){
            idZalogowanego = idZnajomego;
        }


        refRoot.child("Posty").child(idZalogowanego).child(idPosta)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.hasChild("tresc")&&snapshot.hasChild("data")) {

                            String tresc = snapshot.child("tresc").getValue().toString();
                            String data = snapshot.child("data").getValue().toString();

                            trescKomentowanegoPosta.setText(tresc);
                            dataKomentowanegoPosta.setText(data);
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        refRoot.child("Uzytkownicy").child(idZalogowanego)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String zdjecieProfilowe = snapshot.child("zdjecieProfilowe").getValue().toString();
                        if(!zdjecieProfilowe.equals(""))
                            Picasso.get().load(zdjecieProfilowe).into(zdjecieProfiloweCircle);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


        opublikujKomentarz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dodajKomentarz();
            }
        });

     }

    private void dodajKomentarz() {

        String tresc = trescKomentarza.getText().toString();

        if(!TextUtils.isEmpty(tresc)){

            String idKomentujacego = auth.getUid();

            GregorianCalendar gr = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            String refKomentarz = "Komentarze/" + idPosta;

            DatabaseReference push = refRoot.child("Komentarze").push();

            String pushId = push.getKey();

            Map komentarzMap = new HashMap();

            komentarzMap.put("trescKomentarza",tresc);
            komentarzMap.put("autor", idKomentujacego);
            komentarzMap.put("data", sdf.format(gr.getTime()));
            trescKomentarza.getText().clear();

            Map komentarzUzytkownika = new HashMap();
            komentarzUzytkownika.put(refKomentarz+"/"+pushId, komentarzMap);

            refRoot.updateChildren(komentarzUzytkownika, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error != null) {
                        Log.d("CHAT_LOG", error.getMessage().toString());
                    }
                }
            });

        }
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseRecyclerOptions<Komentarz> options = new FirebaseRecyclerOptions.Builder<Komentarz>()
                .setQuery(zapisKomentarzy, Komentarz.class)
                .build();
        FirebaseRecyclerAdapter<Komentarz, KomentarzViewHolder> adapter =
                new FirebaseRecyclerAdapter<Komentarz, KomentarzViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull KomentarzViewHolder holder, int position, @NonNull Komentarz model) {

                        holder.dataKomentarza.setText(model.getData());
                        holder.trescKomentarza.setText(model.getTrescKomentarza());
                        holder.dodajKomentarz.setVisibility(View.GONE);
                        holder.layout.setBackgroundColor(Color.rgb(255,221, 221));
                        holder.lubieTo.setVisibility(View.GONE);

                        String idZal = auth.getUid();

                        if(model.getAutor().equals(idZal)){
                            holder.edytuj.setVisibility(View.VISIBLE);
                        }else {
                            holder.edytuj.setVisibility(View.GONE);
                        }



                        holder.edytuj.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence opcje[] = new CharSequence[]{"Usuń komentarz","Edytuj komentarz"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(DodawanieKomentarza.this);
                                builder.setItems(opcje, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which==0){
                                            String text = model.getTrescKomentarza();
                                            Query query = zapisKomentarzy.orderByChild("trescKomentarza").equalTo(text);
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
                                            Intent intent = new Intent(DodawanieKomentarza.this, EdytujKomentarz.class);
                                            //intent.putExtra("idKomentarza", );
                                            intent.putExtra("idPosta",idPosta);
                                            intent.putExtra("idKomentarza",getRef(position).getKey());
                                            startActivity(intent);
                                        }

                                    }
                                });
                                builder.show();

                            }
                        });


                        refRoot.child("Uzytkownicy").child(model.getAutor()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String zdjecieProfilowe = snapshot.child("zdjecieProfilowe").getValue().toString();
                                String nazwaKomentujacego = snapshot.child("nazwaUzytkownika").getValue().toString();

                                if(!zdjecieProfilowe.equals(""))
                                Picasso.get().load(zdjecieProfilowe).into(holder.profiloweKomentującego);
                                holder.nazwaUzytkownika.setText(nazwaKomentujacego);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public KomentarzViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pojedynczy_post,viewGroup, false);
                        KomentarzViewHolder viewHolder = new KomentarzViewHolder(view);
                        return viewHolder;
                    }
                };

        komentarze.setAdapter(adapter);
        adapter.startListening();



    }

    public static class KomentarzViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView trescKomentarza;
        TextView dataKomentarza;
        CircleImageView profiloweKomentującego;
        Button dodajKomentarz;
        TextView nazwaUzytkownika;
        ConstraintLayout layout;
        ImageButton edytuj;
        TextView lubieTo;

        public KomentarzViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            trescKomentarza = (TextView) itemView.findViewById(R.id.trescPostaPoj2);
            dataKomentarza = (TextView) itemView.findViewById(R.id.dataPostu2);
            profiloweKomentującego = (CircleImageView) itemView.findViewById(R.id.profiloweTab2);
            dodajKomentarz = (Button) itemView.findViewById(R.id.dodajKomentarz);
            nazwaUzytkownika = (TextView) itemView.findViewById(R.id.nazwaKomentujacego);
            layout = (ConstraintLayout) itemView.findViewById(R.id.layoutPostu);
            edytuj = (ImageButton) itemView.findViewById(R.id.opcje);
            lubieTo = (TextView) itemView.findViewById(R.id.lubieTo);




        }


    }



}