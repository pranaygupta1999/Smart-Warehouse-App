package com.pranay.smartwarehouse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceHolder> {
    private ArrayList<DeviceItem> deviceList;

    public DeviceListAdapter(ArrayList<DeviceItem> deviceList) {
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View deviceListItem = inflater.inflate(R.layout.devicelist_item, parent, false);
        return new DeviceHolder(deviceListItem);

    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {
        holder.deviceNameView.setText(deviceList.get(position).name);
        holder.deviceTemperatureView.setText("" + deviceList.get(position).currentTemperature);
        holder.deviceSwitch.setChecked(deviceList.get(position).isDeviceOn);
        String deviceId = deviceList.get(position).deviceId;
        DatabaseReference deviceReference = FirebaseDatabase.getInstance().getReference().child("devices").child(deviceId);

        holder.deviceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            deviceReference.child("isDeviceOn").setValue(isChecked);
        });
        deviceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.deviceTemperatureView.setText(dataSnapshot.child("currentTemperature").getValue(Long.class) + "");
                holder.deviceSwitch.setChecked(dataSnapshot.child("isDeviceOn").getValue(Boolean.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceHolder extends ViewHolder {
        public TextView deviceNameView;
        public TextView deviceTemperatureView;
        public Switch deviceSwitch;

        public DeviceHolder(View v) {
            super(v);
            this.deviceNameView = (TextView) v.findViewById(R.id.devicelist_item_name);
            this.deviceTemperatureView = (TextView) v.findViewById(R.id.devicelist_item_temperature);
            this.deviceSwitch = (Switch) v.findViewById(R.id.devicelist_item_switch);
        }
    }

}

