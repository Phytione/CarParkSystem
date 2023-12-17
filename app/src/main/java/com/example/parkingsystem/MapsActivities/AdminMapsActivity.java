package com.example.parkingsystem.MapsActivities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.parkingsystem.AdminProfileActivity;
import com.example.parkingsystem.Interfaces.AdminReservation;
import com.example.parkingsystem.Adapters.AdminReservationAdapter;
import com.example.parkingsystem.CustomItems.CustomInfoWindowFragment;
import com.example.parkingsystem.CustomItems.CustomToast;
import com.example.parkingsystem.MainActivity;
import com.example.parkingsystem.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.parkingsystem.databinding.ActivityAdminMapsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminMapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener,GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityAdminMapsBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    LocationListener locationListener;
    LocationManager locationManager;
    SharedPreferences sharedPreferences;
    ActivityResultLauncher<String> permissionLauncher;
    ArrayList<AdminReservation> adminReservationArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LatLng latLng;
    boolean info;
    private CustomInfoWindowFragment customInfoWindowFragment;
    AdminReservationAdapter adminReservationAdapter;
    RecyclerView recyclerView2;
    TextView txtToplamRandevu;
    TextView txtOtoparklarim;
    BottomNavigationView bottomNavigationViewAdmin;
    private TextView emptyTextView;
    private static final long WAIT_TIME = 500; // 0.5 saniye
    private long lastClickTime = 0;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        registerLauncher();
        fetchOtoparkData();
        adminReservationArrayList=new ArrayList<>();
        adminReservationAdapter=new AdminReservationAdapter(this,adminReservationArrayList);
        binding.recyclerView2.setAdapter(adminReservationAdapter);
        txtToplamRandevu=binding.txtToplamRandevu;
        txtToplamRandevu.setVisibility(View.INVISIBLE);
        txtOtoparklarim=binding.txtOtoparklarim;
        txtOtoparklarim.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progressBarAdmin);

        bottomNavigationViewAdmin = (BottomNavigationView) binding.bottomNavigationViewAdmin;

        setBottomNavigationViewAdmin();

        emptyTextView = binding.emptyTextView;
        checkEmptyView();






    }
    public void checkEmptyView() {
        if (adminReservationAdapter.getItemCount() == 0) {
            binding.recyclerView2.setVisibility(View.INVISIBLE);
            txtToplamRandevu.setVisibility(View.INVISIBLE);
            txtOtoparklarim.setVisibility(View.INVISIBLE);

            // RecyclerView boşken gösterilecek TextView'yi görünür yapın
            if (emptyTextView != null) {
                emptyTextView.setVisibility(View.VISIBLE);
            }
        } else {
            binding.recyclerView2.setVisibility(View.VISIBLE);
            txtToplamRandevu.setVisibility(View.VISIBLE);
            txtOtoparklarim.setVisibility(View.VISIBLE);

            // RecyclerView doluysa gösterilecek TextView'yi gizleyin
            if (emptyTextView != null) {
                emptyTextView.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(binding.getRoot(), "Permission needed for maps", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //izin iste
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

                    }
                }).show();
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastLocation != null) {

                LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));

            }
            mMap.setMyLocationEnabled(true);
        }
    }

    private void registerLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if (result) {
                    if (ContextCompat.checkSelfPermission(AdminMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (lastLocation != null) {

                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                        }
                    }
                } else {
                    showToast("Permission needed");
                }

            }
        });
    }




    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
        showCustomInfoWindow(latLng.latitude, latLng.longitude);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String otoparkAdi = marker.getTitle();
        // Otoparka tıklandığında rezervasyonları getir ve göster
        getOtoparkReservations(otoparkAdi);

        return true;
    }

    private void getOtoparkReservations(String otoparkAdi) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseFirestore.collection("Rezervasyonlar")
                .whereEqualTo("otoparkAdi", otoparkAdi)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Rezervasyonları al ve RecyclerView'e set et
                        List<AdminReservation> reservationList = new ArrayList<>();
                        final int[] activeReservationCount = {0};
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userEmail = document.getString("userEmail");
                            String startTime = document.getString("startTime");
                            String endTime = document.getString("endTime");

                            if (userEmail != null && startTime != null && endTime != null) {
                                // getEmailName metodu ile kullanıcı ismini al
                                getEmailName(userEmail, new OnNameFetchedListener() {
                                    @Override
                                    public void onNameFetched(String userName) {
                                        if (userName != null) {
                                            reservationList.add(new AdminReservation(userName, startTime, endTime));

                                            // RecyclerView için LinearLayoutManager ekleyin
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(AdminMapsActivity.this);
                                            binding.recyclerView2.setLayoutManager(layoutManager);

                                            // RecyclerView'e rezervasyonları set etmek için adapter'ı kullanın
                                            adminReservationAdapter.setAdminReservationList(reservationList);
                                            adminReservationAdapter.notifyDataSetChanged();

                                            // Alt menüyü göster
                                            showReservationMenu();
                                            txtOtoparklarim.setVisibility(View.VISIBLE);
                                            txtOtoparklarim.setText(otoparkAdi);
                                            txtToplamRandevu.setVisibility(View.VISIBLE);
                                            txtToplamRandevu.setText("Toplam Rezerve Sayısı: " + String.valueOf(reservationList.size()));
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        showToast("Rezervasyonlar getirilirken bir hata oluştu. Tekrar deneyiniz");
                    }
                    progressBar.setVisibility(View.GONE);
                });

    }

    private void showReservationMenu() {
        if (binding.recyclerView2.getVisibility() != View.VISIBLE) {
            binding.recyclerView2.setVisibility(View.VISIBLE);
        }
    }

    private void showCustomInfoWindow(double latitude, double longitude) {


        CustomInfoWindowFragment customInfoWindowFragment = new CustomInfoWindowFragment();
        Bundle args = new Bundle();
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
        customInfoWindowFragment.setArguments(args);
        customInfoWindowFragment.show(getFragmentManager(), "customInfoWindow");

    }

    private void addMarkerToFirebase(LatLng latLng) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String phoneNumber = currentUser.getPhoneNumber();
            if (phoneNumber != null) {
                String collectionPath = "Otoparklar";
                String documentPath = phoneNumber;
                firebaseFirestore.collection(collectionPath).document(documentPath).set(new GeoPoint(latLng.latitude, latLng.longitude)).addOnSuccessListener(aVoid -> {


                }).addOnFailureListener(e -> {
                    showToast("Hata ile karşılaşıldı");
                });

            }
        }
    }

    private void showToast(String message) {
        CustomToast.showToast(getApplicationContext(), message);
    }

    public void onKaydetButtonClick(LatLng latLng) {
        // Firebase'e marker bilgilerini ekleyin
        addMarkerToFirebase(latLng);

        // CustomInfoWindowFragment'ı kapatın
        customInfoWindowFragment.dismiss();
    }

    private void fetchOtoparkData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userNumber = currentUser.getPhoneNumber();
            if (userNumber != null) {
                String collection = "Otoparklar";
                firebaseFirestore.collection(collection).whereEqualTo("userNumber", userNumber).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Double latitude = document.getDouble("latitude");
                            Double longitude = document.getDouble("longitude");
                            String otoparkAdi = document.getString("otoparkAdi");

                            if (latitude != null && longitude != null && otoparkAdi != null) {
                                LatLng latLng = new LatLng(latitude, longitude);
                                addMarkerToMap(latLng, otoparkAdi);

                            }
                        }
                    } else {
                        showToast("Otoparklar getirilirken bir hata oluştu. Tekrar deneyiniz");

                    }
                });
            }
        }
    }
    private void addMarkerToMap(LatLng latLng, String otoparkAdi) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(otoparkAdi);
        mMap.addMarker(markerOptions);

    }
    private void getEmailName(String userEmail, OnNameFetchedListener listener) {
        // Firebase Firestore referansını al
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        // "Profile" koleksiyonundan kullanıcının ismini al
        firebaseFirestore.collection("Profile")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Kullanıcının ismini al
                            String userName = document.getString("name");

                            // Listener'a ismi iletilir
                            if (listener != null) {
                                listener.onNameFetched(userName);
                            }
                        }
                    } else {
                        // Hata durumunda listener'a null değer iletilir
                        if (listener != null) {
                            listener.onNameFetched(null);
                        }
                    }
                });
    }

    public interface OnNameFetchedListener {
        void onNameFetched(String userName);
    }

    private void setBottomNavigationViewAdmin() {
        bottomNavigationViewAdmin.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Bu metot, bir menü öğesi tıklandığında çağrılır

                if (item.getItemId() == R.id.homeScreenAdmin) {
                    // Ana sayfaya git
                } else if (item.getItemId() == R.id.profileScreenAdmin) {
                    // Profil sayfasına git
                    Intent intentToAdminProfile = new Intent(AdminMapsActivity.this, AdminProfileActivity.class);
                    startActivity(intentToAdminProfile);
                } else {
                    showToast("Beklenmeyen bir hata oluştu");
                }

                return true; // true döndürerek seçimi işlemeyi bitirir
            }
        });
    }




}
