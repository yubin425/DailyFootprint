package com.example.dailyfootprint

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dailyfootprint.databinding.ActivityMainBinding
import com.example.dailyfootprint.ui.Map.GPSActivity
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // 앱이 시작될 때 권한을 확인하고 요청
        checkAndRequestPermission()
    }

    private lateinit var gpsActivity: GPSActivity
    private fun checkAndRequestPermission() {
        gpsActivity = GPSActivity(this)

        // 위치 권한이 허용 되어 있지 않으면 요청
        if (!gpsActivity.isLocationPermitted()) {
            gpsActivity.requestPermission()
        }
    }
}