    package com.example.parkingsystem.MapsActivities;

    import androidx.activity.result.ActivityResultCallback;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.annotation.NonNull;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.FragmentActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;
    import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
    import androidx.work.Data;
    import androidx.work.OneTimeWorkRequest;
    import androidx.work.WorkManager;

    import android.Manifest;
    import android.annotation.SuppressLint;
    import android.app.AlertDialog;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.location.Location;
    import android.location.LocationListener;
    import android.location.LocationManager;
    import android.os.Bundle;
    import android.text.InputType;
    import android.util.Log;
    import android.view.Gravity;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.LinearLayout;
    import android.widget.ProgressBar;
    import android.widget.TextView;

    import com.example.parkingsystem.CustomItems.CustomToast;
    import com.example.parkingsystem.CustomItems.DistanceCalculator;
    import com.example.parkingsystem.IslemlerActivity;
    import com.example.parkingsystem.Adapters.OtoparkAdapter;
    import com.example.parkingsystem.Interfaces.OtoparkInfo;
    import com.example.parkingsystem.ProfileActivity;
    import com.example.parkingsystem.R;
    import com.example.parkingsystem.Interfaces.ReservationEndWorker;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.BitmapDescriptorFactory;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.example.parkingsystem.databinding.ActivityMapsBinding;
    import com.google.android.material.bottomnavigation.BottomNavigationView;
    import com.google.android.material.snackbar.Snackbar;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.QueryDocumentSnapshot;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Locale;
    import java.util.Map;
    import java.util.concurrent.Executors;
    import java.util.concurrent.ScheduledExecutorService;
    import java.util.concurrent.TimeUnit;


    public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {



        private GoogleMap mMap;
        private ActivityMapsBinding binding;
        ActivityResultLauncher<String> permissionLauncher;
        LocationListener locationListener;
        LocationManager locationManager;
        SharedPreferences sharedPreferences;
        BottomNavigationView bottomNavigationView;
        FirebaseFirestore firebaseFirestore;
        FirebaseAuth firebaseAuth;
        ArrayList<OtoparkInfo> otoparkInfoArrayList;
        OtoparkAdapter otoparkAdapter;
        private double userLatitude; // Kullanıcının enlem bilgisi
        private double userLongitude; // Kullanıcının boylam bilgisi
        private ArrayList<LatLng> otoparkLatLngList;
        private DistanceCalculator distanceCalculator;
        private SwipeRefreshLayout swipeRefreshLayout;
        private ScheduledExecutorService scheduler;
        ProgressBar progressBar;

        RecyclerView recyclerView;
        TextView emptyTextViewMain;


        boolean info;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            scheduler = Executors.newScheduledThreadPool(1);


            binding = ActivityMapsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            emptyTextViewMain=binding.emptyTextViewMain;




            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            progressBar = findViewById(R.id.progressBar);

            registerLauncher();

            firebaseFirestore=FirebaseFirestore.getInstance();
            firebaseAuth=FirebaseAuth.getInstance();



            otoparkInfoArrayList=new ArrayList<>();
            otoparkLatLngList = new ArrayList<>();
            distanceCalculator=new DistanceCalculator();


            sharedPreferences= MapsActivity.this.getSharedPreferences("com.example.parkingsystem",MODE_PRIVATE);
            info=false;

            bottomNavigationView = (BottomNavigationView) binding.bottomNavigationView;

            setBottomNavigationView();

            getAllParks();
            recyclerView=binding.recyclerView;

            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

            otoparkAdapter=new OtoparkAdapter(this,otoparkInfoArrayList);
            binding.recyclerView.setAdapter(otoparkAdapter);

            swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshData();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });


        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    boolean info = sharedPreferences.getBoolean("info",false);

                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();

                    if(!info){

                        LatLng userLocation = new LatLng(userLatitude,userLongitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                        sharedPreferences.edit().putBoolean("info",true).apply();
                        Log.d("reel", String.valueOf(userLongitude));

                    }
                }
            };
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {


                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                        Snackbar.make(binding.getRoot(),"Permission needed for maps",Snackbar.LENGTH_INDEFINITE).setAction("Yetki Ver", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //izin iste
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

                            }
                        }).show();
                    }else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

                    }
                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(lastLocation!=null){
                        userLatitude = lastLocation.getLatitude();
                        userLongitude = lastLocation.getLongitude();

                        LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));

                    }
                    mMap.setMyLocationEnabled(true);
                }



        }
        public interface EmptyPlaceListener {
            void onEmptyPlaceReceived(int emptyPlace);
        }

        public interface GetNameListener{
            void onGetNameListener(String name);
        }

        private void registerLauncher(){
            permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {

                    if(result){
                        if (ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if(lastLocation!=null){

                                LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                            }
                        }
                    }else{
                        showToast("İzin gerekli!!");
                    }

                }
            });
        }
        public void btnRezerve(View view) {

            int position = recyclerView.getChildLayoutPosition((View) view.getParent());
            String selectedOtoparkAdi = otoparkInfoArrayList.get(position).getOtoparkAdi();
            String saatlikUcret = otoparkInfoArrayList.get(position).getSaatlikUcret();

            getNameOnProfile(new GetNameListener() {
                @Override
                public void onGetNameListener(String name) {
                    if (name.equals("") || name.equals(null)){
                        showToast("Lütfen profilinizi tamamlayın");
                    }else {
                        getCurrentEmptyPlace(selectedOtoparkAdi, new EmptyPlaceListener() {
                            @Override
                            public void onEmptyPlaceReceived(int emptyPlace) {
                                if(emptyPlace==0){
                                    showToast("Otopark şu an maalesef dolu. Dilerseniz biraz bekleyebilir veya alternatifleri deneyebilirsiniz");

                                }else {
                                    showReservationDialog(selectedOtoparkAdi,saatlikUcret,name);
                                }
                            }
                        });

                    }
                }
            });
        }
        private void getCurrentEmptyPlace(String selectedOtoparkAdi, EmptyPlaceListener listener) {
            String collectionPath = "Otoparklar";
            FirebaseFirestore.getInstance()
                    .collection(collectionPath)
                    .whereEqualTo("otoparkAdi", selectedOtoparkAdi)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            // Firestore'dan mevcut bosAracSayisi'ni al
                            String currentBosAracSayisiStr = document.getString("bosArac");
                            if (currentBosAracSayisiStr != null) {
                                int currentBosAracSayisi = Integer.parseInt(currentBosAracSayisiStr);
                                listener.onEmptyPlaceReceived(currentBosAracSayisi);
                            } else {
                                listener.onEmptyPlaceReceived(0);
                            }
                        } else {
                            listener.onEmptyPlaceReceived(0);
                        }
                    });
        }
        private void getNameOnProfile(GetNameListener getNameListener){
            String collectionPath="Profile";
            String email=firebaseAuth.getCurrentUser().getEmail();

            firebaseFirestore.collection(collectionPath)
                    .whereEqualTo("email",email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                            String name = documentSnapshot.getString("name");
                            if(name != null){
                                getNameListener.onGetNameListener(name);

                            }else {
                                getNameListener.onGetNameListener("");
                            }
                        }else {
                            getNameListener.onGetNameListener("");
                        }

                    });

        }
        @SuppressLint("ResourceAsColor")
        private void showReservationDialog(String selectedOtoparkAdi, String saatlikUcret,String name) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Rezervasyon Süresi");

            LinearLayout firstLayout = new LinearLayout(this);
            firstLayout.setOrientation(LinearLayout.VERTICAL);

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            firstLayout.addView(input);

            builder.setView(firstLayout);

            builder.setPositiveButton("Devam", (dialog, which) -> {
                String reservationHoursStr = input.getText().toString().trim();
                if (!reservationHoursStr.isEmpty()){
                    int reservationHours=Integer.parseInt(reservationHoursStr);

                    AlertDialog.Builder secondBuilder= new AlertDialog.Builder(this);
                    LinearLayout secondLayout = new LinearLayout(this);
                    secondLayout.setOrientation(LinearLayout.VERTICAL);

                    final TextView totalCostTextView = new TextView(this);
                    totalCostTextView.setGravity(Gravity.CENTER_VERTICAL);
                    totalCostTextView.setTextColor(getColor(R.color.green));
                    totalCostTextView.setTextSize(20);
                    secondLayout.addView(totalCostTextView);

                    String formattedPrice = saatlikUcret.replaceAll("[^\\d.]+", "");
                    double initialPrice = Double.parseDouble(formattedPrice);
                    double totalPrice = initialPrice * reservationHours;
                    totalCostTextView.setText(String.format(Locale.getDefault(), "Ödeyeceğiniz Ücret: %.2f TL", totalPrice));

                    secondBuilder.setView(secondLayout);

                    secondBuilder.setPositiveButton("Rezerve Et",(secondDialog,secondWhich)->{
                        performReservation(selectedOtoparkAdi,reservationHours,name);
                    });
                    secondBuilder.setNegativeButton("İptal",(secondDialog,secondWhich)->secondDialog.cancel());

                    secondBuilder.show();
                }else {
                    showToast("Geçerli bir saat miktarı giriniz.");
                }
            });

            builder.setNegativeButton("İptal",(dialog,which)->dialog.cancel());

            builder.show();

        }
        private void performReservation(String selectedOtoparkAdi,int reservationHours,String name){
            updateReservation(selectedOtoparkAdi,reservationHours,name);
        }
        private void updateReservation(String selectedOtoparkAdi, int reservationHours,String name) {
            String collectionPath = "Otoparklar";

            FirebaseFirestore.getInstance()
                    .collection(collectionPath)
                    .whereEqualTo("otoparkAdi", selectedOtoparkAdi)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            // Firestore'dan mevcut bosAracSayisi'ni al
                            String currentBosAracSayisiStr = document.getString("bosArac");

                            if (currentBosAracSayisiStr != null) {
                                try {
                                    int currentBosAracSayisi = Integer.parseInt(currentBosAracSayisiStr);
                                    if (currentBosAracSayisi!=0){
                                        // -1 eksilt
                                        int updatedBosAracSayisi = currentBosAracSayisi - 1;
                                        // Firestore'da güncelleme
                                        Map<String, Object> updateData = new HashMap<>();
                                        updateData.put("bosArac", String.valueOf(updatedBosAracSayisi));


                                        document.getReference().update(updateData)
                                                .addOnSuccessListener(aVoid -> {


                                                    long reservationStartTime = System.currentTimeMillis();
                                                    long reservationEndTime = calculateReservationEndTime(reservationStartTime, reservationHours);
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                    String formattedStartTime = simpleDateFormat.format(new Date(reservationStartTime));
                                                    String formattedEndTime = simpleDateFormat.format(new Date(reservationEndTime));
                                                    addReservationToFirestore(selectedOtoparkAdi,formattedEndTime,formattedStartTime,name);

                                                    // Belirtilen süre sonunda ReservationEndWorker'ı başlat
                                                    scheduleReservationEndWork(selectedOtoparkAdi, reservationEndTime - reservationStartTime);
                                                })
                                                .addOnFailureListener(e -> showToast("Rezervasyon güncellenirken hata oluştu."));

                                    }else{
                                        showToast("Otopark şu an maalesef dolu. Dilerseniz biraz bekleyebilir veya alternatifleri deneyebilirsiniz");

                                    }
                                } catch (NumberFormatException e) {
                                    showToast("BosArac değeri sayıya dönüştürülemedi.");
                                }
                            } else {
                                showToast("BosArac değeri bulunamadı veya null.");
                            }
                        } else {
                            showToast("Otopark bilgileri getirilirken hata oluştu.");
                        }
                    });
        }
        private void scheduleReservationEndWork(String selectedOtoparkAdi, long delayMillis) {
            // Belirtilen süre sonunda bir işlemi gerçekleştirmek için WorkManager'ı kullan

            Data inputData = new Data.Builder()
                    .putString("selectedOtoparkAdi", selectedOtoparkAdi)
                    .build();

            // Belirtilen süre sonunda bir işlemi gerçekleştirmek için OneTimeWorkRequest oluştur
            OneTimeWorkRequest reservationEndWorkRequest =
                    new OneTimeWorkRequest.Builder(ReservationEndWorker.class)
                            .setInputData(inputData)
                            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                            .build();

            // WorkManager'a çalışma isteğini gönder
            WorkManager.getInstance(getApplicationContext()).enqueue(reservationEndWorkRequest);
        }

        private long calculateReservationEndTime(long reservationStartTime, int reservationHours) {
            // Rezervasyon süresini saat cinsinden milisaniye cinsine çevir
            long reservationDurationMillis = reservationHours * 60 * 60 * 1000;
            // Rezervasyon başlangıç zamanına süreyi ekleyerek bitiş zamanını hesapla
            return reservationStartTime + reservationDurationMillis;
        }

        private void addReservationToFirestore(String selectedOtoparkAdi, String reservationEndTime, String reservationStartTime,String name) {
            String reservationsCollectionPath = "Rezervasyonlar";
            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            // Yeni rezervasyon verisi oluştur
            Map<String, Object> reservationData = new HashMap<>();
            reservationData.put("userEmail", currentUserEmail);
            reservationData.put("otoparkAdi", selectedOtoparkAdi);
            reservationData.put("endTime", reservationEndTime);
            reservationData.put("startTime",reservationStartTime);
            reservationData.put("name",name);

            // Firestore'a rezervasyonu ekle
            FirebaseFirestore.getInstance()
                    .collection(reservationsCollectionPath)
                    .add(reservationData)
                    .addOnSuccessListener(documentReference -> showToast("Rezervasyon başarıyla alındı"))
                    .addOnFailureListener(e -> showToast("Rezervasyon eklenirken hata oluştu"));
        }
        private void getAllParks(){
            progressBar.setVisibility(View.VISIBLE);
            String collectionPath="Otoparklar";
            firebaseFirestore.collection(collectionPath).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    otoparkInfoArrayList.clear();

                    for(QueryDocumentSnapshot document : task.getResult()){
                        Double latitude=document.getDouble("latitude");
                        Double longitude=document.getDouble("longitude");
                        String otoparkAdi=document.getString("otoparkAdi");
                        String aracSayisi=document.getString("aracSayisi");
                        String elektrikliAraclar=document.getString("elektrikliAraclar");
                        String saatlikUcret = document.getString("saatlikUcret");
                        String bosAracSayisi=document.getString("bosArac");

                        if (latitude != null && longitude != null && otoparkAdi != null) {
                            LatLng latLng = new LatLng(latitude, longitude);
                            addMarkerToMap(latLng, otoparkAdi);

                        }
                        listParks(otoparkAdi,aracSayisi,elektrikliAraclar,saatlikUcret,latitude,longitude,bosAracSayisi);
                    }
                    otoparkAdapter.sortOtoparkInfoByDistance();

                    getOtoparkLocations();

                    otoparkAdapter.notifyDataSetChanged();

                    if (otoparkAdapter.getItemCount() == 0) {
                        // Otopark yoksa mesajı göster
                        showNoParkingMessage();
                    }
                    checkEmptyView();
                }else {
                    showToast("Otoparklar getirilirken bir hata oluştu. Tekrar deneyiniz");
                }
                progressBar.setVisibility(View.GONE);
            });

        }
        private void showNoParkingMessage() {
            emptyTextViewMain.setVisibility(View.VISIBLE);
            emptyTextViewMain.setText("Üzgünüz, şu an bulunduğunuz konumda uygun otopark bulunmamaktadır. Lütfen farklı bir bölgeye giderek veya daha sonra tekrar deneyerek otoparkları görmeye çalışın.");
            binding.recyclerView.setVisibility(View.INVISIBLE);
        }
        private void calculateDistances() {
            for (LatLng otoparkLatLng : otoparkLatLngList) {
                calculateDistance(userLatitude, userLongitude, otoparkLatLng.latitude, otoparkLatLng.longitude);
            }
        }
        private void calculateDistance(double userLatitude, double userLongitude, double otoparkLatitude, double otoparkLongitude) {
            distanceCalculator.calculateDistance(userLatitude, userLongitude, otoparkLatitude, otoparkLongitude);
        }
        private void getOtoparkLocations() {
            String collectionPath = "Otoparklar";
            otoparkLatLngList.clear(); // Liste temizlendi
            firebaseFirestore.collection(collectionPath).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Double latitude = document.getDouble("latitude");
                        Double longitude = document.getDouble("longitude");

                        if (latitude != null && longitude != null) {
                            LatLng latLng = new LatLng(latitude, longitude);
                            otoparkLatLngList.add(latLng);

                        }
                    }
                    calculateDistances();
                } else {
                    showToast("Otoparklar getirilirken bir hata oluştu. Tekrar deneyiniz");
                }
            });
        }
        private void addMarkerToMap(LatLng latLng, String otoparkAdi) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(otoparkAdi)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));


            mMap.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        }
        private void showToast(String message){
            CustomToast.showToast(getApplicationContext(),message);
        }

        private void listParks(String otoparkAdi,String aracSayisi,String elektrikliAraclar,String saatlikUcret,Double otoparkLatitude, Double otoparkLongitude,String bosAracSayisi){
            String distanceToKm = distanceCalculator.calculateDistance(userLatitude, userLongitude, otoparkLatitude, otoparkLongitude);

            // Virgülü noktaya çevir ve " km" kısmını kaldır
            String distanceValueStr = distanceToKm.replace(",", ".").replace(" km", "");

            try {
                // Parse edilen değeri al
                double distanceValue = Double.parseDouble(distanceValueStr);

                // Mesafeyi kontrol et ve 100 km'den küçükse listeye ekle
                if (distanceValue <= 100) {
                    OtoparkInfo otoparkInfo = new OtoparkInfo(otoparkAdi, aracSayisi, elektrikliAraclar, saatlikUcret,distanceValueStr,bosAracSayisi);
                    otoparkInfoArrayList.add(otoparkInfo);
                } else {
                }
            } catch (NumberFormatException e) {

                Log.e("NumberFormatException", "Double'a çevrilemeyen değer: " + distanceValueStr);
            }


        }
        private void refreshData() {
            getAllParks(); // Verileri yenile
            otoparkAdapter.notifyDataSetChanged(); // Adapter'a değişiklikleri bildir
        }
        public void checkEmptyView() {
            if (otoparkAdapter == null || otoparkAdapter.getItemCount() == 0) {
                binding.recyclerView.setVisibility(View.INVISIBLE);

                // RecyclerView boşken gösterilecek TextView'yi görünür yapın
                if (emptyTextViewMain != null) {
                    emptyTextViewMain.setVisibility(View.VISIBLE);
                }
            } else {
                binding.recyclerView.setVisibility(View.VISIBLE);

                // RecyclerView doluysa gösterilecek TextView'yi gizleyin
                if (emptyTextViewMain != null) {
                    emptyTextViewMain.setVisibility(View.GONE);
                }
            }
        }
        private void setBottomNavigationView(){

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId()==R.id.homeScreen){


                    } else if (item.getItemId()==R.id.islemlerScreen) {
                        Intent intentToIslemler=new Intent(MapsActivity.this, IslemlerActivity.class);
                        startActivity(intentToIslemler);

                    } else if (item.getItemId()==R.id.profileScreen) {
                        Intent intentToProfile=new Intent(MapsActivity.this, ProfileActivity.class);
                        startActivity(intentToProfile);

                    }else{
                        showToast("Beklenmeyen bir hata oluştu");
                    }

                    return false;
                }
            });
        }

        }
