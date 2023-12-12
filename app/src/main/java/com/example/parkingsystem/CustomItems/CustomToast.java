package com.example.parkingsystem.CustomItems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkingsystem.R;

public class CustomToast {

    public static void showToast(Context context, String message) {
        // Layout inflator ile custom toast layout'unu inflate et
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        // Toast mesajını set et
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        // Toast'ı oluştur ve yerleştir
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}