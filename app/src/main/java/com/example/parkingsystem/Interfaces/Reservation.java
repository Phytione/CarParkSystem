package com.example.parkingsystem.Interfaces;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Reservation {
    private String userEmail;
    private String otoparkAdi;
    private String startTime;
    private String endTime;

    public Reservation() {

    }

    public Reservation(String userEmail, String otoparkAdi, String startTime, String endTime) {
        this.userEmail = userEmail;
        this.otoparkAdi = otoparkAdi;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public String getOtoparkAdi(){
        return otoparkAdi;
    }
    public String getStartTime(){
        return formatDateTime(startTime);
    }
    public String getEndTime(){
        return formatDateTime(endTime);
    }
    private String formatDateTime(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime; // Hata durumunda orijinal değeri döndür
        }
    }
}
