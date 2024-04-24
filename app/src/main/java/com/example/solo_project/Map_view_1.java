package com.example.solo_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class Map_view_1 extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private TextView location_address;
    private String[] data;
    private double lati;
    private double longs;
    private MapPOIItem mMarker;
    private MapPoint mapPoint;
    private Button goto_my_location;
    private Button goto_location;
    private String location_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view1);
        Intent i = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        data = i.getStringExtra("location").split(">");
        location_address = findViewById(R.id.location_address);
        if(data.length > 2){
            location_add = data[0];
            location_address.setText(location_add);
            lati = Double.parseDouble(data[1]);
            longs = Double.parseDouble(data[2]);
            mapPoint = MapPoint.mapPointWithGeoCoord(lati, longs);

        }
        mapView = new MapView(Map_view_1.this);
        mapViewContainer =  findViewById(R.id.map_view);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        goto_my_location = findViewById(R.id.map_view_goto_my_location);
        goto_location = findViewById(R.id.map_view_goto_location);

        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setMapTilePersistentCacheEnabled(true);
        mapView.setZoomLevel(2, true);
        mapView.zoomIn(true);
        mapView.zoomOut(true);

        mMarker = new MapPOIItem();
        mMarker.setItemName(location_add);
        mMarker.setMapPoint(mapPoint);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lati, longs), true);
        mMarker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(mMarker);
        goto_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lati, longs), true);
            }
        });
        goto_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            }
        });
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}