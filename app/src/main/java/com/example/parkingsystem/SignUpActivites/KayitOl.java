package com.example.parkingsystem.SignUpActivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.parkingsystem.CustomItems.CustomToast;
import com.example.parkingsystem.databinding.ActivityKayitOlBinding;
import com.example.parkingsystem.MapsActivities.MapsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class KayitOl extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private ActivityKayitOlBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityKayitOlBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        firebaseAuth=FirebaseAuth.getInstance();

    }
    public void btnKayitOl(View view){

        String email=binding.edtTextKayitEposta.getText().toString();
        String sifre=binding.edtTextKayitSifre.getText().toString();
        String sifreDogrulama=binding.edtTextKayitSifreDogrulama.getText().toString();

        if (email.equals("") || sifre.equals("")){
            showToast("Tüm alanları doldurunuz");
        }else{
            if (sifre.equals(sifreDogrulama)){

                firebaseAuth.createUserWithEmailAndPassword(email,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intentToMapsActivity=new Intent(KayitOl.this, MapsActivity.class);
                        startActivity(intentToMapsActivity);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        showToast(e.getLocalizedMessage());
                    }
                });



            }else{
                showToast("Şifre doğrulamanız yapılamadı. Tekrar kontrol ediniz");
            }
        }





    }
    private void showToast(String message){
        CustomToast.showToast(getApplicationContext(),message);
    }

}