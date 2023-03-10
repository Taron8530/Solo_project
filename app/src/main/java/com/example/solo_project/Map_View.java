package com.example.solo_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Map_View extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private String TAG = "Map_View";
    private MapPOIItem mMarker;
    private TextView exit;
    private Button submit;
    private double latitude = 0; // 위도
    private double longitude = 0; // 경도
    private String location;
    private TextView location_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        exit = findViewById(R.id.map_view_exit);
        submit = findViewById(R.id.map_view_submit);
        location_address = findViewById(R.id.location_address);

        submit.setClickable(false);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Map_View.this,chating.class);
                i.putExtra("location",location);
                i.putExtra("lati",latitude);
                i.putExtra("long",longitude);
                Log.e(TAG,String.valueOf(latitude) +"/"+ String.valueOf(longitude));
                setResult(RESULT_OK,i);
                finish();
            }
        });
        if(!checkLocationServicesStatus()){
            showDialogForLocationServiceSetting();
        }else{
            checkRunTimePermission();
        }

        //지도를 띄우자
        // java code
        mapView = new MapView(Map_View.this);
        mapViewContainer =  findViewById(R.id.map_view);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setMapTilePersistentCacheEnabled(true);
        mapView.zoomIn(true);
        mapView.zoomOut(true);

        mMarker = new MapPOIItem();
    }

    // 권한 체크 이후로직
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        // READ_PHONE_STATE의 권한 체크 결과를 불러온다
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == 1000) {
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // 권한 체크에 동의를 하지 않으면 안드로이드 종료
            if (check_result == false) {
                finish();
            }
        }else if(requestCode == 2001){
            //사용자가 GPS 활성 시켰는지 검사
            if (checkLocationServicesStatus()) {
                if (checkLocationServicesStatus()) {
                    Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                    checkRunTimePermission();
                    return;
                }
            }
        }
    }
    private void checkRunTimePermission(){
        // 권한ID를 가져옵니다
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);

        int permission2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permission3 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1000);
            }
            return;
        }
    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Map_View.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(false);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, 2001);
            }
        });
        builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        Log.e(TAG,"onCurrentLocationUpdate: "+mapPoint.toString() + String.valueOf(v));
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
        Log.e(TAG,"onCurrentLocationDeviceHeadingUpdate : "+ String.valueOf(v));
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
        Log.e(TAG,"onMapViewCenterPointMoved: "+mapPoint.toString());
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
        Log.e(TAG,"onMapViewZoomLevelChanged: "+String.valueOf(i));
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        Log.e(TAG,"onMapViewSingleTapped: 위도 : "+mapPoint.getMapPointGeoCoord().latitude + "/ 경도 : "+ mapPoint.getMapPointGeoCoord().longitude);
        MapPoint.GeoCoordinate coord = mapPoint.getMapPointGeoCoord();
        longitude = mapPoint.getMapPointGeoCoord().longitude; // 경도
        latitude = mapPoint.getMapPointGeoCoord().latitude; //위도
        MapReverseGeoCoder.ReverseGeoCodingResultListener reverseGeoCodingResultListener = new MapReverseGeoCoder.ReverseGeoCodingResultListener() {
            @Override
            public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
                // 변환된 주소를 처리하는 코드 작성
                Log.e("주소",s);
                mMarker.setItemName(s);
                location = s;
                submit.setClickable(true);
                location_address.setText(s);
            }

            @Override
            public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
                // 주소 변환에 실패한 경우 처리하는 코드 작성
                Log.e("주소","실패함");
            }
        };
        MapReverseGeoCoder mapGeoCoder = new MapReverseGeoCoder("4d73f7809329175f76afdf054f1e3e42", mapPoint, reverseGeoCodingResultListener, this );
        mapGeoCoder.startFindingAddress( );
        mMarker.setItemName("위치");
        mMarker.setMapPoint(mapPoint);
        mMarker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        if(mapView.getPOIItems().length == 0){
            mapView.addPOIItem(mMarker);
        }

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        Log.e(TAG,"onMapViewDoubleTapped: "+mapPoint.toString());
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        Log.e(TAG,"onMapViewLongPressed: "+mapPoint.toString());
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        Log.e(TAG,"onMapViewDragStarted: "+mapPoint.toString());
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        Log.e(TAG,"onMapViewDragEnded: "+mapPoint.toString());
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        Log.e(TAG,"onMapViewMoveFinished: "+ mapPoint.toString());
    }
}