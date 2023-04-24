package com.example.project1;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class KilometrAdapter extends RecyclerView.Adapter<KilometrAdapter.KilometrViewHolder> {

    private List<KilometrBiegu> kilometrList;


    public KilometrAdapter(List<KilometrBiegu> kilometrList) {
        this.kilometrList = kilometrList;
    }

    @NonNull
    @Override
    public KilometrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pojedynczy_kilometr, parent, false);
        return new KilometrViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KilometrViewHolder holder, int position) {

        KilometrBiegu kilometr = kilometrList.get(position);
        int rounded = (int) Math.round(kilometr.getCzas());
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        holder.czasKilometra.setText(String.format("%02d",minutes) + ":"+ String.format("%02d",seconds));


        holder.progressKilometra.setProgress(100);
        if(kilometr.getDystans()>=1){
            holder.progressKilometra.setProgress(100);
        }else {
            double progresss = (kilometr.getDystans() * 100);
            holder.progressKilometra.setProgress((int) progresss);
        }

    }

    @Override
    public int getItemCount() {
        return kilometrList.size();
    }

    public class KilometrViewHolder extends RecyclerView.ViewHolder {

        TextView czasKilometra;
        ProgressBar progressKilometra;

        public KilometrViewHolder(View itemView) {
            super(itemView);
            czasKilometra = (TextView) itemView.findViewById(R.id.czasKilometra);
            progressKilometra = (ProgressBar) itemView.findViewById(R.id.progressKilometra);
            progressKilometra.setScaleY(3f);
            progressKilometra.getProgressDrawable().setColorFilter(
                  Color.rgb(255,165,0)  , PorterDuff.Mode.SRC_IN);
        }


    }
}

