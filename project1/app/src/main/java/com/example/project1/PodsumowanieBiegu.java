package com.example.project1;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class PodsumowanieBiegu extends AppCompatActivity {

    TextView dataPodsumowania;
    TextView dystansPodsumowania;
    TextView czasPodsumowania;
    TextView tempoPodusmowania;
    TextView kcalPodsumowania;
    ImageView zdjecieMapy;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String idZalogowanego;
    String index;
    RecyclerView kilometry;
    private final List<KilometrBiegu> listaKilometrow = new ArrayList<>();
    private KilometrAdapter kilometrAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.podsumowanie_biegu_layout);

        kilometry = (RecyclerView) findViewById(R.id.kilometryBiegu);
        kilometrAdapter = new KilometrAdapter(listaKilometrow);
        kilometry.setHasFixedSize(true);
        kilometry.setLayoutManager(new LinearLayoutManager(this));
        kilometry.setAdapter(kilometrAdapter);
        auth= FirebaseAuth.getInstance();
        idZalogowanego = auth.getUid();
        index = getIntent().getStringExtra("indexBiegu");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Biegi").child(idZalogowanego).child(index);

         dataPodsumowania = (TextView) findViewById(R.id.dataBieguPodsumowania);
         dystansPodsumowania =(TextView) findViewById(R.id.dystansPodsumowania);
         czasPodsumowania = (TextView)  findViewById(R.id.czasTrwaniaPodusmowania);
         tempoPodusmowania =(TextView) findViewById(R.id.srednieTempoPodsumowanie);
         kcalPodsumowania = (TextView) findViewById(R.id.spaloneKaloriePodusmowania);
         zdjecieMapy  = (ImageView) findViewById(R.id.zdjecieMapyBiegu);

         databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {

                 Bieg bieg = snapshot.getValue(Bieg.class);

                 Log.d("size", String.valueOf(bieg.getKilometrBiegus().size()));

                 for(int i=0;i<bieg.getKilometrBiegus().size();i++)
                 listaKilometrow.add(bieg.getKilometrBiegus().get(i));



                 dataPodsumowania.setText("Podsumowanie biegu z dnia: "+bieg.getDataDnia());
                 dystansPodsumowania.setText(String.format("%.2f",bieg.getDystans())+"\n Km");
                 int rounded = (int) Math.round(bieg.czas);
                 int seconds = ((rounded % 86400) % 3600) % 60;
                 int minutes = ((rounded % 86400) % 3600) / 60;
                 int hours = ((rounded % 86400) / 3600);
                 czasPodsumowania.setText(SledzenieBiegu.fortmatTime(seconds,minutes,hours)+"\n hh:mm:ss");
                 tempoPodusmowania.setText(String.valueOf(String.format("%.2f",bieg.getTempo())+"\n min/km").replace(".",":" ));
                 kcalPodsumowania.setText((int)bieg.getSpaloneKalorie()+"\n kcal");
                 Picasso.get().load(bieg.getMapaBiegu()).fit().into(zdjecieMapy);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {
             }
         });
    }
}