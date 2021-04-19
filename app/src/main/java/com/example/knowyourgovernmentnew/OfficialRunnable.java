package com.example.knowyourgovernmentnew;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class OfficialRunnable implements Runnable {

    private static final String TAG = "official download runnable";
    private final MainActivity mainActivity;
    private final String zipcode;

    private static final String civicurl = "https://www.googleapis.com/civicinfo/v2/representatives";
    private static final String apikey = "AIzaSyAQ5Pow0tlrEs2IOIO_Vh29a6jE0jN4oTc";
    public OfficialRunnable(MainActivity mainActivity, String zipcode) {
        this.mainActivity = mainActivity;
        this.zipcode = zipcode;
    }


    @SuppressLint("LongLogTag")
    @Override
    public void run() {
        Uri.Builder buildURL = Uri.parse(civicurl).buildUpon();
        buildURL.appendQueryParameter("key", apikey);
        buildURL.appendQueryParameter("address", zipcode);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "run: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();
            Log.d(TAG, "run: "+ connection.getResponseCode());
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    @SuppressLint("LongLogTag")
    public void handleResults(final String jsonString){
        if(jsonString==null){
            Log.d(TAG, "handleResults: download Failed");
            mainActivity.runOnUiThread(() -> mainActivity.error404());
            return;
        }
        final ArrayList<Official> officiallist = parseJSON(jsonString);

        mainActivity.runOnUiThread(()->{
            if(officiallist!=null)
                Toast.makeText(mainActivity,"with size"+officiallist.size(),Toast.LENGTH_LONG).show();
            mainActivity.updateData(officiallist);
        });
    }

    @SuppressLint("LongLogTag")
    private ArrayList<Official> parseJSON(String s){
        ArrayList<Official> officialArrayList = new ArrayList<>();
        try{
            JSONObject jObjMain = new JSONObject(s);


            JSONArray jofficesArray = jObjMain.getJSONArray("offices");
            JSONArray jofficialsArray = jObjMain.getJSONArray("officials");


            String locationtext = "";
            JSONObject normalizedInput = jObjMain.getJSONObject("normalizedInput");
            if(normalizedInput.has("line1")){
                if(!(normalizedInput.getString("line1").equals(""))){
                    locationtext += normalizedInput.getString("line1") + ", ";
                }
            }
            if (normalizedInput.has("city")) {
                locationtext += normalizedInput.getString("city") + ", ";
            }
            if (normalizedInput.has("state")) {
                locationtext += normalizedInput.getString("state") + ' ';
            }
            if (normalizedInput.has("zip")) {
                locationtext += normalizedInput.getString("zip");
            }

            int length = jofficesArray.length();

            for(int i = 0; i<length;i++){
                JSONObject jObj = jofficesArray.getJSONObject(i);
                String officetitle = jObj.getString("name");

                JSONArray indicesofficial = jObj.getJSONArray("officialIndices");


                for (int j = 0; j<indicesofficial.length(); j++){
                    int pos = Integer.parseInt(indicesofficial.getString(j));
                    Official official = new Official();
                    official.setPostname(officetitle);
                    JSONObject jOfficial = jofficialsArray.getJSONObject(pos);

                    official.setName(jOfficial.getString("name"));
                    Log.d(TAG, "parseJSON: location is "+locationtext);
                    official.setlocationtext(locationtext);
                    String address = "";
                    if(!jOfficial.has("address")){
                        official.setAddress("No address found");
                    }else{
                        JSONArray jAddresses = jOfficial.getJSONArray("address");
                        JSONObject jAddress = jAddresses.getJSONObject(0);



                        if (jAddress.has("line1")){
                            address+=jAddress.getString("line1")+'\n';
                        }
                        if (jAddress.has("line2")) {
                            address+=jAddress.getString("line2")+'\n';
                        }
                        if (jAddress.has("line3")) {
                            address+=jAddress.getString("line3")+'\n';
                        }
                        if (jAddress.has("city")) {
                            address+=jAddress.getString("city")+", ";
                        }
                        if (jAddress.has("state")) {
                            address+=jAddress.getString("state")+' ';
                        }
                        if (jAddress.has("zip")) {
                            address+=jAddress.getString("zip");
                        }

                        official.setAddress(address);
                    }


                    if (jOfficial.has("party")) {
                        official.setParty(jOfficial.getString("party"));
                    }else{
                        official.setParty("not found");
                    }
                    if (jOfficial.has("phones")) {
                        official.setPhoneno(jOfficial.getJSONArray("phones").getString(0));
                    }else{
                        official.setPhoneno("not found");
                    }
                    if (jOfficial.has("urls")) {
                        official.setWebsiteurl(jOfficial.getJSONArray("urls").getString(0));
                    }else{
                        official.setWebsiteurl("not found");
                    }
                    if (jOfficial.has("emails")) {
                        official.setEmail(jOfficial.getJSONArray("emails").getString(0));
                    }else{
                        official.setEmail("not found");
                    }

                    if (jOfficial.has("channels")){

                        JSONArray jmedia = jOfficial.getJSONArray("channels");
                        for (int k = 0; k<jmedia.length(); k++){
                            JSONObject jChannel = jmedia.getJSONObject(k);
                            if (jChannel.getString("type").equals("Facebook")) {
                                official.setfb(jChannel.getString("id"));
                            }else{
                                official.setfb("not found");
                            }
                            if (jChannel.getString("type").equals("Twitter")) {
                                official.setTwtr(jChannel.getString("id"));
                            }else{
                                official.setTwtr("not found");
                            }
                            if (jChannel.getString("type").equals("YouTube")) {
                                official.setYoutube(jChannel.getString("id"));
                            }else{
                                official.setYoutube("not found");
                            }
                        }

                    }

                    if (jOfficial.has("photoUrl")){
                        official.setPhotoAddress(jOfficial.getString("photoUrl"));
                    }else{
                        official.setPhotoAddress("none");
                    }
                    officialArrayList.add(official);
                    Log.d(TAG, "parseJSON: official added");
                }


            }
            return officialArrayList;

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }





}

