package com.manya.epamtaskdoingmulti

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

/**
 * Simple started [Service] with progress notification.
 *
 * @author Maria Kirdun
 */

class DownloadService : Service() {

    private lateinit var notificationManager: NotificationManager
    private var notificationBuilder: NotificationCompat.Builder? = null

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val chanelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("ForegroundDownloadService", "ForegroundDownloadService")
        } else {
            ""
        }

        val progressCurrent = intent!!.getIntExtra(PROGRESS,0)

        notificationBuilder = NotificationCompat.Builder(this, chanelId)
            .setSmallIcon(R.drawable.notification_bg)
            .setContentTitle("Picture Download")
            .setContentText("Download in progress")
                NotificationManagerCompat.from(this).apply {
                    notificationBuilder!!.setProgress(PROGRESS_MAX, progressCurrent, true)
                    notify(1, notificationBuilder!!.build())

                    if (PROGRESS_MAX == progressCurrent) {
                        notificationBuilder!!.setContentText("Download complete")
                        notificationBuilder!!.setProgress(PROGRESS_MIN, PROGRESS_MIN, false)
                        notify(1, notificationBuilder!!.build())
                    }
                }
        startForeground(1, notificationBuilder!!.build())
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        with(notificationManager) {
            createNotificationChannel(channel)
        }
        return channelId
    }


    companion object {
        const val PROGRESS_MAX = 100
        const val PROGRESS_MIN = 0
        var PROGRESS = "progress"
    }
}
