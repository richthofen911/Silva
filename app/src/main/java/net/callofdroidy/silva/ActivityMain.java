package net.callofdroidy.silva;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class ActivityMain extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    public static final String TAG = "ActivityMain";

    GoogleApiClient mGoogleApiClient;

    LocationManager locationManager;
    LocationListener locationListener;

    TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = (TextView) findViewById(R.id.tv_location);

        buildGoogleApiClient();
        if(!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                String locationToDisplay = "\nLat: " + lat + "::Lng: " + lng;
                Log.e(TAG, "onLocationChanged: " + locationToDisplay);
                tvLocation.setText(locationToDisplay);
            }
        };
    }

    private void setAlarm(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    }

    private void requestLastLocation(){
        if(PermissionHandler.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(lastKnownLocation != null)
                Log.e(TAG, "onCreate: lastLocation: " + lastKnownLocation.getLatitude() + " " + lastKnownLocation.getLongitude());
            else
                Log.e(TAG, "onCreate: lastLocation: is null");
        }
        else {
            PermissionHandler.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, Constants.PERMISSION_REQUEST_CODE_FINE_LOCATION);
        }
    }

    private void requestLocationUpdate(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);

        if(PermissionHandler.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);
        }
        else {
            PermissionHandler.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, Constants.PERMISSION_REQUEST_CODE_FINE_LOCATION);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.e("GoogleApiClient", "connected");
        requestLastLocation();
        requestLocationUpdate();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.e(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_CODE_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(PermissionHandler.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
                        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50f, locationListener);
                        requestLastLocation();
                } else {
                    Toast.makeText(this, "no permission to run this app", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }
}
