package com.example.hasta_kala.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.hasta_kala.R

object NotificationHelper {
    private const val CHANNEL_ID = "hasta_kala_notifications"
    private const val DOWNLOAD_CHANNEL_ID = "download_notifications"

    fun showOtpNotification(context: Context, otp: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Shop Setup", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Shop Verification")
            .setContentText("Your OTP is: $otp")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }

    fun showInventoryAlert(context: Context, productName: String, currentStock: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("inventory_alerts", "Inventory Alerts", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "inventory_alerts")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Low Stock Alert")
            .setContentText("$productName is running low ($currentStock left)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(productName.hashCode(), notification)
    }

    fun showTrendingAlert(context: Context, productName: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("sales_alerts", "Sales Alerts", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "sales_alerts")
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle("Trending Product!")
            .setContentText("$productName is gaining popularity")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(productName.hashCode() + 1, notification)
    }

    fun showDownloadNotification(context: Context, fileName: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(DOWNLOAD_CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Download Complete")
            .setContentText("$fileName has been saved to Downloads")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(fileName.hashCode(), notification)
    }
}
