package com.example.e_commerce_app.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.e_commerce_app.R;
import com.example.e_commerce_app.databinding.ActivityMapsBinding;
import com.example.e_commerce_app.models.CategoryModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
       firestore = FirebaseFirestore.getInstance();
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        firestore.collection("StoreLocations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("task.results", "onComplete: "+task.getResult().size());
                        if (task.isSuccessful()) {
                            double centerLatitude = 0.0;
                            double centerLongtitude = 0.0;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                double latitude = document.getGeoPoint("location").getLatitude();
                                double longtitude = document.getGeoPoint("location").getLongitude();
                                centerLatitude += latitude;
                                centerLongtitude += longtitude;
                                String name = document.get("name").toString();
                                LatLng store = new LatLng(latitude,longtitude);
                                mMap.addMarker(new MarkerOptions().position(store).title(name));

                            }
                            centerLatitude /= task.getResult().size();
                            centerLongtitude /= task.getResult().size();
                            LatLng center = new LatLng(centerLatitude,centerLongtitude);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,12));

                        } else {
                            Toast.makeText(MapsActivity.this, "map failed" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });





    }
}