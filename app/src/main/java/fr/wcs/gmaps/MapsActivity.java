package fr.wcs.gmaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private  LocationManager mLocationManager = null;

    private Location mLocation;
    private Marker nousMarker;
    private Marker mMarker;
    private static final int ACCESS_FINE_REQUEST = 100;

    private boolean isMapReady;
    private boolean isLocationReady;
    private boolean hasAnimated;
    private LatLngBounds BORDEAUX;
    //private LatLng BORDEAUX_CENTER = new LatLng(44.837789,-0.57918);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //FIRST SOUTHWEST THEN NORTHEAST
       BORDEAUX = new LatLngBounds(
                new LatLng(44.813, -0.617), new LatLng(44.87, -0.5394));
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
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(15.0f);
        mMap.setLatLngBoundsForCameraTarget(BORDEAUX);

        if (isLocationReady) placeSelfMarker();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                placeMarker(latLng);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q="+ marker.getPosition().toString()));
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) !=null){
                    startActivity(mapIntent);
                }
                return false;
            }
        });
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



                mMap.setMyLocationEnabled(true);


             if (isMapReady) placeSelfMarker();
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


    public void placeSelfMarker(){


        LatLng nous = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

        if(!hasAnimated) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nous, 12.0f));
            hasAnimated = true;
        }


//        if (nousMarker != null){
//            nousMarker.remove();
//        }
//
//        //marche pas a recheflir
//        else {
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nous, 12.0f));
//        }


        //nousMarker = mMap.addMarker(new MarkerOptions().position(nous).title(mLocation.toString()));

//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(nous));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nous, 12.0f));

            // //Move the camera to the user's location and zoom in!
        //



    }

    public Marker placeMarker(LatLng pLatLnp){

        if(mMarker != null){
            mMarker.remove();
        }
        mMarker = mMap.addMarker(new MarkerOptions().position(pLatLnp));

        return mMarker;


    }




}
