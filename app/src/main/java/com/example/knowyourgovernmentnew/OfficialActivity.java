package com.example.knowyourgovernmentnew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class OfficialActivity extends AppCompatActivity {
    private static final String TAG = "";
    private Official official;
    private View view;
    private TextView address;
    private TextView phone;
    private TextView post;
    private TextView website;
    private TextView name;
    private TextView party;
    private ImageView fb;
    private ImageView twtr;
    private ImageView yt;
    private TextView locationtext;
    private ImageView officialphoto;
    private ImageView partypic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);


        officialphoto = findViewById(R.id.officialpicture);

        address = findViewById(R.id.addressid);
        phone = findViewById(R.id.phoneid);
        website = findViewById(R.id.websiteid);
        fb =findViewById(R.id.fb);
        twtr = findViewById(R.id.twtr);
        yt = findViewById(R.id.youtube);
        post = findViewById(R.id.idpost);
        name = findViewById(R.id.idname);
        party = findViewById(R.id.idparty);
        locationtext = findViewById(R.id.locationofficialactivity);
        setTitle("Civil Advocacy");
        Intent intent = getIntent();
        if(intent.hasExtra("OfficialObject")){
            official = (Official) intent.getSerializableExtra("OfficialObject");
            if(official == null){
                Log.d(TAG, "onCreate: null official");
                return;
            }

            view = findViewById(R.id.officialbackground);
            if(official.getParty().equals("Democratic Party")){
                view.setBackgroundColor(getResources().getColor(R.color.democrat));

            }
            else if(official.getParty().equals("Republican Party")){
                view.setBackgroundColor(getResources().getColor(R.color.republican));

            }
            else{
                view.setBackgroundColor(getResources().getColor(R.color.black));
            }

            address.setText(official.getAddress());

            post.setText(official.getPostname());
            name.setText(official.getName());
            party.setText(official.getParty());
            locationtext.setText(official.getlocationtext());

            loadOfficialPhoto(official.getPhotoAddress());

            partypic = findViewById(R.id.partylogo);
            if(official.getParty().equals("Democratic Party")){
                partypic.setImageResource(R.drawable.dem_logo);
            }
            if(official.getParty().equals("Republican Party")){
                partypic.setImageResource(R.drawable.rep_logo);
            }
            else{
                partypic.setEnabled(false);
                partypic.setVisibility(View.INVISIBLE);
            }

            if(official.getTwtr().equals("Not Found")){
                twtr.setEnabled(false);
                twtr.setVisibility(View.INVISIBLE);
            }
            if(official.getYoutube().equals(("Not Found"))){
                yt.setEnabled(false);
                yt.setVisibility(View.INVISIBLE);
            }
            if(official.getfb().equals("Not Found")){
                fb.setEnabled(false);
                fb.setVisibility(View.INVISIBLE);
            }


            if(official.getPhoneno().equals("not found")){
                phone.setText("None Found");
            }else{
                phone.setText(official.getPhoneno());
            }

            if(official.getAddress().equals("Data not found")){
                address.setText("None Found");
            }else{
                address.setText(official.getAddress());
            }

            if(official.getWebsiteurl().equals("none")){
                website.setText("None Found");
            }else{
                website.setText(official.getWebsiteurl());
            }


            Linkify.addLinks(address,Linkify.ALL);
            Linkify.addLinks(phone,Linkify.ALL);
            Linkify.addLinks(website,Linkify.ALL);

        }
        else{

            Toast.makeText(this,"official not found",Toast.LENGTH_SHORT).show();
        }
    }



    public void loadOfficialPhoto(String picurl){

        if(doNetworkCheck()){
            officialphoto.setImageResource(R.drawable.placeholder);
            if(official.getPhotoAddress().equals("none")){
                officialphoto.setImageResource(R.drawable.missing);
            }
            else{
                final String url = picurl;
                Picasso picasso = new Picasso.Builder(this).listener((picasso1, uri, exception) -> {
                    final String replacedurl = url.replace("http:", "https:");
                    picasso1.get().load(replacedurl)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(officialphoto);
                }).build();

                picasso.get().load(picurl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(officialphoto);
            }
        }
        else{
            officialphoto.setImageResource(R.drawable.brokenimage);

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

    public void photoClicked(View v){
        Intent intent = new Intent(this,PhotoActivity.class);
        intent.putExtra("OfficialPic",official);
        startActivity(intent);
    }

    public void partyclicked(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String partyurl;
        if(official.getParty().equals("Democrat Party")){
            partyurl = "https://democrats.org/";
            intent.setData(Uri.parse(partyurl));
            startActivity(intent);
        }
        if(official.getParty().equals("Republican Party")){
            partyurl = "https://www.gop.com/";
            intent.setData(Uri.parse(partyurl));
            startActivity(intent);

        }
    }


    public void facebookClicked(View view){
        int currFacebookAppVersion = 3002850;
        String facebook = official.getfb();
        String FACEBOOK_URL = "https://www.facebook.com/" + facebook;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= currFacebookAppVersion){
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            }
            else {
                urlToUse = "fb://page/" + official.getfb();
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL;
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View view){
        Intent intent = null;
        String twitter = official.getTwtr();
        try {
            getPackageManager().getPackageInfo("com.twitter.android",0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitter));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }catch (Exception e){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/" + twitter));
        }
        startActivity(intent);
    }

    public void youtubeClicked(View view){
        String youtube = official.getYoutube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + youtube));
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + youtube)));
        }
    }






}



