package com.sdutacm.locationservice;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;

import java.io.IOException;
import java.util.List;

public class MainActivity extends MapActivity {

    private MapView mapView;
    private EditText name;
    private Button find;

    private Geocoder geocoder;

    private static final double lat = 39.908716;
    private static final double lng = 116.397529;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getController().setZoom(17);
        mapView.getController().animateTo(new GeoPoint((int)(lat * 1E6),(int)(lng * 1E6)));

        geocoder = new Geocoder(this);

        name = (EditText) findViewById(R.id.name);
        find = (Button) findViewById(R.id.find);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = name.getText().toString();
                try {
                    List<Address> addrs = geocoder.getFromLocationName(keyword, 3);
                    if (addrs != null && addrs.size() > 0) {
                        int latE6 = (int) (addrs.get(0).getLatitude() * 1E6);
                        int lngE6 = (int) (addrs.get(0).getLongitude() * 1E6);
                        GeoPoint point = new GeoPoint(latE6, lngE6);
                        mapView.getController().animateTo(point);

                        final MapView.LayoutParams params = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT, point, LayoutParams.BOTTOM_CENTER);
                        final ImageView marker = new ImageView(MainActivity.this);
                        marker.setImageResource(R.mipmap.ic_launcher);
                        marker.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "hello geocoder!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mapView.addView(marker, params);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}