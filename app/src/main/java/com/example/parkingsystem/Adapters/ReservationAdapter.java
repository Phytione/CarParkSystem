package com.example.parkingsystem.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingsystem.R;
import com.example.parkingsystem.Interfaces.Reservation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    private List<Reservation> reservationList;

    public ReservationAdapter(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }


    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {

        Reservation reservation = reservationList.get(position);

        holder.textViewOtoparkAdi.setText(reservation.getOtoparkAdi());
        holder.textViewStartTime.setText(reservation.getStartTime());
        holder.textViewEndTime.setText(reservation.getEndTime());
        holder.checkTransactionDate(reservation.getEndTime());


    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }
    public void setReservationList(List<Reservation> reservationList) {

        Collections.sort(reservationList, new Comparator<Reservation>() {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            @Override
            public int compare(Reservation o1, Reservation o2) {
                try {
                    Date date1 = dateFormat.parse(o1.getEndTime());
                    Date date2 = dateFormat.parse(o2.getEndTime());
                    if (date1 != null && date2 != null) {
                        return date2.compareTo(date1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        // Sıralanmış listeyi güncelle
        this.reservationList = reservationList;

        // Veri seti değişikliklerini bildir
        notifyDataSetChanged();
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewOtoparkAdi;
        public TextView textViewStartTime;
        public TextView textViewEndTime;
        public LinearLayout linearLayout;


        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);



            textViewOtoparkAdi = itemView.findViewById(R.id.textViewOtoparkAdi);
            textViewStartTime = itemView.findViewById(R.id.textViewStartTime);
            textViewEndTime = itemView.findViewById(R.id.textViewEndTime);
            linearLayout = itemView.findViewById(R.id.recylerBorder);
        }
        public void checkTransactionDate(String endTime) {

            if (isTransactionDatePassed(endTime)) {
                linearLayout.setBackgroundResource(R.drawable.border_past);
            } else {
                linearLayout.setBackgroundResource(R.drawable.border_not_past);
            }
        }
        private boolean isTransactionDatePassed(String endTime) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            try {
                Date endDate = sdf.parse(endTime);
                Date currentDate = new Date();

                return endDate != null && endDate.before(currentDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }




    }


}
