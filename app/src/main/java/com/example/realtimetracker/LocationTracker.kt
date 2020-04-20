package com.example.realtimetracker

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.StringBuilder

class LocationTracker : Service() {
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.numUpdates = 100000
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        val broadcastIntent=Intent()
        broadcastIntent.action="restartservice"
        this.sendBroadcast(broadcastIntent)
       // mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient.lastLocation.addOnCompleteListener(MainActivity()) { task ->
                var location: Location? = task.result
                if (location == null) {
                    Toast.makeText(this,"Getting location", Toast.LENGTH_LONG).show()
                    requestNewLocationData()
                }
            }
        } else {
            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.numUpdates = 100000

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            val database = Firebase.database
            val myRefLat = database.getReference("amit/Latitude")
            myRefLat.setValue(mLastLocation.latitude.toString())
            val myRefLong = database.getReference("amit/Longitude")
            myRefLong.setValue(mLastLocation.longitude.toString())
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}
