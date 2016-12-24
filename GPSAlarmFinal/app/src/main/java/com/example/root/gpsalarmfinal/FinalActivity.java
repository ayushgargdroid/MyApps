package com.example.root.gpsalarmfinal;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class FinalActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,com.google.android.gms.location.LocationListener {

    Location start,current,destination;
    Float distance;
    TextView distanceText;
    MediaPlayer mediaPlayer;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    float flag;
    private static final int INTERVAL = 1000*10;
    private static final int FASTEST_INTERVAL = 1000*5;
    private static final String TAG = "LocationActivity2";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private FusedLocationProviderApi fusedLocationProviderApi;
    EditText editText;

    public void setmLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        setmLocationRequest();

        mediaPlayer = MediaPlayer.create(this,R.raw.airhorn);

        Intent intent = getIntent();
        start = intent.getParcelableExtra("Start");
        destination = intent.getParcelableExtra("Destination");
        distance = start.distanceTo(destination);
        distanceText = (TextView)findViewById(R.id.distanceTextView);
        distanceText.setText(Float.toString(distance));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        Log.i("info","Exiting onCreate......");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
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
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public void stopLocationUpdates(){
        fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        current = location;
        Log.i("Location Lat",Double.toString(current.getLatitude()));
        Log.i("Location Lng",Double.toString(current.getLongitude()));
        distance = current.distanceTo(destination);
        distanceText.setText(Float.toString(distance));
        if(distance<flag){
            mediaPlayer.start();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    public void startClock(View view){
        editText = (EditText)findViewById(R.id.flag);
        flag = Float.parseFloat(editText.getText().toString());
        LinearLayout a = (LinearLayout)findViewById(R.id.lin1);
        a.setAlpha(0f);
        a = (LinearLayout)findViewById(R.id.lin2);
        a.setAlpha(1f);
    }
}
