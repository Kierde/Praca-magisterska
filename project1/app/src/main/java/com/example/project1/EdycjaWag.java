package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Inherited;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class EdycjaWag extends AppCompatActivity {

    RecyclerView zapisaneWagi;
    DatabaseReference zapisWagRef;
    DatabaseReference rootRef;
    StorageReference storageReference;
    FirebaseAuth auth;
    String idZalogowanego;
    GregorianCalendar dt1;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edycja_wag);


        dt1 = new GregorianCalendar();
        storageReference = FirebaseStorage.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();
        zapisaneWagi = (RecyclerView) findViewById(R.id.zapisaneWagi);
        zapisaneWagi.setHasFixedSize(true);
        zapisaneWagi.setLayoutManager(new LinearLayoutManager(EdycjaWag.this));
        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();
        zapisWagRef = FirebaseDatabase.getInstance().getReference()
                .child("Zmiany w wadze").child(idZalogowanego);
    }


    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerOptions<Waga> options = new FirebaseRecyclerOptions.Builder<Waga>()
                .setQuery(zapisWagRef, Waga.class)
                .build();
        FirebaseRecyclerAdapter<Waga, WagaViewHolder> adapter =
                new FirebaseRecyclerAdapter<Waga, WagaViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull WagaViewHolder holder, int position, @NonNull Waga model) {

                        String idWagi = getRef(position).getKey();

                        DecimalFormat df = new DecimalFormat();
                        df.setMaximumFractionDigits(2);

                        holder.waga.setText(String.format("%.2f",model.getWaga()));
                        holder.data.setText(model.getData());


                        holder.dodajZdjęcie.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent( EdycjaWag.this, DodawanieZdjecia.class);
                                intent.putExtra("idWagi", idWagi );
                                startActivity(intent);

                            }
                        });




                        holder.usun.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String index = model.getIndex();


                                Query mQuery = zapisWagRef.orderByChild("index").equalTo(index) ;
                                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        });

                        holder.edytuj.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String index = model.getIndex();
                                String edytowanaWaga = holder.waga.getText().toString();

                                Query mQuery = zapisWagRef.orderByChild("index").equalTo(index) ;
                                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds :snapshot.getChildren()){
                                            ds.getRef().child("waga").setValue((Float.valueOf(edytowanaWaga).floatValue()));
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                            }
                        });



                    }

                    @NonNull
                    @Override
                    public WagaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pojedyncza_waga,viewGroup, false);
                        WagaViewHolder viewHolder = new WagaViewHolder(view);
                        return  viewHolder;
                    }
                };

        zapisaneWagi.setAdapter(adapter);
        adapter.startListening();

    }

    public static class WagaViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView data;
        EditText waga;
        ImageButton usun;
        ImageButton edytuj;
        ImageButton dodajZdjęcie;

        public WagaViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            waga = (EditText) itemView.findViewById(R.id.waga);
            data = (TextView) itemView.findViewById(R.id.dataWagi);
            usun = (ImageButton) itemView.findViewById(R.id.usun1);
            edytuj = (ImageButton) itemView.findViewById(R.id.edytuj);
            dodajZdjęcie = (ImageButton) itemView.findViewById(R.id.dodajZdjecie);


        }
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,EkranGlowny.class);
        startActivity(intent);

    }


}