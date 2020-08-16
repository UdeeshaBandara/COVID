package com.MAD.healthapp;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class NearPlaces extends AsyncTask {
    String googlePlaces;
    GoogleMap googleMap;
    String url;

    @Override
    protected Object doInBackground(Object[] objects) {


        googleMap =(GoogleMap)objects[0];
        url=(String) objects[1];
        Log.e("sds",url);

        GetUrl geturl=new GetUrl();
        try{
            googlePlaces=geturl.readURL(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlaces;
    }

    @Override
    protected void onPostExecute(Object o) {
        try {
            JSONObject object=new JSONObject();
            JSONArray arr=object.getJSONArray("results");

            for(int i= 0; i<arr.length();i++){

                JSONObject jsonObject=arr.getJSONObject(i);
                JSONObject locationObj = jsonObject.getJSONObject("geometry").getJSONObject("location");

                String latitude=locationObj.getString("lat");
                String logitude= locationObj.getString("lng");

                JSONObject nameObj=arr.getJSONObject(i);
                String name=nameObj.getString("name");

                LatLng latlng=new LatLng(Double.parseDouble(latitude),Double.parseDouble(logitude));

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.title(name);
                markerOptions.position(latlng);


                googleMap.addMarker(markerOptions);

            }

        }catch (JSONException e){

            e.printStackTrace();
        }
    }
}
