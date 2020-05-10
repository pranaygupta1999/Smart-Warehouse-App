package com.pranay.smartwarehouse;

public class DeviceItem {
    public String deviceId;
    public String name;
    public double currentTemperature = 0.0;
    public boolean isDeviceOn = false;

    public DeviceItem(String deviceId, String name, double currentTemperature, boolean isDeviceOn) {
        this.deviceId = deviceId;
        this.name = name;
        this.currentTemperature = currentTemperature;
        this.isDeviceOn = isDeviceOn;
    }

    public DeviceItem(String deviceId, String name) {
        this.deviceId = deviceId;
        this.name = name;
    }
}
