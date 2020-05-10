package com.pranay.smartwarehouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    User currentUser;
    CoordinatorLayout coordinatorLayout;
    FloatingActionButton fab;
    AlertDialog.Builder addDeviceDialogBuilder;
    private EditText addDeviceIdText;
    private EditText addDeviceNameText;
    private AlertDialog addDeviceDialog;
    private View dialogView;
    private RecyclerView deviceListRecyclerView;
    private RecyclerView.Adapter deviceListAdapter;
    private ArrayList<DeviceItem> devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        firebaseAuth = FirebaseAuth.getInstance();


        deviceListRecyclerView = (RecyclerView) findViewById(R.id.devicelist_recyclerview);
        deviceListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        devicesList = new ArrayList<DeviceItem>();
        deviceListRecyclerView.setHasFixedSize(true);
        deviceListAdapter = new DeviceListAdapter(devicesList);
        deviceListRecyclerView.setAdapter(deviceListAdapter);

        setInitialValues();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> addDevice());
    }

    protected void setInitialValues() {
        Log.v("Pranay", "Its working here");
        MainActivity activity = this;
        Log.v("Pranay", "It is working here as well; " + firebaseAuth.getCurrentUser().getUid());

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());
        Log.v("Pranay", userReference.toString());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                if (currentUser.listOfDevices != null) {
                    activity.setTitle(currentUser.name);
                    loadUserDevices(currentUser);
                    deviceListAdapter.notifyDataSetChanged();
                } else {
                    activity.setTitle(dataSnapshot.child("name").getValue(String.class));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                logout("Error occurred. Please Login again");
            }
        });

    }

    private void loadUserDevices(User currentUser) {
        devicesList.clear();
        for (Map.Entry<String, String> deviceItem : currentUser.listOfDevices.entrySet()) {
            devicesList.add(new DeviceItem(deviceItem.getKey(), deviceItem.getValue()));
            Log.v("Pranay", deviceItem.getKey() + " " + deviceItem.getValue());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_menu_btn: {
                logout("You have been logged out successfully");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void logout(String message) {
        firebaseAuth.signOut();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, RegisterActivity.class));
    }

    protected void addDevice() {

        dialogView = getLayoutInflater().inflate(R.layout.add_device_dialog, null);
        addDeviceIdText = (EditText) dialogView.findViewById(R.id.device_id);
        addDeviceNameText = (EditText) dialogView.findViewById(R.id.device_name);
        addDeviceDialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialog)
                .setView(dialogView)
                .setNegativeButton("Cancel", null);
        addDeviceDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String deviceId = addDeviceIdText.getText().toString();
                String deviceName = addDeviceNameText.getText().toString();

                DatabaseReference deviceReference = FirebaseDatabase.getInstance().getReference().child("devices").child(deviceId);
                DatabaseReference userDevicesReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.uid).child("listOfDevices");
                deviceReference.child("name").setValue(deviceName);
                deviceReference.child("deviceId").setValue(deviceId);
                deviceReference.child("currentTemperature").setValue(0.0);
                deviceReference.child("isDeviceOn").setValue(false);
                userDevicesReference.child(deviceId).setValue(deviceName);

            }
        });
        addDeviceDialog = addDeviceDialogBuilder.create();
        addDeviceDialog.show();
    }
}
