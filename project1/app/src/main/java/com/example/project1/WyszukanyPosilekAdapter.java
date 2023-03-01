package com.example.project1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import Deserialization.Dish;

public class WyszukanyPosilekAdapter extends RecyclerView.Adapter<WyszukanyPosilekAdapter.WyszukanyPosilekViewHolder> {

    private List<Dish> wyszukanyPosilekList;

    public WyszukanyPosilekAdapter(List<Dish> wyszukanyPosilekList) {
        this.wyszukanyPosilekList = wyszukanyPosilekList;
    }

    @NonNull
    @Override
    public WyszukanyPosilekAdapter.WyszukanyPosilekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pojedynczy_wyszukany_posilek, parent, false);
        return new WyszukanyPosilekAdapter.WyszukanyPosilekViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull WyszukanyPosilekAdapter.WyszukanyPosilekViewHolder holder, int position) {

        Dish dish = wyszukanyPosilekList.get(position);
        //holder.nazwaSzukanego.setText(dish.name);
        holder.kalorycznoscSzukanego.setText(dish.caloric);
        holder.tluszczSzukanego.setText(dish.fat);
        holder.weglowodanySzukanego.setText(dish.carbon);
        holder.bialkoSzukanego.setText(dish.protein);

    }

    @Override
    public int getItemCount() {
        return wyszukanyPosilekList.size();
    }


    public class WyszukanyPosilekViewHolder extends RecyclerView.ViewHolder {

        TextView nazwaSzukanego;
        TextView kalorycznoscSzukanego;
        TextView tluszczSzukanego;
        TextView weglowodanySzukanego;
        TextView bialkoSzukanego;


        public WyszukanyPosilekViewHolder(View itemView) {
            super(itemView);
             nazwaSzukanego = (TextView) itemView.findViewById(R.id.nazwaSzukanego);
             kalorycznoscSzukanego =(TextView) itemView.findViewById(R.id.kalorycznoscSzukanego);
             tluszczSzukanego = (TextView) itemView.findViewById(R.id.tluszczSzukanego);
             weglowodanySzukanego =(TextView) itemView.findViewById(R.id.weglowodanySzukanego);
             bialkoSzukanego = (TextView) itemView.findViewById(R.id.bialkoSzukanego);
        }


    }
}

