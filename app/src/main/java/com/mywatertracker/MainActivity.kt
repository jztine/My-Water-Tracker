package com.mywatertracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val NOTIFICATION_PERMISSION_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissionAndStartService()

        val btnDrinkWater = findViewById<Button>(R.id.btnDrinkWater)
        btnDrinkWater.setOnClickListener {
            addWater()
        }
    }

    private fun checkPermissionAndStartService() {
        // Step 7: Request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE)
            } else {
                startWaterService()
            }
        } else {
            startWaterService()
        }
    }


    private fun startWaterService() {
        val serviceIntent = Intent(this, WaterService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun addWater() {
        Log.d("MainActivity", "Button clicked! Sending 250ml.") // <-- ADD THIS LINE

        val serviceIntent = Intent(this, WaterService::class.java)
        serviceIntent.putExtra(WaterService.EXTRA_WATER_ADD, 250.0)
        startService(serviceIntent)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startWaterService()
            }
        }
    }
}