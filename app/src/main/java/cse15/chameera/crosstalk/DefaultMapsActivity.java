package cse15.chameera.crosstalk;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class DefaultMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private double radius;
    private boolean userFound = false;

    private ArrayList<String> nearbyUsers;
    private ArrayList<Marker> nearbyMarkers;
    private HashMap<String, Marker> nearbyHashMap;

    private Button mLogout, mLookupUsers;

    private String currentUserID, currentUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //set default radius value 0.6km
        this.radius = 0.6;

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Log.i("Current user ID", currentUserID);


        //Logout Button
        mLogout = (Button) findViewById(R.id.Logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =
                        new Intent(DefaultMapsActivity.this, MainActivity.class);
                startActivity(intent);

                removeUserAvailability();

                finish();
                FirebaseAuth.getInstance().signOut();

                return;
            }
        });

        //Find Nearby Users Button
        mLookupUsers = (Button) findViewById(R.id.LookupUsers);
        mLookupUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearbyUsers = new ArrayList<>();
                nearbyMarkers = new ArrayList<>();
                getNearbyUsers();
                // markNearbyUsers();
            }
        });
    }


    // Find nearby users


    private void getNearbyUsers() {

        if (nearbyHashMap != null) {
            Iterator iterator = nearbyHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Marker> pair = (Map.Entry<String, Marker>) iterator.next();
                pair.getValue().remove();
            }

            nearbyHashMap.clear();

        } else
            nearbyHashMap = new HashMap<>();


        DatabaseReference userLocation = FirebaseDatabase.
                getInstance().getReference().child("UserAvailable");

        GeoFire geoFire = new GeoFire(userLocation);

        //String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        GeoQuery geoQuery =
                geoFire.queryAtLocation(
                        new GeoLocation(
                                mLastLocation.getLatitude(), mLastLocation.getLongitude()), radius);


        //Query Listener

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                userFound = true;

                if (!Objects.equals(key, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    nearbyUsers.add(key);

                    Log.i("currentUser", "Current user is " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Log.i("onKeyEntered: ", "UserID found" + key + " array size is" + nearbyUsers.size());


                    Marker m = mMap.addMarker(new MarkerOptions().
                            position(new LatLng(location.latitude, location.longitude)).
                            title(key));
                    m.setTag(key);


                    nearbyHashMap.put(key, m);

                }
                //markNearbyUsers();


            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!userFound) {
                    radius += 0.1;
                    getNearbyUsers();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }


    int i;

    //Adding markers to nearby users
    private void markNearbyUsers() {
        Log.i("markNearbyUsers: ", "nearbyUsers size" + nearbyUsers.size());
        for (i = 0; i < nearbyUsers.size(); i++) {
            Log.i("markNearbyUsers: ", "Value of i is: " + i);

            DatabaseReference userLocationRef = FirebaseDatabase.getInstance().getReference().
                    child("UserAvailable").child(nearbyUsers.get(i)).child("l");

            userLocationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<Object> map = (List<Object>) dataSnapshot.getValue();
                        double otherUserLat = 0;
                        double otherUserLng = 0;

                        if (map.get(0) != null && map.get(1) != null) {
                            otherUserLat = Double.parseDouble(map.get(0).toString());
                            otherUserLng = Double.parseDouble(map.get(1).toString());
                        }

                        LatLng otherUserLocation = new LatLng(otherUserLat, otherUserLng);

                        if (nearbyMarkers.size() > i) {
                            for (int j = 0; j < nearbyMarkers.size(); j++) {
                                nearbyMarkers.get(j).remove();
                            }
                            nearbyMarkers.clear();
                            Log.i("markNearbyUsers: ", "Cleared, Value of i is: " + i);

                        }

                        nearbyMarkers.add(mMap.addMarker(new MarkerOptions().
                                position(otherUserLocation).title("Hello Classes.User")));


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));


            String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserAvailable");
//
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(currentUserID, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {

                }
            });
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserAvailable");

        //currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(currentUserID, new GeoFire.CompletionListener() {
            @Override

            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Log.i("onComplete:", "user is " + currentUserID +
                            "Error: " + error.toString());
                }
            }
        });


    }

    public void removeUserAvailability(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(currentUserID, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Log.i("onComplete:", "user is " + currentUserID +
                            "Error: " + error.toString());
                    Toast.makeText(DefaultMapsActivity.this,
                            "Error: "+ error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String markerUserID = marker.getTag().toString();


        Bundle bundle = new Bundle();
        bundle.putString("uid", markerUserID);
        bundle.putString("current_UID", currentUserID);
        Intent intentbundle =
                new Intent(DefaultMapsActivity.this, ChatActivity.class);
        intentbundle.putExtras(bundle);
        startActivity(intentbundle);


        return false;
    }
}
