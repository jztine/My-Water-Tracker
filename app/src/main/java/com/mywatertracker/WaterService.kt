package com.mywatertracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat

class WaterService : Service() {

    private var waterLevel = 0.0 // Step 4: Track water level
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    companion object {
        const val NOTIFICATION_ID = 1 // Step 5: Notification ID
        const val CHANNEL_ID = "WaterTrackerChannel"
        const val EXTRA_WATER_ADD = "extra_water_add" // Step 5: Intent data key
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification()) // Step 7: Start Foreground Service


        runnable = object : Runnable {
            override fun run() {
                waterLevel -= 0.144
                updateNotification()
                handler.postDelayed(this, 5000)
            }
        }
        handler.post(runnable)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Step 9: Handle addition of fluids
        val addedWater = intent?.getDoubleExtra(EXTRA_WATER_ADD, 0.0) ?: 0.0
        if (addedWater > 0) {
            waterLevel += addedWater
            updateNotification()
        }
        return START_STICKY
    }


    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("My Water Tracker")
            .setContentText("Current Balance: %.2f ml".format(waterLevel))
            .setSmallIcon(R.drawable.ic_water_notification)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Water Tracker Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}