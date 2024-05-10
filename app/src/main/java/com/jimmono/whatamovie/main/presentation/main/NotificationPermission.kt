package com.jimmono.whatamovie.main.presentation.main

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jimmono.whatamovie.R

class NotificationPermission(context: Context) : ContextWrapper(context) {


    private val channelId = "What A Movie"
    private val notificationId = 1001


    // private val REQUEST_CODE_PERMISSIONS = 101 // Choose a unique request code
/*
    fun requestNotificationPermission(activity: Activity) {
        val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)

        val activityResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { grantResults ->
            val allPermissionsGranted = grantResults.values.all { it }
            if (allPermissionsGranted) {
                displayNotification() // Permission granted, proceed with notification
            } else {
                // Handle case where permission is denied (e.g., show a message)
            }
        }

        activityResultLauncher.launch(permissions)
    }

*/
    fun displayNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel() // Ensure channel is created before displaying notification
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Welcome to What A Movie!")
            .setContentText("Just A Normal Notification")
            .setSmallIcon(R.drawable.ic_series)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ContextCompat.checkSelfPermission(
                    this@NotificationPermission,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationId, builder.build())
            } else {
                //
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "My Notification Channel" // Replace with a user-friendly name
        val descriptionText = "Channel description" // Optional description
        val importance = NotificationManager.IMPORTANCE_DEFAULT // Adjust importance as needed

        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}

