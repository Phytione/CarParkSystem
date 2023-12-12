package com.example.parkingsystem.SignUpActivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.parkingsystem.CustomItems.CustomToast;
import com.example.parkingsystem.databinding.ActivityOtoparkSahipKayitBinding;
import com.example.parkingsystem.MapsActivities.AdminMapsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtoparkSahipKayitActivity extends AppCompatActivity {
    private ActivityOtoparkSahipKayitBinding binding;
    private FirebaseAuth firebaseAuth;
    private String verificationId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityOtoparkSahipKayitBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        firebaseAuth=FirebaseAuth.getInstance();

        binding.btnDogrulama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(binding.edtPhoneKayit.getText().toString())){
                    showToast("Lütfen geçerli bir telefon numarası giriniz");
                }else {
                    String phone="+90"+ binding.edtPhoneKayit.getText().toString();
                    sendVerificationCode(phone);
                }

            }
        });
        binding.btnOtoparkSahipKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(binding.edtDogrulamaKodu.getText().toString())){
                    showToast("Lütfen geçerli doğrulama kodunu giriniz");

                }else {

                    verifyCode(binding.edtDogrulamaKodu.getText().toString());
                }
            }
        });
    }

    private void signInWithCredential(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(OtoparkSahipKayitActivity.this, AdminMapsActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    showToast(task.getException().getMessage());
                }

            }
        });
    }

    private void sendVerificationCode(String number){
        PhoneAuthOptions options=PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(number).setTimeout(60L,TimeUnit.SECONDS).setActivity(this).setCallbacks(mCallBack).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
        mCallBack= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken forceResendingToken){
            super.onCodeSent(s,forceResendingToken);
            verificationId=s;
        }
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            final String code=phoneAuthCredential.getSmsCode();

            if(code!=null){
                binding.edtDogrulamaKodu.setText(code);
                verifyCode(code);

            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            showToast(e.getMessage());

        }
    };
    private void verifyCode(String code){
        PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(verificationId,code);
        signInWithCredential(phoneAuthCredential);
    }
    private void showToast(String message){
        CustomToast.showToast(getApplicationContext(),message);
    }










}