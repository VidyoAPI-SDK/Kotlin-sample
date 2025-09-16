package com.vidyo.vidyoconnector.bl.connector.conference

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import com.vidyo.vidyoconnector.App
import com.vidyo.vidyoconnector.R
import com.vidyo.vidyoconnector.appContext
import com.vidyo.vidyoconnector.bl.connector.ConnectorManager
import com.vidyo.vidyoconnector.ui.MainActivity
import com.vidyo.vidyoconnector.utils.coroutines.collectInScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

private const val NOTIF_ID = 1
class ConferenceService : Service() {
    companion object {
        private const val CHANNEL_ID = "notification_channel_id"
        val ACTION_STOP = "${appContext.packageName}.stop"
    }

    private val manager by lazy { NotificationManagerCompat.from(this) }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val scope = CoroutineScope(Dispatchers.Main.immediate)


    override fun onCreate() {
        super.onCreate()
        (application as App).isMediaProjectionSet.collectInScope(scope) {
            it?.let { isSet ->
                createNotificationAndStartService(isSet)
            }
        }
        createNotificationChannel()
        createNotificationAndStartService(false)
    }

    /**
     * By default service starts with no media projection, since media projection permission set by user
     * during conference call.
     * @param isMediaProjectionSet Boolean
     */
    private fun createNotificationAndStartService(isMediaProjectionSet : Boolean){
        Log.d("ConferenceService", "createNotificationAndStartService isMediaProjectionSet: $isMediaProjectionSet")
        val notification = createAndGetNotification()
        callStartForeground(notification, isMediaProjectionSet)
    }

    /**
     * Start foreground service according to isMediaProjectionSet true or false.
     * @param notification Notification
     * @param isMediaProjectionSet Boolean
     */
    private fun callStartForeground(notification: Notification, isMediaProjectionSet: Boolean) {

        if(isMediaProjectionSet){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Log.d("ConferenceService", "start FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION or FOREGROUND_SERVICE_TYPE_MICROPHONE or FOREGROUND_SERVICE_TYPE_CAMERA")

                    startForeground(
                        NOTIF_ID,
                        notification,
                        FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                                or FOREGROUND_SERVICE_TYPE_MICROPHONE or FOREGROUND_SERVICE_TYPE_CAMERA
                    )
                } else {
                    Log.d("ConferenceService", "start FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION")

                    startForeground(
                        NOTIF_ID,
                        notification,
                        FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
                }
            } else {
                Log.d("ConferenceService", "start NA")
                startForeground(NOTIF_ID, notification)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Log.d("ConferenceService", "start FOREGROUND_SERVICE_TYPE_MICROPHONE or FOREGROUND_SERVICE_TYPE_CAMERA")

                    startForeground(
                        NOTIF_ID,
                        notification, FOREGROUND_SERVICE_TYPE_MICROPHONE or FOREGROUND_SERVICE_TYPE_CAMERA)
                }else{
                    Log.d("ConferenceService", "start NA")
                    startForeground(NOTIF_ID, notification)
                }
            }else {
                Log.d("ConferenceService", "start NA")
                startForeground(NOTIF_ID, notification)
            }
        }
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

    private fun createAndGetNotification() : Notification {
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

        return notification
    }
}
