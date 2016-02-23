package com.locator_app.locator.controller;


import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.apiservice.device.DeviceApiService;
import com.locator_app.locator.apiservice.device.RegisterDeviceRequest;
import com.locator_app.locator.apiservice.device.RegisterDeviceResponse;

import rx.Observable;

public class DeviceController {

    private static final String DEVICE_REGISTERED_FLAG = "device_registered";

    DeviceApiService service = new DeviceApiService();

    public Observable<RegisterDeviceResponse> registerDevice(String pushToken) {
        RegisterDeviceRequest request = new RegisterDeviceRequest();
        request.version = Build.VERSION.RELEASE;
        request.deviceModel = Build.MODEL;
        request.manufacturer = Build.MANUFACTURER;
        request.deviceId = Settings.Secure.getString(LocatorApplication.getAppContext()
            .getContentResolver(), Settings.Secure.ANDROID_ID);
        request.pushToken = pushToken;
        return service.registerDevice(request)
                .doOnCompleted(this::setDeviceRegisteredFlag)
                .doOnError((throwable) ->
                        Log.d("DeviceController",
                              "Could not register device: " + throwable.getMessage()));
    }

    public void setDeviceRegisteredFlag() {
        SharedPreferences prefs = LocatorApplication.getSharedPreferences();
        prefs.edit().putBoolean(DEVICE_REGISTERED_FLAG, true).commit();
    }

    public boolean isDeviceAlreadyRegistered() {
        SharedPreferences prefs = LocatorApplication.getSharedPreferences();
        return prefs.getBoolean(DEVICE_REGISTERED_FLAG, false);
    }

    public void clearDeviceRegisteredFlag() {
        SharedPreferences prefs = LocatorApplication.getSharedPreferences();
        prefs.edit().remove(DEVICE_REGISTERED_FLAG).commit();
    }

    private static DeviceController instance;
    public static DeviceController getInstance() {
        if (instance == null) {
            instance = new DeviceController();
        }
        return instance;
    }

}
