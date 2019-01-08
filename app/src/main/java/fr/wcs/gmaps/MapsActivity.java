package fr.wcs.gmaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private  LocationManager mLocationManager = null;

    private Location mLocation;
    private Marker nousMarker;
    private static final int ACCESS_FINE_REQUEST = 100;

    private boolean isMapReady;
    private boolean isLocationReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkPermission();




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


        isMapReady = true;

        if (isLocationReady) placeMarker();
        // Add a marker in Sydney and move the camera
//        LatLng nous = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(nous).title("Marker here"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(nous));



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (ACCESS_FINE_REQUEST == requestCode) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                initLocation();
            } else {
                finish();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }

    }



    private void checkPermission(){
        // vérification de l'autorisation d'accéder à la position GPS

        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // l'autorisation n'est pas acceptée

            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // l'autorisation a été refusée précédemment, on peut prévenir l'utilisateur ici

            } else {

                // l'autorisation n'a jamais été réclamée, on la demande à l'utilisateur

                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_REQUEST);
            }
        } else {

            // TODO : autorisation déjà acceptée, on peut faire une action ici

            initLocation();


        }
    }


    @SuppressLint("MissingPermission")
    public void initLocation(){

        //Création de l'instance de la classe LocationManager, dans la référence mLocationManager.
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // TODO : effectuer une action ici !

                mLocation = location;
                isLocationReady = true;

             if (isMapReady) placeMarker();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, locationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, locationListener);

    }


    public void placeMarker(){


        LatLng nous = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());


        if (nousMarker != null){
            nousMarker.remove();
        }

        //marche pas a recheflir
        else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nous, 12.0f));
        }

        nousMarker = mMap.addMarker(new MarkerOptions().position(nous).title(mLocation.toString()));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(nous));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nous, 12.0f));
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(15.0f);
            // //Move the camera to the user's location and zoom in!
        //



    }




}
