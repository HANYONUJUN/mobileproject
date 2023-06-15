package com.inhatc.travellog;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LocationListActivity extends AppCompatActivity {

    private ListView locationListView;
    private ArrayAdapter<String> locationListAdapter;
    private ArrayList<String> locationList;
    private DBMap dbMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        dbMap = new DBMap(this);

        locationListView = findViewById(R.id.locationListView);
        locationList = new ArrayList<>();
        locationListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, locationList);
        locationListView.setAdapter(locationListAdapter);
        locationListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        loadLocationData();

        Button editButton = findViewById(R.id.editButton);
        Button moveButton = findViewById(R.id.moveButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button mapButton = findViewById(R.id.mapButton);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationListActivity.this, LocationDetailActivity.class);
                startActivity(intent);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = locationListView.getCheckedItemPosition();
                if (selectedPosition != ListView.INVALID_POSITION) {
                    String selectedLocation = locationList.get(selectedPosition);
                    Toast.makeText(LocationListActivity.this, "수정할 위치: " + selectedLocation, Toast.LENGTH_SHORT).show();

                    // showEditDialog() 메서드 호출
                    showEditDialog(selectedLocation);
                } else {
                    Toast.makeText(LocationListActivity.this, "수정할 위치를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = locationListView.getCheckedItemPosition();
                if (selectedPosition != ListView.INVALID_POSITION) {
                    String selectedLocation = locationList.get(selectedPosition);
                    Toast.makeText(LocationListActivity.this, "이동할 위치: " + selectedLocation, Toast.LENGTH_SHORT).show();

                    // LocationDetailActivity로 이동하면서 선택한 항목의 위치 코드와 이름 전달
                    String locationCode = getLocationCodeFromLocationData(selectedLocation);
                    String locationName = getLocationNameFromLocationData(selectedLocation);
                    Intent intent = new Intent(LocationListActivity.this, LocationDetailActivity.class);
                    intent.putExtra("locationCode", locationCode);
                    intent.putExtra("locationName", locationName);
                    startActivity(intent);
                } else {
                    Toast.makeText(LocationListActivity.this, "이동할 위치를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = locationListView.getCheckedItemPosition();
                if (selectedPosition != ListView.INVALID_POSITION) {
                    String selectedLocation = locationList.get(selectedPosition);
                    Toast.makeText(LocationListActivity.this, "삭제할 위치: " + selectedLocation, Toast.LENGTH_SHORT).show();

                    // TODO: 삭제 작업 수행
                    SQLiteDatabase db = dbMap.getWritableDatabase();
                    int selectedId = getSelectedLocationId(selectedLocation); // 선택된 위치의 ID 가져오기
                    int rowsAffected = db.delete("Locations", "id=?", new String[]{String.valueOf(selectedId)});
                    if (rowsAffected > 0) {
                        Toast.makeText(LocationListActivity.this, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        loadLocationData(); // 데이터 다시 로드
                    } else {
                        Toast.makeText(LocationListActivity.this, "삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LocationListActivity.this, "삭제할 위치를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadLocationData() {
        locationList.clear();

        SQLiteDatabase db = dbMap.getReadableDatabase();
        Cursor cursor = db.query("Locations", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String code = cursor.getString(cursor.getColumnIndexOrThrow("code"));
                String locationData = "ID: " + id + " | Name: " + name + " | Code: " + code;
                locationList.add(locationData);
            } while (cursor.moveToNext());
            cursor.close();
        }

        locationListAdapter.notifyDataSetChanged();
    }

    private String getLocationCodeFromLocationData(String locationData) {
        String[] locationDataParts = locationData.split("\\|");
        String codePart = locationDataParts[2].replace("Code:", "").trim();
        return codePart;
    }
    private String getLocationNameFromLocationData(String locationData) {
        String[] locationDataParts = locationData.split("\\|");
        String namePart = locationDataParts[1].replace("Name:", "").trim();
        return namePart;
    }
    private int getSelectedLocationId(String selectedLocation) {
        // 선택된 위치의 ID 가져오기
        String[] locationDataParts = selectedLocation.split("\\|");
        String idPart = locationDataParts[0].replace("ID:", "").trim();
        return Integer.parseInt(idPart);
    }

    // 수정 버튼 클릭 시 다이얼로그 표시
    private void showEditDialog(final String selectedLocation) {
        final EditText input = new EditText(this);
        input.setText(""); // EditText를 비워둡니다.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이름 수정")
                .setView(input)
                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newLocation = input.getText().toString().trim();
                        if (!newLocation.isEmpty()) {
                            SQLiteDatabase db = dbMap.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("name", newLocation);
                            int selectedId = getSelectedLocationId(selectedLocation);
                            int rowsAffected = db.update("Locations", values, "id=?", new String[]{String.valueOf(selectedId)});
                            if (rowsAffected > 0) {
                                Toast.makeText(LocationListActivity.this, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                loadLocationData();
                            } else {
                                Toast.makeText(LocationListActivity.this, "수정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LocationListActivity.this, "새로운 위치를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
