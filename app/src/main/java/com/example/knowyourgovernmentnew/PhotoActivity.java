package com.example.knowyourgovernmentnew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "";
    private Official o;
    private View v;
    private TextView post;
    private TextView name;
    private TextView location;
    ImageView officialpic;
    ImageView partylogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        v = findViewById(R.id.photoactivityview);
        post = findViewById(R.id.officialpostphotoactivity);
        name = findViewById(R.id.officialnamephotoactivity);
        officialpic = findViewById(R.id.officialimagephotoactivity);
        partylogo = findViewById(R.id.partyphotologo);
        location = findViewById(R.id.locationphotoactitivity);

        setTitle("Civil Advocacy");
        Intent intent = getIntent();
        if(intent.hasExtra("OfficialPic")){
            o = (Official) intent.getSerializableExtra("OfficialPic");
            if(o==null){
                Log.d(TAG, "onCreate: null official passed");
                return;
            }
            location.setText(o.getlocationtext());
            if (o.getParty().equals("Democratic Party")) {
                v.setBackgroundColor(getResources().getColor(R.color.democrat));

            }
            else if (o.getParty().equals("Republican Party")) {
                v.setBackgroundColor(getResources().getColor(R.color.republican));
            }
            else {
                v.setBackgroundColor(getResources().getColor(R.color.black));
            }

            post.setText(o.getPostname());
            name.setText(o.getName());

            loadPhoto(o.getPhotoAddress());

            if (o.getParty().equals("Democratic Party")) {

                partylogo.setImageResource(R.drawable.dem_logo);

            }

            else if (o.getParty().equals("Republican Party")) {
                partylogo.setImageResource(R.drawable.rep_logo);

            }

            else {
                partylogo.setEnabled(false);
                partylogo.setVisibility(View.GONE);
            }


        }

        else {
            Log.d(TAG, "onCreate: official not loaded to photoactivity");
            Toast.makeText(this, "official not found",Toast.LENGTH_SHORT).show();
        }




    }

    public void loadPhoto(String url){
        if(doNetworkCheck()){
            officialpic.setImageResource(R.drawable.placeholder);
            if (o.getPhotoAddress().equals("none")) {
                officialpic.setImageResource(R.drawable.missing);
            }
            else {
                final String picurl = url;
                Picasso picasso = new Picasso.Builder(this).listener((picasso1, uri, exception) -> {

                    final String secureUrl = picurl.replace("http:", "https:");
                    picasso1.get().load(secureUrl)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(officialpic);
                }).build();

                picasso.get().load(url)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(officialpic);
            }
        }
        else{
            officialpic.setImageResource(R.drawable.brokenimage);
        }


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


    public void officialpartyclicked(View v){
        Intent i = new Intent(Intent.ACTION_VIEW);


        String partyLink;

        if (o.getParty().equals("Democratic Party")) {

            partyLink = "https://democrats.org/";
            i.setData(Uri.parse(partyLink));
            startActivity(i);

        }

        if (o.getParty().equals("Republican Party")) {

            partyLink = "https://www.gop.com/";
            i.setData(Uri.parse(partyLink));
            startActivity(i);

        }
    }







}




