package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EdytujKomentarz extends AppCompatActivity {

    DatabaseReference firebaseDatabase;
    String idPosta;
    String idKomentarza;
    EditText trescKomentarzaEdycji;
    Button zatwierdzZmiany;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edytuj_komentarz);

        trescKomentarzaEdycji = (EditText) findViewById(R.id.trescKomentarzaDoEdycji);
        zatwierdzZmiany = (Button) findViewById(R.id.zatwierdzZmiany1);

        idPosta = getIntent().getStringExtra("idPosta");
        idKomentarza = getIntent().getStringExtra("idKomentarza");

        firebaseDatabase = FirebaseDatabase.getInstance().getReference()
               .child("Komentarze").child(idPosta).child(idKomentarza);



        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String textKomentarza = snapshot.child("trescKomentarza").getValue().toString();

                if(!trescKomentarzaEdycji.equals(""))
                trescKomentarzaEdycji.setText(textKomentarza);

                zatwierdzZmiany.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String data = snapshot.child("data").getValue().toString();
                        String text = trescKomentarzaEdycji.getText().toString();
                        String autor = snapshot.child("autor").getValue().toString();

                        Komentarz komentarz = new Komentarz(text, autor, data);

                        firebaseDatabase.setValue(komentarz);





                    }
                });










            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}