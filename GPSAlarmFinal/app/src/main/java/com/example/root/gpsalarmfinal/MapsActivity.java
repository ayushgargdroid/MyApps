package com.example.root.gpsalarmfinal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,com.google.android.gms.location.LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener  {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final String TAG = "LocationActivity1";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    GoogleApiClient m2GoogleApiClient;
    Location mCurrentLocation,destination = new Location("Destination");
    Marker destinationMarker,currentMarker;
    boolean onMapsReady = false;
    Button button;
    int radius;
    private GoogleMap mMap;
    private FusedLocationProviderApi fusedLocationProviderApi;

    public void createLocation(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d(TAG, "onCreate ...............................");
        Intent intent = getIntent();
        radius = intent.getIntExtra("radius",500);
        createLocation();
        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        m2GoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.i("info","onMapsReady");
        // Add a marker in Sydney and move the camera
        onMapsReady = true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        Log.i("Location Lat",Double.toString(mCurrentLocation.getLatitude()));
        Log.i("Location Lng",Double.toString(mCurrentLocation.getLongitude()));
        setCurrentLocation();
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public void stopLocationUpdates(){
        fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            Log.d("Error", "GPS not Connected");
            Toast.makeText(this, "GPS is not enabled. Opening Location Settings", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        }
        startLocationUpdates();
    }

    public void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void setCurrentLocation(){
        if(onMapsReady){
            if(currentMarker!=null)
                currentMarker.remove();
            LatLng current = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            currentMarker = mMap.addMarker(new MarkerOptions().position(current).title("You"));
            //mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current,15f));
            onChoose();
        }
    }
    public void onChoose(){
        Log.i("Position","chooseLoc");
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setText("Enter Destination");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("Found", "Place: " + place.getName());
                LatLng selectedLocation = place.getLatLng();
                Log.i("info","Place received");
                if(destinationMarker!=null)
                    destinationMarker.remove();
                destinationMarker = mMap.addMarker(new MarkerOptions().position(selectedLocation).title(place.getName().toString()));
                //mMap.addMarker(new MarkerOptions().position(selectedLocation).title(place.getName().toString()));
                Log.i("info","Marker Set");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation,15f));
                Log.i("info","Marker Zooming");
                destination.setLatitude(selectedLocation.latitude);
                destination.setLongitude(selectedLocation.longitude);
                Log.i("info","exiting onPlaceSelected");
                button = (Button)findViewById(R.id.startButton);
                button.setAlpha(1f);
                Log.i("info","exiting onPlaceSelected");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("info","Going WonderLand!");
                        Intent intent = new Intent(getApplicationContext(),FinalActivity.class);
                        intent.putExtra("Start",mCurrentLocation);
                        intent.putExtra("Destination",destination);
                        intent.putExtra("Radius",radius);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(Status status) {
                Log.i("Found error",status.getStatusMessage());
                Log.i("Found","Couldn't find your location");
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }
}
