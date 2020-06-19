package com.example.thortracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_PERMISIION = 10101;
    private static final int ERROR_CODE = 1111;
    boolean mLocationPermisisionGranter;

    LatLng SearchPlacesLatLong;

    MapView mapView;
    GoogleMap gMap;

    FusedLocationProviderClient mLocationClient;
    LocationCallback mLocationCallback;
    ImageView current;
    private EditText inputSearch;
    private ImageView btnSearch;
    Geocoder geocoder;
    Marker searchMm,myLocationMm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        current = findViewById(R.id.current);
        initMap();
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                } else {
                    Location location = locationResult.getLastLocation();
                    Toast.makeText(MainActivity.this, location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                }
            }
        };
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
        inputSearch = findViewById(R.id.inputSearch);
        btnSearch = findViewById(R.id.ImageSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String address = inputSearch.getText().toString();

                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocationName(address, 1);
                    if (addressList.size() > 0) {
                        Address address1 = addressList.get(0);
                        GotoSearchLocation(address1.getLatitude(), address1.getLongitude());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        if (SearchPlacesLatLong != null) {
            // Toast.makeText(this, SearchPlacesLatLong.latitude, Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            mLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        GotoMyLocation(location.getLatitude(), location.getLongitude());

                    } else {
                        Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void GotoSearchLocation(final double lat, double lng) {
        if (searchMm != null) {
            searchMm.remove();
        }
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f);
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(latLng);
        markerOptions1.title("Your Selected Place");
        markerOptions1.draggable(true);

        searchMm = gMap.addMarker(markerOptions1);
        gMap.animateCamera(cameraUpdate);

        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (searchMm != null) {
                    searchMm.remove();
                }
                LatLng latLng = marker.getPosition();
                try {
                    List<Address> newListAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                    if (newListAddress.size() > 0) {
                        Address address = newListAddress.get(0);
                        MarkerOptions markerOptions2 = new MarkerOptions();
                        markerOptions2.position(latLng);
                        markerOptions2.title("Your Selected Place");
                        markerOptions2.draggable(true);
                        searchMm = gMap.addMarker(markerOptions2);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private void GotoMyLocation(final double lat, double lng) {
        if (myLocationMm != null) {
            myLocationMm.remove();
        }
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate1 = CameraUpdateFactory.newLatLngZoom(latLng, 10f);
        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(latLng);
        markerOptions3.title("I am Here");
        markerOptions3.draggable(false);
        myLocationMm = gMap.addMarker(markerOptions3);
        gMap.animateCamera(cameraUpdate1);
    }

    private void getLocationUpdate() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder().addLocationRequest()
    }
    private void initMap() {
        if (isSericesOK()) {
            if (PermissionOK()) {
                Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
            } else {
                RequestPermission();
            }
        }
    }

    private boolean PermissionOK() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isSericesOK() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int result = apiAvailability.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Google Play Services OK", Toast.LENGTH_SHORT).show();
            return true;
        } else if (apiAvailability.isUserResolvableError(result)) {
            Dialog dialog = apiAvailability.getErrorDialog(MainActivity.this, result, ERROR_CODE, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(MainActivity.this, "Dialog is Cancelled By User", Toast.LENGTH_SHORT).show();

                }

            });
            dialog.show();
        } else {
            Toast.makeText(this, "Google Play Service is required for Google maps", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void RequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISIION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISIION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            mLocationPermisisionGranter = true;
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}