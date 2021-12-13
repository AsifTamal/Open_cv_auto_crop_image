package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class PermissionHelper {
    public static final int STORAGE_REQUEST_CODE = 112;

    Context context;
    String storagePermission[];

    public PermissionHelper(Context context) {
        this.context = context;

        storagePermission=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA};


    }
    public boolean checkStoregePermission() {

        boolean result= ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        boolean result2= ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)==(PackageManager.PERMISSION_GRANTED);
        boolean result3= ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)==(PackageManager.PERMISSION_GRANTED);
        boolean result4= ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO)==(PackageManager.PERMISSION_GRANTED);
        boolean result5= ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        return result && result1 && result2 && result3 && result4 && result5;

    }

//    public boolean checkLocationPermission() {
//
//        boolean result= ContextCompat.checkSelfPermission(context,
//                Manifest.permission.READ_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
//        boolean result1= ContextCompat.checkSelfPermission(context,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
//        return result && result1;
//
//    }

    public void requestStoragePermission() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) context,storagePermission,STORAGE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public static boolean canWriteOnExternalStorage() {
        // get the state of your external storage
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // if storage is mounted return true
            //  Log.v(“sTag”, “Yes, can write to external storage.”);
            return true;
        }
        return false;
    }
}
