package com.example.parkingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.parkingsystem.CustomItems.CustomToast;
import com.example.parkingsystem.MapsActivities.AdminMapsActivity;
import com.example.parkingsystem.databinding.ActivityAdminProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AdminProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    BottomNavigationView bottomNavigationViewAdminProfile;
    private ActivityAdminProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();
        bottomNavigationViewAdminProfile = (BottomNavigationView) binding.bottomNavigationViewAdminProfile;

        setBottomNavigationViewAdmin();

        setEdtText();


    }

    public void btnAdminCikis(View view) {
        firebaseAuth.signOut();
        Intent intentToMain = new Intent(AdminProfileActivity.this, MainActivity.class);
        startActivity(intentToMain);
        finish();
    }




    private void setBottomNavigationViewAdmin() {
        bottomNavigationViewAdminProfile.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.homeScreenAdmin) {
                    Intent intentToAdminMaps=new Intent(AdminProfileActivity.this, AdminMapsActivity.class);
                    intentToAdminMaps.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentToAdminMaps);
                } else if (item.getItemId() == R.id.profileScreenAdmin) {
                } else {
                    showToast("Beklenmeyen bir hata oluştu");
                }
                return true;
            }
        });
    }

    private void showToast(String message) {
        CustomToast.showToast(getApplicationContext(), message);
    }

    private void setEdtText(){
        binding.editTextPhone.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber());
    }



}