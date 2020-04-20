package com.example.realtimetracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    lateinit var serviceIntent:Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(isLocationEnabled()) {
            try {
                ServiceUtil(this).execute()
            } catch (e:Exception){
                Log.v("debug",e.toString())
            }
        } else {
            setLocationPermission()
        }

    }

    override fun onDestroy() {
        stopService(serviceIntent)
        super.onDestroy()
    }

    fun setLocationPermission(){
        val permission= ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
        if(permission!= PackageManager.PERMISSION_GRANTED){
            Log.v("Coding","Permission Denied")
        }
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
            val builder=androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setMessage("Location permission is required")
            builder.setTitle("Permission Required!")
            builder.setPositiveButton("Ok")
            {
                    dialog,which->
                Log.v("Coding","Clicked")
                makeRequest()
            }
            val dialog=builder.create()
            dialog.show()
        } else{
            makeRequest()
        }
    }

    fun setBackgroundLocationPermission(){
        val permission= ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if(permission!= PackageManager.PERMISSION_GRANTED){
            Log.v("Coding","Permission Denied")
        }
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
            val builder=androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setMessage("Background Location permission is required")
            builder.setTitle("Permission Required!")
            builder.setPositiveButton("Ok")
            {
                    dialog,which->
                Log.v("Coding","Clicked")
                makeRequestBackground()
            }
            val dialog=builder.create()
            dialog.show()
        } else{
            makeRequestBackground()
        }
    }

    fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
    }
    fun makeRequestBackground(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),2)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1->{
                if(grantResults.isEmpty() || grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                    Log.v("Coding","Permission has been denied by user")
                } else{
                    Log.v("Coding","Permission has been granted by user")

                }
            }
            2->{
                if(grantResults.isEmpty() || grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                    Log.v("Coding","Permission has been denied by user")
                } else{
                    Log.v("Coding","Permission has been granted by user")
                    ServiceUtil(this).execute()
                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}

class ServiceUtil(val context: Context):AsyncTask<String,String,String>(){
    override fun doInBackground(vararg p0: String?): String {
        (context as MainActivity).serviceIntent=Intent(context,LocationTracker::class.java)
        context.startService(context.serviceIntent)
        return "Done"
    }
}