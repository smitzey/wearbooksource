package io.wearbook.wmap ;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location ;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class WMapHandheldActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap googleMap;

    private GoogleApiClient googleApiClient ;

    private Location locationLastKnown;

    private boolean resolvingGoogleApiClientError;

    private static final int REQUEST_RESOLVE_ERROR = 999;
    private static final String DIALOG_ERROR = "DIALOG_ERROR";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wmap_handheld);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        verifyPermissions () ;


        initGoogleApiClient();


    }

    private void verifyPermissions () {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d ( TAG, "verifyPermissions()Manifest.permission.ACCESS_FINE_LOCATION not granted") ;

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // @TODO show explanation async

            } else {
                Log.d ( TAG, "verifyPermissions() requesting permissions") ;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_REQUEST_LOCATION_PERMISSIONS);

            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!resolvingGoogleApiClientError) {
            googleApiClient.connect();
            Log.d ( TAG, "onStart()... attempted to connect googleApiClient googleApiClient.connect()") ;
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    protected synchronized void initGoogleApiClient () {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


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
        this.googleMap = googleMap;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        refreshPositionOnMap();




    }

    private void refreshPositionOnMap () {

        Log.d ( TAG, "refreshPositionOnMap()... lastKnownLocation=" + locationLastKnown) ;

        if ( this.googleMap == null ) {
            return ;
        }

        if (locationLastKnown != null) {
            LatLng latLng = new LatLng(locationLastKnown.getLatitude(), locationLastKnown.getLongitude());
            this.googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            CameraUpdateFactory.zoomTo(18.0f) ;
        }


    }




    @Override
    public void onConnected(Bundle bundle) {

        Log.d(TAG, "onConnected()..." ) ;
        locationLastKnown = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if ( locationLastKnown == null ) {
            LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient) ;
            if( locationLastKnown == null ){
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        locationLastKnown = location;

                        Log.d(TAG, "LocationServices.FusedLocationApi.requestLocationUpdate)/onLocationChanged()...locationLastKnown=" + locationLastKnown ) ;

                        refreshPositionOnMap();

                    }
                });
            }


            boolean locationAvailabilityFlag= false ;

            if ( locationAvailability != null ) {
                locationAvailabilityFlag = locationAvailability.isLocationAvailable() ;
            }

            Log.d(TAG, "onConnected()...locationAvailability=" + locationAvailability  ) ;
        }

        Log.d(TAG, "onConnected()...locationLastKnown=" + locationLastKnown ) ;

        refreshPositionOnMap();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended()..." ) ;

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() connectionResult= "  + connectionResult) ;

        if (resolvingGoogleApiClientError) {
             return;
        } else if (connectionResult.hasResolution()) {
            try {
                resolvingGoogleApiClientError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                googleApiClient.connect();
            }
        } else {

            showErrorDialog(connectionResult.getErrorCode());
            resolvingGoogleApiClientError = true;
        }



    }

    private void showErrorDialog(int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
               Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    public void onDialogDismissed() {
        resolvingGoogleApiClientError = false;
    }

    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((WMapHandheldActivity) getActivity()).onDialogDismissed();
        }
    }


    private  LocationRequest createLocationRequest() {

        LocationRequest retVal = new LocationRequest();
        retVal.setInterval( MILLIS_LOCATION_INTERVAL);
        retVal.setFastestInterval( MILLIS_LOCATION_FASTEST_INTERVAL);
        retVal.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        return retVal ;
    }

    private static final long MILLIS_LOCATION_INTERVAL = 18000;
    private static final long MILLIS_LOCATION_FASTEST_INTERVAL = 6000;
    private static final int MY_REQUEST_LOCATION_PERMISSIONS = 99;




    private static final String TAG = WMapHandheldActivity.class.getName() ;
}
