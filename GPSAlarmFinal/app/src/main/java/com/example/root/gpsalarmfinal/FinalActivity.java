package com.example.root.gpsalarmfinal;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class FinalActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,com.google.android.gms.location.LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int INTERVAL = 1000 * 10;
    private static final int FASTEST_INTERVAL = 1000 * 5;
    private static final String TAG = "LocationActivity2";
    Location start,current,destination;
    Float distance;
    TextView distanceText;
    MediaPlayer mediaPlayer;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    float flag,radius;
    int cancel = 0;
    private FusedLocationProviderApi fusedLocationProviderApi;

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
        radius = (float)intent.getIntExtra("Radius",500);
        cancel = intent.getIntExtra("Cancel",0);
        Log.d("info","Received "+radius);
        flag = radius;
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
        Log.d("info","Cancel: "+cancel);
        if(distance<flag && cancel==0){
            mediaPlayer.start();
            createNotif();
        }
    }

    public void createNotif(){
        Intent intent1 = new Intent(this,FinalActivity.class);
        intent1.putExtra("Start",start);
        intent1.putExtra("Destination",destination);
        intent1.putExtra("Radius",radius);
        intent1.putExtra("Cancel",1);
        Intent intent2 = new Intent(this,FinalActivity.class);
        intent2.putExtra("Start",start);
        intent2.putExtra("Destination",destination);
        intent2.putExtra("Radius",radius);
        intent2.putExtra("Cancel",0);
        PendingIntent pIntent1 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent1, 0);
        PendingIntent pIntent2 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent2, 0);
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Destination Reached")
                .setContentText("Subject").setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent2)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_cancel, "Stop", pIntent1).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

}
