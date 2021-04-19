package com.example.knowyourgovernmentnew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitle("Civil Advocacy");

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    public void onClick(View v) {

        Intent APIintent = new Intent(Intent.ACTION_VIEW);
        APIintent.setData(Uri.parse("https://developers.google.com/civic-information/"));
        startActivity(APIintent);
    }




}