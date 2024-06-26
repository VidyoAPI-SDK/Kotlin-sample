package com.vidyo.vidyoconnector.bl.connector.conference

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.appContext
import com.vidyo.vidyoconnector.bl.connector.ConnectorManager
import com.vidyo.vidyoconnector.ui.MainActivity

class ConferenceService : Service() {
    companion object {
        private const val CHANNEL_ID = "notification_channel_id"
        val ACTION_STOP = "${appContext.packageName}.stop"
    }

    private val manager by lazy { NotificationManagerCompat.from(this) }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        createNotification()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        ConnectorManager.conference.disconnect()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel() {
        val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, importance)
            .setName(getString(R.string.conferenceService_channelName))
            .setSound(null, null)
            .setLightsEnabled(false)
            .setVibrationEnabled(false)
            .build()

        manager.createNotificationChannel(channel)
    }

    private fun createNotification() {
        val activityIntent = Intent(this, MainActivity::class.java)
        val activityPending = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_conference)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.conferenceService_inProgress))
            .setContentIntent(activityPending)
            .build()

        startForeground(1, notification)
    }
}
