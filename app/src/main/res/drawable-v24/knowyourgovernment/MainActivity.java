package com.example.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "";
    private final ArrayList<Official> officialArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OfficialAdapter officialAdapter;
    private static final int REQUEST_CODE_A = 1;
    private static final int REQUEST_CODE_B = 2;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;

    private static String locationString = "Unspecified Location";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(officialAdapter);
        textView = findViewById(R.id.locationmainactivity);
        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        determineLocation();

    }

    private void determineLocation() {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            locationString = getPlace(location);
                            textView.setText(locationString);
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }

        
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }


    private String getPlace(Location loc) {

        StringBuilder sb = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            sb.append(String.format(
                    Locale.getDefault(),
                    "%s, %s%n%nProvider: %s%n%n%.5f, %.5f",
                    city, state, loc.getProvider(), loc.getLatitude(), loc.getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    textView.setText("permission denied");
                }
            }
        }
    }


    @Override
    public void onClick(View view) {
        Toast.makeText(this, "onclick was done", Toast.LENGTH_SHORT).show();
    }



    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    public void loadOfficials(View view){


    }

    public boolean onOptionsItemSelected(MenuItem item){//NEED CHANGES
        Log.d(TAG, "onOptionsItemSelected: menu clicked");
        int id = item.getItemId();
        if(id ==R.id.infobutton){
            openinfopage();
            return true;
        }
        if(id ==R.id.searchbutton){
            openSearchPage();
            return true;
        }
        return false;
    }

    private void openSearchPage() {

    }

    private void openinfopage() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivitymenu, menu);
        menu.findItem(R.id.infobutton).setTitle("Info");
        menu.findItem(R.id.searchbutton).setTitle("AddNote");
        return true;
    }
}