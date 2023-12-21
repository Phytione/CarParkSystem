package com.example.parkingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.parkingsystem.CustomItems.CustomToast;
import com.example.parkingsystem.databinding.ActivityProfileBinding;
import com.example.parkingsystem.MapsActivities.MapsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;



    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfileBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        progressBar=findViewById(R.id.progressBar);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();


        bottomNavigationView = (BottomNavigationView) binding.bottomNavigationView;

        setBottomNavigationView();

        Menu menu= bottomNavigationView.getMenu();
        MenuItem menuItem= menu.getItem(2);
        menuItem.setChecked(true);

        String userEmail=firebaseAuth.getCurrentUser().getEmail();
        binding.edtProfileEposta.setText(userEmail);




        getDataForProfile();






    }
    public void btnSave(View view){
        String email=binding.edtProfileEposta.getText().toString();
        String name=binding.edtProfileName.getText().toString();
        String number=binding.edtProfilePhoneNumber.getText().toString();
        String birlestir="+90"+number;
        if(name.equals("") || number.equals("")){
            showToast("Lütfen bütün değerleri doldurunuz");
        } else {
            if (birlestir.length()==13){
                checkPhoneNumber(birlestir, new CheckPhoneNumberListener() {
                    @Override
                    public void onCheckResult(boolean userExists) {
                        if(userExists){
                            showToast("Sistemimizde bu numaraya sahip kullanıcı var. Tekrar deneyiniz");

                        }else{
                            showConfirmationDialog(email,name,birlestir);
                        }

                    }
                });

            }else {
                showToast("Lütfen geçerli bir numara giriniz");
            }


        }

    }

    private void showConfirmationDialog(String email,String name,String number){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Onay");
        builder.setMessage("Profil bilgileriniz sonradan değiştirilemez. Onaylıyor musunuz?");
        builder.setPositiveButton("Evet",(dialog,which)->{
            showToast("Kayıt Başarılı");
            saveProfileInfo(email,name,number);
            binding.btnSave.setVisibility(View.GONE);
        });
        builder.setNegativeButton("Hayır",(dialog,which)->{
            showToast("Değişiklikler onaylanmadı");

        });
        builder.show();

    }
    private void saveProfileInfo(String email,String name,String birlestir){
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> profileData=new HashMap<>();
        profileData.put("email",email);
        profileData.put("name",name);
        profileData.put("number",birlestir);



        firebaseFirestore.collection("Profile").document(email).set(profileData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                //Toast.makeText(ProfileActivity.this,"Profil bilgileriniz başarıyla kaydedilmiştir",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Bir hata oluştu. Tekrar deneyiniz");

            }
        });
        progressBar.setVisibility(View.GONE);


    }

    private void getDataForProfile(){
        progressBar.setVisibility(View.VISIBLE);
        String edtEmail=binding.edtProfileEposta.getText().toString();
        firebaseFirestore.collection("Profile").document(edtEmail).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                String name=documentSnapshot.getString("name");
                String number=documentSnapshot.getString("number");

                binding.edtProfileName.setText(name);
                binding.edtProfilePhoneNumber.setText(number);

                binding.edtProfileName.setEnabled(false);
                binding.edtProfilePhoneNumber.setEnabled(false);

                binding.edtProfileName.setTextColor(Color.BLACK);
                binding.edtProfilePhoneNumber.setTextColor(Color.BLACK);

                checkButtonVisibility();
            }else{
                binding.edtProfileName.setHint("İsminizi giriniz");
                binding.edtProfilePhoneNumber.setHint("5XX-XXX-XXXX");
            }
        }).addOnFailureListener(e -> {
            showToast("Bir hata oluştu. Tekrar deneyiniz");
        });
        progressBar.setVisibility(View.GONE);

    }
    public interface CheckPhoneNumberListener {
        void onCheckResult(boolean userExists);
    }

    private void checkPhoneNumber(String readingNumber,CheckPhoneNumberListener checkPhoneNumberListener){
        firebaseFirestore.collection("Profile").whereEqualTo("number",readingNumber).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()){
                if(task.getResult() != null && !task.getResult().isEmpty()){

                    checkPhoneNumberListener.onCheckResult(true);
                }else{
                    checkPhoneNumberListener.onCheckResult(false);
                }

            }else {
                // Hata durumu
                Exception exception = task.getException();
                showToast("Veri okunurken hata oluştu: "+exception);
                checkPhoneNumberListener.onCheckResult(false);

            }


        });


    }



    public void btnCikisYap(View view){
        firebaseAuth.signOut();
        Intent intentSignOut = new Intent(ProfileActivity.this,MainActivity.class);
        startActivity(intentSignOut);
        finish();

    }



    private void setBottomNavigationView(){

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.homeScreen){
                    Intent intentToHome=new Intent(ProfileActivity.this, MapsActivity.class);
                    intentToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentToHome);
                } else if (item.getItemId()==R.id.islemlerScreen) {
                    Intent intentToIslemler=new Intent(ProfileActivity.this,IslemlerActivity.class);
                    finish();
                    startActivity(intentToIslemler);

                } else if (item.getItemId()==R.id.profileScreen) {

                }else{
                    showToast("Beklenmeyen bir hata oluştu");
                }

                return false;
            }
        });
    }

    private void checkButtonVisibility() {
        String number = binding.edtProfilePhoneNumber.getText().toString();
        String name = binding.edtProfileName.getText().toString();

        Log.d("kesin",number);
        Log.d("kesin",name);

        if (!name.isEmpty() && !number.isEmpty()) {
            // İkisi de doluysa btnSave'i gizle

            binding.btnSave.setVisibility(View.GONE);
        } else {
            // İkisi de boşsa btnSave'i göster
            binding.btnSave.setVisibility(View.VISIBLE);
        }
    }



    private void showToast(String message){
        CustomToast.showToast(getApplicationContext(),message);
    }


}