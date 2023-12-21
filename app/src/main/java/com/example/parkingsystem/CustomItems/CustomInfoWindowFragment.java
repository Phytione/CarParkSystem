package com.example.parkingsystem.CustomItems;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.parkingsystem.MapsActivities.AdminMapsActivity;
import com.example.parkingsystem.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class CustomInfoWindowFragment extends DialogFragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    Context mContext;

    private Button kaydetButton;
    private Button iptalButton;


    public CustomInfoWindowFragment() {
        // Boş kurucu metot
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_info_window, container, false);

        EditText otoparkAdiEditText = view.findViewById(R.id.edtOtoparkAdi);
        EditText aracSayisiEditText = view.findViewById(R.id.edtAraclar);
        EditText elektrikliAraclarEditText = view.findViewById(R.id.edtElektrikliAraclar);
        EditText saatlikUcretEditText = view.findViewById(R.id.edtSaatlikUcret);
        kaydetButton = view.findViewById(R.id.btnKaydet);
        iptalButton = view.findViewById(R.id.btnIptal);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mContext=getContext();
        Bundle args = getArguments();
        if (args != null) {
            double latitude = args.getDouble("latitude");
            double longitude = args.getDouble("longitude");
            kaydetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Firebase'e veri kaydetme işlemleri burada gerçekleştirilecek
                    String otoparkAdi = otoparkAdiEditText.getText().toString();
                    String aracSayisi = aracSayisiEditText.getText().toString();
                    String elektrikliAraclar = elektrikliAraclarEditText.getText().toString();
                    String saatlikUcret = saatlikUcretEditText.getText().toString();
                    LatLng latLng=new LatLng(latitude,longitude);
                    String bosAracSayisi=aracSayisi;

                    if (otoparkAdi.equals("") || aracSayisi.equals("") || elektrikliAraclar.equals("") || saatlikUcret.equals("")){
                        Toast.makeText(mContext,"Lütfen tüm alanları doldurunuz",Toast.LENGTH_SHORT).show();
                    }else{
                        saveOtoparkInfo(otoparkAdi,aracSayisi,elektrikliAraclar,saatlikUcret,latLng,bosAracSayisi);
                        onKaydetButtonClick();
                        dismiss(); // Dialog penceresini kapat
                    }

                }
            });

            iptalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    Toast.makeText(mContext,"İşlem iptal edildi",Toast.LENGTH_SHORT).show();
                }
            });
            
        }
        return view;


    }
    private void onKaydetButtonClick(){
        LatLng latLng=getArguments().getParcelable("latLng");
        if(latLng!=null){
            ((AdminMapsActivity) getActivity()).onKaydetButtonClick(latLng);
        }
    }


    private void saveOtoparkInfo(String otoparkAdi,String aracSayisi,String elektrikliAraclar,String saatlikUcret,LatLng latLng,String bosAracSayisi){
        if(latLng!=null){
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String userNumber=currentUser.getPhoneNumber().toString();
            HashMap<String, Object> otoparkData=new HashMap<>();
            otoparkData.put("otoparkAdi",otoparkAdi);
            otoparkData.put("aracSayisi",aracSayisi);
            otoparkData.put("elektrikliAraclar",elektrikliAraclar);
            otoparkData.put("saatlikUcret",saatlikUcret);
            otoparkData.put("latitude", latLng.latitude);
            otoparkData.put("longitude", latLng.longitude);
            otoparkData.put("userNumber",userNumber);
            otoparkData.put("bosArac",bosAracSayisi);


            firebaseFirestore.collection("Otoparklar").add(otoparkData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(mContext,"Otopark başarıyla kaydedildi",Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(mContext!=null){
                        Toast.makeText(mContext,"Bir hata oluştu. Tekrar deneyiniz",Toast.LENGTH_SHORT).show();
                    }


                }
            });


        }else{
            Toast.makeText(mContext,"Beklenmeyen hata",Toast.LENGTH_SHORT).show();
        }


    }
    private void showToast(String message) {
        CustomToast.showToast(mContext, message);
    }








}
