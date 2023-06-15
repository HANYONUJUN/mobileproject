package com.inhatc.travellog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;
import androidx.core.app.ActivityCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Locale;

public class LocationDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private EditText locationNameEditText;
    private EditText locationCodeEditText;
    private Button listButton;
    private Button saveButton;
    private Button moveButton;
    private Handler handler;
    private Runnable codeUpdateRunnable;
    private DBMap dbMap;
    private String locationCode;
    private String locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        dbMap = new DBMap(this);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Intent intent = getIntent();
        locationCode = intent.getStringExtra("locationCode");
        locationName = intent.getStringExtra("locationName");

        locationNameEditText = findViewById(R.id.locationName);
        locationCodeEditText = findViewById(R.id.locationCode);
        listButton = findViewById(R.id.buttonList);
        saveButton = findViewById(R.id.buttonSave);
        moveButton = findViewById(R.id.buttonMove);

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 위치 목록 화면으로 이동
                Intent intent = new Intent(LocationDetailActivity.this, LocationListActivity.class);
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 저장 버튼 클릭 처리
                String locationName = locationNameEditText.getText().toString();
                String locationCode = locationCodeEditText.getText().toString();

                if (locationName.isEmpty()) {
                    locationNameEditText.setError("위치 이름을 입력하세요!");
                    return;
                }

                // 위치 이름이 이미 데이터베이스에 있는지 확인
                if (dbMap.isLocationNameExists(locationName)) {
                    Toast.makeText(LocationDetailActivity.this, "이미 존재하는 이름입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 위치 이름과 위치 코드를 SQLite 데이터베이스에 저장
                dbMap.insertLocation(locationName, locationCode);
                Toast.makeText(LocationDetailActivity.this, "위치 저장됨", Toast.LENGTH_SHORT).show();

                // 지도에 마커 추가
                if (!locationCode.isEmpty()) {
                    String[] latLng = locationCode.split(",");
                    double latitude = Double.parseDouble(latLng[0].trim());
                    double longitude = Double.parseDouble(latLng[1].trim());
                    LatLng location = new LatLng(latitude, longitude);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(locationName);
                    googleMap.addMarker(markerOptions);
                }
            }
        });

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 필요한 권한이 허용되었는지 확인
                if (ContextCompat.checkSelfPermission(LocationDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(LocationDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // 허용되지 않았다면 권한 요청
                    ActivityCompat.requestPermissions(LocationDetailActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    return;
                }

                // 위치 서비스가 활성화되어 있는지 확인
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // 위치 서비스 활성화를 요청
                    Toast.makeText(LocationDetailActivity.this, "GPS를 켜주세요.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    return;
                }

                // Fused Location Provider API를 사용하여 사용자의 위치 가져오기
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocationDetailActivity.this);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(LocationDetailActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
                                } else {
                                    Toast.makeText(LocationDetailActivity.this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(LocationDetailActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LocationDetailActivity.this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // 지도 설정
        googleMap.getUiSettings().setZoomControlsEnabled(true); // 줌 컨트롤 표시
        googleMap.getUiSettings().setScrollGesturesEnabled(true); // 스크롤 동작 활성화

        // 예시 마커
        LatLng location = new LatLng(37.448344, 126.657474); // 시작 위치 설정 (37.448344, 126.657474)
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title("학교");

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
        googleMap.addMarker(markerOptions);

        // 받은 위치 코드를 locationCodeEditText에 설정
        locationCodeEditText.setText(locationCode);

        // 받은 위치 코드로 지도 이동
        if (locationCode != null && !locationCode.isEmpty()) {
            String[] latLng = locationCode.split(",");
            double latitude = Double.parseDouble(latLng[0].trim());
            double longitude = Double.parseDouble(latLng[1].trim());
            LatLng savedLocation = new LatLng(latitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(savedLocation, 16));
            googleMap.addMarker(new MarkerOptions().position(savedLocation).title(locationName));
        }

        // 지도 이동 및 줌 이벤트 처리
        handler = new Handler();
        codeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                // Google Maps 위치 코드로 locationCodeEditText 자동 업데이트

                // 예시: 현재 지도의 중심 좌표를 가져와서 locationCodeEditText 업데이트
                LatLng center = googleMap.getCameraPosition().target;
                String locationCode = String.format(Locale.KOREA, "%.5f, %.5f", center.latitude, center.longitude);
                locationCodeEditText.setText(locationCode);
            }
        };

        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                // 지도가 이동될 때 호출되는 콜백 메서드
                // 이전에 예약된 코드 실행 취소
                handler.removeCallbacks(codeUpdateRunnable);

                // 코드 실행 예약 다시 설정
                handler.postDelayed(codeUpdateRunnable, 3000); // 3초 후에 코드 실행
            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // 지도 이동이 멈추었을 때 호출되는 콜백 메서드
                // 이전에 예약된 코드 실행 취소
                handler.removeCallbacks(codeUpdateRunnable);

                // Google Maps 위치 코드로 locationCodeEditText 자동 업데이트

                // 예시: 현재 지도의 중심 좌표를 가져와서 locationCodeEditText 업데이트
                LatLng center = googleMap.getCameraPosition().target;
                String locationCode = String.format(Locale.US, "%.5f, %.5f", center.latitude, center.longitude);
                locationCodeEditText.setText(locationCode);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}