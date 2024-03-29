package com.example.project1;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

public class PosilekAdapter extends RecyclerView.Adapter<PosilekAdapter.PosilekViewHolder> {


    GregorianCalendar today = new GregorianCalendar();
    GregorianCalendar dt1 = new GregorianCalendar();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    String todayDate =  simpleDateFormat.format(today.getTime());


    void zmienDate(GregorianCalendar nowaData){

        this.dt1 = nowaData;

         reference = FirebaseDatabase.getInstance().getReference("Wszystkie posilki uzytkownika do monitora posilkow")
                .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime()));

         sniadanieRef = FirebaseDatabase.getInstance().getReference("Dziennik_posilkow")
                .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime())).child("Sniadanie");

         obiadRef = FirebaseDatabase.getInstance().getReference("Dziennik_posilkow")
                .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime())).child("Obiad");

         kolacjaRef = FirebaseDatabase.getInstance().getReference("Dziennik_posilkow")
                .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime())).child("Kolacja");

         cwiczeniaRef = FirebaseDatabase.getInstance().getReference("Dziennik_posilkow")
                .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime())).child("Cwiczenia");

         przekaskiRef = FirebaseDatabase.getInstance().getReference("Dziennik_posilkow")
                 .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime())).child("Przekaski");
    }

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String zalogowanyId = auth.getUid();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Wszystkie posilki uzytkownika do monitora posilkow")
            .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime()));

    DatabaseReference sniadanieRef = FirebaseDatabase.getInstance().getReference("Dziennik_posilkow")
            .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime())).child("Sniadanie");

    DatabaseReference obiadRef = FirebaseDatabase.getInstance().getReference("Dziennik_posilkow")
            .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime())).child("Obiad");

    DatabaseReference kolacjaRef = FirebaseDatabase.getInstance().getReference("Dziennik_posilkow")
            .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime())).child("Kolacja");

    DatabaseReference cwiczeniaRef = FirebaseDatabase.getInstance().getReference("Dziennik_posilkow")
            .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime())).child("Cwiczenia");

    DatabaseReference przekaskiRef = FirebaseDatabase.getInstance().getReference("Dziennik_posilkow")
            .child(zalogowanyId).child(simpleDateFormat.format(dt1.getTime())).child("Przekaski");


    private List<Posilek> posilekList;

    public PosilekAdapter(List<Posilek> posilekList) {
        this.posilekList = posilekList;
    }




    @NonNull
    @Override
    public PosilekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pojedynczy_posilek, parent, false);
        return new PosilekViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PosilekViewHolder holder, int position) {

       Posilek posilek = posilekList.get(position);
       holder.nazwaJedzenia.setText(posilek.getNazwaPosilku());
       holder.iloscKalorii.setText(String.valueOf(posilek.getKalorycznosc())+" \n kcal");

      if(posilek.getKalorycznosc()>0){

          holder.tluszcz.setText("T. " + String.format("%.1f",posilek.getTluszcz()) + "g");
          holder.wegle.setText("B." + String.format("%.1f",posilek.getBialko()) + "g");
          holder.bialko.setText("W." +String.format("%.1f", posilek.getWeglowodany()) + "g");
      }

        if(!todayDate.equals(simpleDateFormat.format(dt1.getTime()))){
            holder.usunPosilek.setVisibility(View.GONE);
        }else{
            holder.usunPosilek.setVisibility(View.VISIBLE);
        }


        holder.usunPosilek.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Animation animation = new AlphaAnimation(1.0f, 0.0f);
               animation.setDuration(300);
               holder.usunPosilek.startAnimation(animation);

               String index = posilekList.get(position).getIndex();
               Query mQuery = reference.orderByChild("index").equalTo(index);


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

               Query mQuery1 = sniadanieRef.orderByChild("index").equalTo(index);
               mQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for(DataSnapshot ds :snapshot.getChildren()){
                           ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   posilekList.remove(posilekList.get(position));
                                   notifyItemRemoved(position);
                               }
                           });
                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

               Query mQuery2 = obiadRef.orderByChild("index").equalTo(index);
               mQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for(DataSnapshot ds :snapshot.getChildren()){
                           ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   posilekList.remove(posilekList.get(position));
                                   notifyItemRemoved(position);
                               }
                           });
                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
               Query mQuery3 = kolacjaRef.orderByChild("index").equalTo(index);
               mQuery3.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for(DataSnapshot ds :snapshot.getChildren()){
                           ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   posilekList.remove(posilekList.get(position));
                                   notifyItemRemoved(position);
                               }
                           });
                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
               Query mQuery4 = przekaskiRef.orderByChild("index").equalTo(index);
               mQuery4.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for(DataSnapshot ds :snapshot.getChildren()){
                           ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   posilekList.remove(posilekList.get(position));
                                   notifyItemRemoved(position);
                               }
                           });
                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
               Query mQuery5 = cwiczeniaRef.orderByChild("index").equalTo(index);
               mQuery5.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for(DataSnapshot ds :snapshot.getChildren()){
                           ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   posilekList.remove(posilekList.get(position));
                                   notifyItemRemoved(position);
                               }
                           });
                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

           }
       });


    }

    @Override
    public int getItemCount() {
        return posilekList.size();
    }



    public class PosilekViewHolder extends RecyclerView.ViewHolder{

        TextView nazwaJedzenia;
        TextView iloscKalorii;
        TextView bialko;
        TextView wegle;
        TextView tluszcz;
        ImageButton usunPosilek;

        @SuppressLint("WrongViewCast")
        public PosilekViewHolder(View itemView){
            super(itemView);

            nazwaJedzenia = (TextView) itemView.findViewById(R.id.nazwaPosilku);
            iloscKalorii = (TextView) itemView.findViewById(R.id.kalorie);
            bialko =(TextView) itemView.findViewById(R.id.bialkoPosilku);
            wegle= (TextView) itemView.findViewById(R.id.carboPosilku);
            tluszcz =(TextView)itemView.findViewById(R.id.tluszczPosilku);
            usunPosilek = (ImageButton) itemView.findViewById(R.id.usunPosilek);
        }

    }
}
