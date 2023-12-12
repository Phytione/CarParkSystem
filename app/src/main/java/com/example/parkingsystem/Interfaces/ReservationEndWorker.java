package com.example.parkingsystem.Interfaces;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReservationEndWorker extends Worker {

    public ReservationEndWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String selectedOtoparkAdi = getInputData().getString("selectedOtoparkAdi");

        // Rezervasyon süresi sonunda +1 ekle veya diğer güncellemeleri yap
        updateFirestore(selectedOtoparkAdi);

        return Result.success();
    }

    private void updateFirestore(String selectedOtoparkAdi) {
        FirebaseFirestore.getInstance().collection("Otoparklar")
                .whereEqualTo("otoparkAdi", selectedOtoparkAdi)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Firestore üzerinde güncelleme işlemlerini gerçekleştir
                        Map<String, Object> updateData = new HashMap<>();

                        // Örnek: BosArac sayısını bir artır
                        String currentBosAracSayisiStr = document.getString("bosArac");
                        if (currentBosAracSayisiStr != null) {
                            try {
                                int currentBosAracSayisi = Integer.parseInt(currentBosAracSayisiStr);
                                int finalAracSayisi=currentBosAracSayisi+1;
                                updateData.put("bosArac", String.valueOf(finalAracSayisi));
                            } catch (NumberFormatException e) {
                                showToast("BosArac değeri sayıya dönüştürülemedi.");
                            }
                        } else {
                            showToast("BosArac değeri bulunamadı veya null.");
                        }

                        // Diğer güncelleme işlemlerini burada yapabilirsiniz

                        document.getReference().update(updateData)
                                .addOnSuccessListener(aVoid -> {
                                    showToast("Araç yeri tekrar açıldı!");
                                })
                                .addOnFailureListener(e -> showToast("Araç yeri açılırken hata oluştu."));
                    }
                });
    }

    private void showToast(String message) {
        // Toast mesajı gösterme işlemi
    }
}