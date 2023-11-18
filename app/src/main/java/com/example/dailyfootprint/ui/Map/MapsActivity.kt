package com.example.dailyfootprint.ui.Map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dailyfootprint.R
import com.google.android.gms.common.api.Status

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.dailyfootprint.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val REQUEST_CODE_LOCATION = 1
    private val permissionsLocation = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var currentLocation: LatLng
    private val DEFAULT_ZOOM_LEVEL = 17f
    private val TAG = "MapsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Places 라이브러리 초기화
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyB_7LSzaKbT7-EhBo7-qzl6APfc7uFczfs")
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Initialize the AutocompleteSupportFragment
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))


        // Set up a PlaceSelectionListener to handle the selected place
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // Handle the selected place, for example, you can move the camera to the selected place
                val selectedLocation = place.latLng
                if (selectedLocation != null) {
                    mMap.addMarker(MarkerOptions().position(selectedLocation).title(place.name))
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            selectedLocation,
                            DEFAULT_ZOOM_LEVEL
                        )
                    )
                }
                // 추가: Log를 사용하여 디버깅 메시지 출력
                Log.d(TAG, "onPlaceSelected called. Place: ${place.name}, LatLng: $selectedLocation")
                Log.d(TAG, "Place details: ${place.toString()}")
            }

            override fun onError(status: Status) {
                // Handle the error
                Log.i(TAG, "An error occurred: $status")
            }
        })
        CurrentLocation()

    }

    fun requestPermission(){
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //28버전 이상에서 실행 가능
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this as Activity,
                permissionsLocation ,
                REQUEST_CODE_LOCATION
            )

        }

        // }

    }

    fun isLocationPermitted(): Boolean {
        for (perm in permissionsLocation ) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    @SuppressLint("MissingPermission")
    private fun CurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if(!isLocationPermitted()) {
            requestPermission()
        }
        else {
            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).
            addOnSuccessListener { success: Location? ->
                success?.let {location ->
                    currentLocation = LatLng(location.latitude, location.longitude)

                    // 무사히 위치 정보를 가져오면 현재 위치에 마커 추가
                    mMap.addMarker(MarkerOptions().position(currentLocation).title("Your Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM_LEVEL))
                }
            }
                .addOnFailureListener { fail ->
                    currentLocation = LatLng(0.0, 0.0)
                }
        }
    }
}