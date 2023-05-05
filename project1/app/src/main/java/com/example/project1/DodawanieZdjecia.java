package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class DodawanieZdjecia extends AppCompatActivity{

    Button zdjecieAparat;
    Button zdjecieZTelefonu;
    ImageView zdjecieSylwetki;
    String idZalogowanego;
    FirebaseAuth auth;
    DatabaseReference rootRef;
    StorageReference storageReference;
    public static final int CAMERA_REQUEST_CODE = 1888;
    String idWagi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodawanie_zdjecia);

        rootRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();
        zdjecieSylwetki = (ImageView) findViewById(R.id.zdjecieSylwetki);
        zdjecieAparat = (Button) findViewById(R.id.zdjecieAparat);
        zdjecieZTelefonu = (Button) findViewById(R.id.zdjecieTelefonu);
        idWagi =getIntent().getStringExtra("idWagi");


        rootRef.child("Zmiany w wadze").child(idZalogowanego)
                .child(idWagi).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChild("zdjecie sylwetki")){
                    zdjecieAparat.setText("Dodaj inne zdjęcie zrobione aparatem");
                    zdjecieZTelefonu.setText("Dodaj inne zdjęcie z telefonu");
                }

                if(snapshot.hasChild("zdjecie sylwetki")){
                    String zdjecie = snapshot.child("zdjecie sylwetki").getValue().toString();

                    if(!zdjecie.equals("")) {
                        Picasso.get().load(zdjecie).into(zdjecieSylwetki);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        zdjecieZTelefonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wybierzZdjencie();
            }
        });
        zdjecieAparat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

    }
    private void wybierzZdjencie(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){

            Bitmap zdjecie = (Bitmap) data.getExtras().get("data");
            Uri uri = getImageUri(this, zdjecie);
            StorageReference filepath = storageReference.child("Zdjęcia sylwetki").child(idZalogowanego).child(idWagi);

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.child("Zdjęcia sylwetki/"+ idZalogowanego + "/" + idWagi)
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            rootRef.child("Zmiany w wadze").child(idZalogowanego)
                                    .child(idWagi).child("zdjecie sylwetki").setValue(uri.toString());
                        }
                    });
                }
            });
        }

        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Uri imageUri = data.getData();
            StorageReference filepath = storageReference.child("Zdjęcia sylwetki").child(idZalogowanego).child(idWagi);
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    rootRef.child("Zmiany w wadze").child(idZalogowanego)
                            .child(idWagi).child("zdjecie sylwetki").setValue(imageUri.toString());
                }
            });
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}