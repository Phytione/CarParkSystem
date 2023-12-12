package com.example.parkingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.parkingsystem.Adapters.ReservationAdapter;
import com.example.parkingsystem.CustomItems.CustomToast;
import com.example.parkingsystem.Interfaces.Reservation;
import com.example.parkingsystem.databinding.ActivityIslemlerBinding;
import com.example.parkingsystem.MapsActivities.MapsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class IslemlerActivity extends AppCompatActivity {
    ArrayList<Reservation> reservationArrayList;
    ReservationAdapter reservationAdapter;
    BottomNavigationView bottomNavigationView;
    private ActivityIslemlerBinding binding;
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityIslemlerBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        bottomNavigationView = (BottomNavigationView) binding.bottomNavigationView;
        setContentView(view);
        setBottomNavigationView();

        reservationArrayList=new ArrayList<>();
        reservationAdapter=new ReservationAdapter(reservationArrayList);
        binding.islemlerRecylerView.setAdapter(reservationAdapter);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        Menu menu= bottomNavigationView.getMenu();
        MenuItem menuItem= menu.getItem(1);
        menuItem.setChecked(true);

        recyclerView=binding.islemlerRecylerView;


        getReservationsFromFirestore();


    }
    private void getReservationsFromFirestore() {

        // Kullanıcının email adresini al
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Firestore sorgusu yap
        FirebaseFirestore.getInstance()
                .collection("Rezervasyonlar")
                .whereEqualTo("userEmail", currentUserEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Verileri al
                        List<Reservation> reservations = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reservation reservation = document.toObject(Reservation.class);
                            reservations.add(reservation);
                        }

                        reservationAdapter.setReservationList(reservations);
                        // RecyclerView için Adapter oluştur
                        reservationAdapter = new ReservationAdapter(reservations);

                        // RecyclerView'ı ayarla
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        recyclerView.setAdapter(reservationAdapter);
                    } else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                    }
                });
    }
    private void setBottomNavigationView(){

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.homeScreen){
                    Intent intentToHome=new Intent(IslemlerActivity.this, MapsActivity.class);
                    intentToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentToHome);

                } else if (item.getItemId()==R.id.islemlerScreen) {

                } else if (item.getItemId()==R.id.profileScreen) {
                    Intent intentToProfile=new Intent(IslemlerActivity.this,ProfileActivity.class);
                    finish();
                    startActivity(intentToProfile);

                }else{
                    showToast("Beklenmeyen bir hata oluştu");
                }

                return false;
            }
        });
    }
    private void showToast(String message){
        CustomToast.showToast(getApplicationContext(),message);
    }

}