package com.example.parkingsystem.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingsystem.Interfaces.AdminReservation;
import com.example.parkingsystem.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminReservationAdapter extends RecyclerView.Adapter<AdminReservationAdapter.AdminReservationViewHolder> {
    private List<AdminReservation> adminReservationList;

    public AdminReservationAdapter(List<AdminReservation> adminReservationList){
        this.adminReservationList=adminReservationList;
    }


    @NonNull
    @Override
    public AdminReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_reservation, parent, false);
        return new AdminReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminReservationAdapter.AdminReservationViewHolder holder, int position) {
        AdminReservation adminReservation = adminReservationList.get(position);

        holder.adminRandevuName.setText(adminReservation.getUserName());
        holder.adminTextViewStartTime.setText(adminReservation.getStartTime());
        holder.adminTextViewEndTime.setText(adminReservation.getEndTime());
        holder.checkTransactionDate(adminReservation.getEndTime());

    }

    @Override
    public int getItemCount() {return adminReservationList.size();}

    public void setAdminReservationList(List<AdminReservation> adminReservationList) {
        Collections.sort(adminReservationList, new Comparator<AdminReservation>() {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            @Override
            public int compare(AdminReservation o1, AdminReservation o2) {
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
        this.adminReservationList = adminReservationList;

        notifyDataSetChanged();
    }

    public static class AdminReservationViewHolder extends RecyclerView.ViewHolder{

        public TextView adminRandevuName;

        public TextView adminTextViewStartTime;

        public  TextView adminTextViewEndTime;

        public LinearLayout linearLayout;

        public AdminReservationViewHolder(@NonNull View itemView) {
            super(itemView);

            adminRandevuName=itemView.findViewById(R.id.adminRandevuName);
            adminTextViewStartTime=itemView.findViewById(R.id.adminTextViewStartTime);
            adminTextViewEndTime=itemView.findViewById(R.id.adminTextViewEndTime);
            linearLayout=itemView.findViewById(R.id.adminLinearLayout);
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
