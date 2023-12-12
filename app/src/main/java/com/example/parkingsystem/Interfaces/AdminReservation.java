package com.example.parkingsystem.Interfaces;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminReservation {
    private String userName;

    private String startTime;

    private String endTime;

    public AdminReservation() {

    }


    public AdminReservation(String userName, String startTime, String endTime){

        this.userName=userName;
        this.startTime=startTime;
        this.endTime=endTime;

    }

    public String getUserName(){return userName;}

    public String getStartTime(){return formatDateTime(startTime);}

    public String getEndTime(){return formatDateTime(endTime);}

    private String formatDateTime(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

}
