package com.example.location

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*

class LocationService : Service(), LocationListener {

    private val TAG = "LocationService"
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var intent: Intent
    private var isGPSEnable = false
    private var isNetworkEnable = false
    private var latitude = 0.0
    private var longitude = 0.0
    private var location: Location? = null
    private var handler = Handler()
    private var timer: Timer? = null
    private var intervel: Long = 10000
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        intent = Intent("ABC")
        timer = Timer()
        timer!!.schedule(TimerTaskToGetLocation(), 5, intervel)

    }

    private inner class TimerTaskToGetLocation : TimerTask() {
        override fun run() {
            handler.post(Runnable { fn_getlocation() })
        }


    }


    private fun fn_update(location: Location) {
        intent.putExtra("LAT", location.latitude);
        intent.putExtra("LAN", location.longitude);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }


    private fun fn_getlocation() {
        locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGPSEnable && !isNetworkEnable) {
        } else {
            if (isNetworkEnable) {
                Log.d(TAG, "fn_getlocation: isNetworkEnable")
                location = null
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    0.0f,
                    this
                )
                if (locationManager != null) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        return
                    }
                    location =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                    if (location != null) {
                        Log.e("latitude", location!!.latitude.toString() + "")
                        Log.e("longitude", location!!.longitude.toString() + "")
                        latitude = location!!.latitude
                        longitude = location!!.longitude
                        fn_update(location!!)
                    }
                }
            } else if (isGPSEnable) {
                Log.d(TAG, "fn_getlocation: isGPSEnable")
                location = null
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
                    if (location != null) {
                        Log.e("latitude", location!!.latitude.toString() + "")
                        Log.e("longitude", location!!.longitude.toString() + "")
                        latitude = location!!.latitude
                        longitude = location!!.longitude
                        fn_update(location!!)
                    }
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {

    }
}
