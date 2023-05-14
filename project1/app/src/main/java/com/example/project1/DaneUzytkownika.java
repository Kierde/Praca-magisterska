package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DaneUzytkownika extends AppCompatActivity {


    BottomNavigationView bottomNavigationView;

    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    TextView nazwaUzytkownika;
    TextView adresEmail;
    TextView plec;
    TextView wiek;
    TextView wzrost;
    TextView waga;
    TextView kalorie;
    TextView cel;
    TextView poziomAkt;
    Button edytuj;
    String userID;
    String nazwaText;
    String adresText;
    String wagaText;
    String wzrostText;
    String wiekText;
    String plecText;
    String celText;
    String poziomText;

    TextView zapotrzeBialko;
    TextView zapotrzeWegle;
    TextView zapotrzebTluszcz;


    String profiloweText;
    CircleImageView zdjecieProfilowe;

    public Uri imageUri;
    static double kalorieRet;

    static float bialkoRet;
    static float wegleRet;
    static float tluszczeRet;
    static float wagaRet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dane_uzytkownika);

        bottomNavigationView = findViewById(R.id.bottom_navigation2);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
        nazwaUzytkownika = (TextView) findViewById(R.id.textViewNazwaUzytkownika);
        adresEmail = (TextView) findViewById(R.id.textViewAresEmail);
        plec = (TextView) findViewById(R.id.textViewPlec);
        wiek = (TextView) findViewById(R.id.textViewWiek);
        wzrost = (TextView) findViewById(R.id.textViewWzrost);
        waga = (TextView) findViewById(R.id.textViewWaga);
        kalorie = (TextView) findViewById(R.id.textViewKalorie);
        poziomAkt = (TextView) findViewById(R.id.textViewPoziom);
        cel = (TextView) findViewById(R.id.textViewCel);
        zdjecieProfilowe = (CircleImageView) findViewById(R.id.profilowe1);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        edytuj = (Button) findViewById(R.id.buttonTest);

        zapotrzeBialko = (TextView) findViewById(R.id.zapotrzebowanieBialko);
        zapotrzeWegle = (TextView) findViewById(R.id.zapotrzebowanieWegle);
        zapotrzebTluszcz =(TextView) findViewById(R.id.zapotrzebowanieTluszcz);

        edytuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEdytujProfil();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        databaseReference =  FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(userID);
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Uzytkownicy").child(userID).child("zdjecieProfilowe");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                nazwaText = snapshot.child("nazwaUzytkownika").getValue().toString();
                adresText = snapshot.child("adresEmail").getValue().toString();
                wagaText = snapshot.child("waga").getValue().toString();
                wzrostText = snapshot.child("wzrost").getValue().toString();
                wiekText = snapshot.child("wiek").getValue().toString();
                plecText = snapshot.child("plec").getValue().toString();
                celText = snapshot.child("cel").getValue().toString();
                poziomText = snapshot.child("poziomAktywnosci").getValue().toString();
                profiloweText =  snapshot.child("zdjecieProfilowe").getValue().toString();


                if(!profiloweText.equals(""))
                Picasso.get().load(profiloweText).into(zdjecieProfilowe);



                int poziomInt = Integer.parseInt(poziomText);
                int celInt = Integer.parseInt(celText);
                int wiekInt = Integer.parseInt(wiekText);
                float wagaFloat = Float.valueOf(wagaText).floatValue();
                wagaRet = wagaFloat;
                float wzrostFloat =Float.valueOf(wzrostText).floatValue();


                nazwaUzytkownika.setText("Nazwa użytkownika: "+nazwaText);
                adresEmail.setText("Adres e-mail: "+adresText);
                wiek.setText("Wiek: "+ wiekText + " lata");
                wzrost.setText("Wzrost: "+ String.format("%.2f",wzrostFloat) + " cm");
                waga.setText("Waga: "+ String.format("%.2f",wagaFloat)  + " kg");


                //tryb siedzacy
                if (poziomInt == 1) {
                    poziomAkt.setText("Poziom aktywności fizycznej: siedzący");
                }
                //malo aktywny
                else if (poziomInt == 2) {
                    poziomAkt.setText("Poziom aktywności fizycznej: mało aktywny");
                }
                //aktywny
                else if (poziomInt == 3) {
                    poziomAkt.setText("Poziom aktywności fizycznej: aktywny");
                }
                //bardzo aktywny
                else if (poziomInt == 4) {
                    poziomAkt.setText("Poziom aktywności fizycznej: bardzo aktywny");
                }

                double ppm;

                 if(plecText.equals("true")) {
                     plec.setText("Płeć: "+"Mężczyzna");

                        ppm = (float) (66.5 + 13.75 * wagaFloat + 5.033 * wzrostFloat - 6.776 * wiekInt);
                 } else {
                     plec.setText("Płeć: "+"Kobieta");
                        ppm = (float) (665.1 + 9.563 * wagaFloat + 1.850 * wzrostFloat - 4.676 * wiekInt);
                 }

                 //Obliczanie PA

                double pa = 0;
                //dorosłe osoby
                 if(wiekInt>=19) {
                     //tryb siedzacy
                     if (poziomInt == 1) {
                          pa = 1;
                     }
                     //malo aktywny
                     else if (poziomInt == 2) {
                          pa =  1.115;
                     }
                     //aktywny
                     else if (poziomInt == 3) {
                         pa = 1.26;
                     }
                     //bardzo aktywny
                     else if (poziomInt == 4) {
                         pa = 1.465;
                     }
                ///mlodzież
                 }else {
                     //tryb siedzacy
                     if (poziomInt == 1) {
                          pa = 1;
                     }
                     //malo aktywny
                     else if (poziomInt == 2) {
                          pa =  1.145;
                     }
                     //aktywny
                     else if (poziomInt == 3) {
                          pa = 1.31;
                     }
                     //bardzo aktywny
                     else if (poziomInt == 4) {
                          pa = 1.49;
                     }

                 }


                double cmp = ppm*pa;
                double cmp1 = cmp;
                double cmp2 = cmp;
                double cmp3 = cmp;

                    //utracenie wagi
                 if(celInt==1){
                     cel.setText("Cel: utracenie wagi");
                    cmp1 = cmp1 - 500;
                     kalorie.setText(String.format("%.2f",cmp1));
                     kalorieRet = cmp1;
                 }
                 //utrzymanie wagi
                 else if(celInt==2){
                     cel.setText("Cel: utrzymanie wagi");
                     cmp2 = cmp2;
                     kalorie.setText(String.format("%.2f",cmp2));
                     kalorieRet = cmp2;
                }
                 //przybranie wagi
                 else if(celInt==3){
                     cel.setText("Cel: przybrane na wadze");
                     cmp3 = cmp3 + 500;
                     kalorie.setText(String.format("%.2f",cmp3));
                     kalorieRet = cmp3;
                }

                 //2 gramy białka na kilogram ciała
                bialkoRet = ((int) wagaFloat) *2;

                float kcalBialka = 4*bialkoRet;
                float kcalTluszczu = (float) (0.25 * kalorieRet);

                //1g tłuszczu - 9 kcal
                tluszczeRet = kcalTluszczu/9;
                float kcalWeglowodanow= (float) (kalorieRet-kcalBialka-kcalTluszczu);
                //1 g weglowodanów - 4 kcal
                wegleRet = kcalWeglowodanow/4;

                zapotrzeBialko.setText("Białko:\n"+ String.format("%.1f",bialkoRet)+" g");
                zapotrzeWegle.setText("Węglowodany:\n " + String.format("%.1f",wegleRet)+" g");
                zapotrzebTluszcz.setText("Tłuszcze:\n "+ String.format("%.1f",tluszczeRet)+" g");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

        zdjecieProfilowe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wybierzZdjencie();
            }
        });
    }


    public void openEdytujProfil(){
        Intent intent = new Intent(this, Edytuj.class);
        startActivity(intent);
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
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            zdjecieProfilowe.setImageURI(imageUri);
            uploadPicture();
        }
    }


    private void uploadPicture(){

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Wgrywanie zdjęcia");
        pd.show();
        StorageReference riversRef = storageReference.child("images/"+userID);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference.child("images/"+ userID).getDownloadUrl().
                                addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        databaseReference1.setValue(uri.toString());
                                    }
                                });

                        pd.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"Coś poszło nie tak", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progres = (100.00 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                    }
                });
    }

    static public double getKal(){
        return kalorieRet;
    }

    public static float getBialkoRet() {
        return bialkoRet;
    }

    public static float getWegleRet() {
        return wegleRet;
    }

    public static float getTluszczeRet() {
        return tluszczeRet;
    }

    public static float getWagaRet(){
        return wagaRet;
    }

}