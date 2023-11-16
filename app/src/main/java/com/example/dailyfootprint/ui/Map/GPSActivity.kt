package com.example.dailyfootprint.ui.Map

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.SupportMapFragment

import com.google.android.gms.maps.*

class GPSActivity(private val context: Context) : AppCompatActivity() {
    private val REQUEST_CODE_LOCATION = 1
    private val permissionsLocation = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.dailyfootprint.R.layout.activity_gps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
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



    @SuppressLint("MissingPermission")
    private fun calculateDistance(textView: TextView) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if(!isLocationPermitted()) {
            requestPermission()
        }
        else {
            fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null).
            addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    val selectedLocation = Location("selectedPoint").apply {
                        latitude = 0.0
                        longitude = 0.0
                    }
                    textView.text = "${location.distanceTo(selectedLocation)}M"
                }
            }
                .addOnFailureListener { fail ->
                    textView.text = fail.localizedMessage
                }
        }
    }


}
