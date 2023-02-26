package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EdytujPost extends AppCompatActivity {

    EditText edytowanyPost;
    Button zatwierdzZmiany;
    FirebaseAuth auth;
    DatabaseReference reference;
    String idZalogowanego;
    String idPosta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edytuj_post);

        idPosta = getIntent().getStringExtra("postId");

        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Posty").child(idZalogowanego)
                .child(idPosta);

        edytowanyPost = (EditText) findViewById(R.id.edycjaposta);
        zatwierdzZmiany = (Button) findViewById(R.id.zatwierdzZminay);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String trescEdytowana;
                String data = null;

                if(snapshot.hasChild("tresc") && snapshot.hasChild("data")) {

                    trescEdytowana = snapshot.child("tresc").getValue().toString();
                    data = snapshot.child("data").getValue().toString();

                    if (!trescEdytowana.equals(""))
                        edytowanyPost.setText(trescEdytowana);

                }


                String finalData = data;
                zatwierdzZmiany.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Post post = new Post(finalData, edytowanyPost.getText().toString());

                        reference.setValue(post);


                    }
                });


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
}