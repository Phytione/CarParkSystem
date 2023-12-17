package com.example.parkingsystem.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingsystem.Interfaces.OtoparkInfo;
import com.example.parkingsystem.MapsActivities.AdminMapsActivity;
import com.example.parkingsystem.MapsActivities.MapsActivity;
import com.example.parkingsystem.databinding.RecylerRowBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OtoparkAdapter extends RecyclerView.Adapter<OtoparkAdapter.OtoparkHolder> {

    private ArrayList<OtoparkInfo> otoparkInfoArrayList;
    private Context context;
    private OnDataSetChangedListener onDataSetChangedListener;
    public OtoparkAdapter(Context context,ArrayList<OtoparkInfo> otoparkInfoArrayList){
        this.otoparkInfoArrayList=otoparkInfoArrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public OtoparkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecylerRowBinding recylerRowBinding = RecylerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new OtoparkHolder(recylerRowBinding);
    }

    @Override
    public int getItemCount() {
        return otoparkInfoArrayList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull OtoparkHolder holder, int position) {

        holder.recylerRowBinding.txtOtoparkAdi.setText(otoparkInfoArrayList.get(position).otoparkAdi);
        holder.recylerRowBinding.txtAracSayisi.setText(otoparkInfoArrayList.get(position).aracSayisi);
        holder.recylerRowBinding.txtElektrikliAraclar.setText(otoparkInfoArrayList.get(position).elektrikliAraclar);
        holder.recylerRowBinding.txtSaatlikUcret.setText(otoparkInfoArrayList.get(position).saatlikUcret);
        holder.recylerRowBinding.txtMesafe.setText(otoparkInfoArrayList.get(position).distance);
        holder.recylerRowBinding.bosAracSayisi.setText(otoparkInfoArrayList.get(position).bosAracSayisi);


    }

    class OtoparkHolder extends RecyclerView.ViewHolder{

        RecylerRowBinding recylerRowBinding;

        public OtoparkHolder(RecylerRowBinding recylerRowBinding) {
            super(recylerRowBinding.getRoot());
            this.recylerRowBinding=recylerRowBinding;
        }
    }
    public void setOnDataSetChangedListener(OnDataSetChangedListener listener) {
        this.onDataSetChangedListener = listener;
    }
    public void sortOtoparkInfoByDistance() {

        Collections.sort(otoparkInfoArrayList, new Comparator<OtoparkInfo>() {
            @Override
            public int compare(OtoparkInfo o1, OtoparkInfo o2) {
                // Mesafe değerlerini double'a çevirip karşılaştırma yap
                double distance1 = Double.parseDouble(o1.distance.replace(" km", ""));
                double distance2 = Double.parseDouble(o2.distance.replace(" km", ""));
                return Double.compare(distance1, distance2);
            }
        });

        // Veri setinde değişiklik olduğunu adapter'a bildir
        notifyDataSetChanged();

        if(onDataSetChangedListener != null){
            onDataSetChangedListener.onDataSetChanged();
        }

    }
    public interface OnDataSetChangedListener {
        void onDataSetChanged();
    }

}
