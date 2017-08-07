package com.sdutacm.locationservice;

import com.google.android.maps.GeoPoint;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class LocationUtil {

    public static GeoPoint getFromLocationName(String address) {
        String url = "http://maps.google.com/maps/api/geocode/json";
        HttpGet httpGet = new HttpGet(url + "?sensor=false&address=" + address);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getGeoPoint(jsonObject);
    }

    private static GeoPoint getGeoPoint(JSONObject jsonObject) {
        try {
            JSONArray array = (JSONArray) jsonObject.get("results");
            JSONObject first = array.getJSONObject(0);
            JSONObject geometry = first.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");

            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");

            return new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}