package com.example.dailyfootprint.ui.GPS

import android.Manifest
import android.annotation.TargetApi
import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class GPSActivity(private val context: Context) : AppCompatActivity() {
    private val REQUEST_CODE_LOCATION = 1
    private val permissionsLocation = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.dailyfootprint.R.layout.activity_gps)
    }

    fun requestPermission(){
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //28버전 이상에서 실행 가능
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    permissionsLocation ,
                    REQUEST_CODE_LOCATION
                )

            }

       // }

    }

    fun isLocationPermitted(): Boolean {
        for (perm in permissionsLocation ) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}
