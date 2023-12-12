package com.example.parkingsystem.Interfaces;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.parkingsystem.Interfaces.ReservationEndWorker;

import java.util.concurrent.TimeUnit;

public class ReservationWorker extends Worker {

    public ReservationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // WorkManager tarafından çağrıldığında bu metod çalışacak

        // WorkManager'dan gelen verileri al
        String selectedOtoparkAdi = getInputData().getString("selectedOtoparkAdi");
        long reservationEndTime = getInputData().getLong("reservationEndTime", 0);
        long reservationStartTime = getInputData().getLong("reservationStartTime", 0);

        // Rezervasyon süresi sonunda +1 ekle
        long timeDiffMillis = reservationEndTime - reservationStartTime;
        scheduleReservationEndWork(selectedOtoparkAdi, timeDiffMillis);

        // Başarılı bir şekilde tamamlandığını belirt
        return Result.success();
    }

    private void scheduleReservationEndWork(String selectedOtoparkAdi, long delayMillis) {
        // Belirtilen süre sonunda bir işlemi gerçekleştirmek için WorkManager'ı kullan

        Data inputData = new Data.Builder()
                .putString("selectedOtoparkAdi", selectedOtoparkAdi)
                .build();

        // Belirtilen süre sonunda bir işlemi gerçekleştirmek için OneTimeWorkRequest oluştur
        OneTimeWorkRequest reservationEndWorkRequest =
                new OneTimeWorkRequest.Builder(ReservationEndWorker.class)
                        .setInputData(inputData)
                        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                        .build();

        // WorkManager'a çalışma isteğini gönder
        WorkManager.getInstance(getApplicationContext()).enqueue(reservationEndWorkRequest);
    }



}
