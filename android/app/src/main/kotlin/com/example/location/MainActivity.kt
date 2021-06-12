package com.example.location

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {
    private val CHANNEL = "ABCD"
    private val TAG = "MainActivity"
    private lateinit var receiver: BroadcastReceiver
    private val listLatLan:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this.flutterEngine!!)
        requestPermissionsForLocation()

        MethodChannel(
            getFlutterEngine()!!.getDartExecutor().getBinaryMessenger(),
            CHANNEL
        ).setMethodCallHandler(MethodChannel.MethodCallHandler { call, result ->
            if (call.method.equals("list()")){
                result.success(listLatLan.toString())
            }
        })
    }

    private fun requestPermissionsForLocation() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // You can use the API that requires the permission.
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) -> {
                        // You can use the API that requires the permission.
                        getLocation()
                    }
                    else -> {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            101
                        )
                    }
                }
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    100
                )
            }
        }
    }

    private fun getLocation() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val lat = intent!!.getDoubleExtra("LAT", 0.0)
                val lng = intent.getDoubleExtra("LAN", 0.0)

                val latLan = "$lat,$lng"
                listLatLan.add(latLan)
            }
        }
        startService(Intent(this, LocationService::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    requestPermissionsForLocation()
                } else {
                    Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show()
                }
                return
            }
            101 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    requestPermissionsForLocation()
                } else {
                    Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            receiver,
            IntentFilter("ABC")
        )
    }
}
