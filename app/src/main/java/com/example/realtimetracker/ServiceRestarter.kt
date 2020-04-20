package com.example.realtimetracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log


class ServiceRestarter:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v("debug","Service restart request")
        context?.startService(Intent(context, LocationTracker::class.java))
    }
}