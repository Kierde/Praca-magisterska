package com.example.project1;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BiegAdapter extends RecyclerView.Adapter<BiegAdapter.BiegViewHolder> {

    private List<Bieg> listaBiegow;

    public BiegAdapter(List<Bieg> listaBiegow) {
        this.listaBiegow = listaBiegow;
    }

    @NonNull
    @Override
    public BiegViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pojedynczy_bieg, parent, false);
        return new BiegViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BiegViewHolder holder, int position) {

        Bieg bieg = listaBiegow.get(position);

        holder.data.setText("Bieg"+"            "+bieg.getDataDnia());
        holder.dystans.setText(String.format("%.2f",bieg.getDystans())+"\n km");
        int rounded = (int) Math.round(bieg.getCzas());
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);
        holder.czasTrwania.setText(String.format("%02d",hours) + ":" + String.format("%02d",minutes) +  ":" + String.format("%02d",seconds)+"\n hh:mm:ss");
        holder.srednieTempo.setText(String.valueOf(String.format("%.2f",bieg.getTempo())).replace(".",":")+"\n min/km");


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), PodsumowanieBiegu.class );
                intent.putExtra("indexBiegu",bieg.getIndex() );
                intent.putExtra("data",bieg.getDataDnia());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaBiegow.size();
    }

    public class BiegViewHolder extends RecyclerView.ViewHolder {

       View view;
       TextView data;
       TextView dystans;
       TextView czasTrwania;
       TextView srednieTempo;

        public BiegViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            data = (TextView) itemView.findViewById(R.id.biegplusData);
            dystans = (TextView) itemView.findViewById(R.id.biegDystansLista);
            czasTrwania =(TextView) itemView.findViewById(R.id.biegCzasLista);
            srednieTempo =(TextView) itemView.findViewById(R.id.srednieTempoLista);



        }


    }
}

