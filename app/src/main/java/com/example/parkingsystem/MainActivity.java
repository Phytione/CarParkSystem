package com.example.parkingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.parkingsystem.CustomItems.CustomToast;
import com.example.parkingsystem.SignUpActivites.KayitOl;
import com.example.parkingsystem.SignUpActivites.OtoparkSahipKayitActivity;
import com.example.parkingsystem.databinding.ActivityMainBinding;
import com.example.parkingsystem.MapsActivities.AdminMapsActivity;
import com.example.parkingsystem.MapsActivities.MapsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        /*if(user!=null){
            Intent intent=new Intent(MainActivity.this,MapsActivity.class);
            startActivity(intent);
            finish();
        }*/
        provideCheck();





    }
    public void btnGirisUser(View view){
        String email=binding.edtEposta.getText().toString();
        String sifre=binding.edtSifre.getText().toString();

        if (email.equals("") || sifre.equals("")){
            showToast("Tüm alanları doldurunuz");
        }else{
            firebaseAuth.signInWithEmailAndPassword(email,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                   Intent intentToMapsActivity=new Intent(MainActivity.this, MapsActivity.class);
                   startActivity(intentToMapsActivity);
                   finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast(e.getLocalizedMessage());
                }
            });

        }

    }
    public void btnGirisAdmin(View view){
        Intent intentToAdmin=new Intent(MainActivity.this, OtoparkSahipKayitActivity.class);
        startActivity(intentToAdmin);
    }
    public void txtClick(View view){
        Intent intentToRegister=new Intent(MainActivity.this, KayitOl.class);
        startActivity(intentToRegister);
    }
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
    private void provideCheck(){
        if(user!=null){
            List<? extends UserInfo> providers=user.getProviderData();

            for (UserInfo userInfo : providers){
                String providerID=userInfo.getProviderId();

                if(providerID.equals(EmailAuthProvider.PROVIDER_ID)){
                    Intent intentToNormal=new Intent(MainActivity.this,MapsActivity.class);
                    startActivity(intentToNormal);
                    finish();
                } else if (providerID.equals(PhoneAuthProvider.PROVIDER_ID)) {
                    Intent intentToAdmin=new Intent(MainActivity.this, AdminMapsActivity.class);
                    startActivity(intentToAdmin);
                    finish();

                }

            }
        }
    }
    private void showToast(String message){
        CustomToast.showToast(getApplicationContext(),message);
    }


}





