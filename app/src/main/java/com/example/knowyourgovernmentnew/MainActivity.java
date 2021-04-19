package com.example.knowyourgovernmentnew;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
    Official official;
    private static String locationString = "Unspecified Location";
    private static String zip;
    private TextView locationtext;
    private String searchstring;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationtext = findViewById(R.id.locationmainactivity);
        setTitle("Know Your Government");

        if(doNetworkCheck()){

            recyclerView = findViewById(R.id.recycler);
            officialAdapter = new OfficialAdapter(this,officialArrayList);
            recyclerView.setAdapter(officialAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            locationtext = findViewById(R.id.locationmainactivity);
            locationtext.setText("User Location");
            mFusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this);
            determineLocation();
        }
        else{
            networkError();
            locationtext.setVisibility(View.INVISIBLE);
        }



    }

    private void determineLocation() {

        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            locationString = getPlace(location);
                            locationtext.setText(locationString);
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

    private void networkError(){
        String message = "please check network and try again";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private String getPlace(Location loc) {

        StringBuilder sb = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            zip = addresses.get(0).getPostalCode();

            OfficialRunnable officialtaskrunner = new OfficialRunnable(this,zip);
            new Thread(officialtaskrunner).start();

            sb.append(String.format(
                    Locale.getDefault(),
                    "%s, %s, %s",
                    city, state,zip));

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
                    locationtext.setText("permission denied");
                }
            }
        }
    }

    public void updateData(ArrayList<Official> off){
        if(off ==null){
            Toast.makeText(this,"Please enter a valid zip code",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            officialArrayList.clear();
            officialArrayList.addAll(off);
            officialAdapter.notifyDataSetChanged();

            locationtext.setText(off.get(0).getlocationtext());
        }
    }



    @Override
    public void onClick(View view) {
        Toast.makeText(this, "onclick was done", Toast.LENGTH_SHORT).show();
        int pos = recyclerView.getChildLayoutPosition(view);
        official = officialArrayList.get(pos);

        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("OfficialObject",official);
        startActivity(intent);
    }



    @Override
    public boolean onLongClick(View view) {
        return true;
    }

    public boolean doNetworkCheck(){

        ConnectivityManager conn = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if(conn == null){
            return false;
        }
        if(networkInfo != null){
            return true;
        }
        else{
            return false;
        }
    }



    public boolean onOptionsItemSelected(MenuItem item){//NEED CHANGES

        int id = item.getItemId();
        if(id ==R.id.searchbutton){
            if(doNetworkCheck()){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setGravity(Gravity.CENTER_HORIZONTAL);
                builder.setView(et);


                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    searchstring = et.getText().toString();
                    OfficialRunnable officialrunner = new OfficialRunnable(MainActivity.this, searchstring);
                    new Thread(officialrunner).start();
                });

                builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
                });

                builder.setMessage("Enter a state, city, or zipcode");
                builder.setTitle("Enter Address");

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                networkError();
            }

        }
        else if (item.getItemId()==R.id.infobutton) {

            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);

        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivitymenu, menu);
        menu.findItem(R.id.infobutton).setTitle("Info");
        menu.findItem(R.id.searchbutton).setTitle("AddNote");
        return true;
    }

    public void error404() {
        showDownloadErrorDialog();
    }

    public void showDownloadErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please enter a valid state, city, or zip");
        builder.setTitle("Wrong location!");

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}