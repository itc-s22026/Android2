package jp.ac.it_college.std.s22026.servicesample

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val CHANNEL_ID = "sound_manager_service_notification_channel"

class SoundManageService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer()

        // 以下、通知のための設定
        val name = getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mediaFileUri = Uri.parse(
//            "android.resource://${packageName}/${R.raw.giant_robot_factory_short}"
            "android.resource://${packageName}/${R.raw.zidai}"
        )
        mediaPlayer?.run {
            setDataSource(this@SoundManageService, mediaFileUri)
            setOnPreparedListener { onMediaPlayerPrepared() }
            setOnCompletionListener { onPlaybackEnd() }
            prepareAsync()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.run {
            if (isPlaying) {
                stop()
            }
        }
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    private fun onMediaPlayerPrepared() {
        mediaPlayer?.start()
        // 通知送る準備
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).run {
            setSmallIcon(android.R.drawable.ic_dialog_info)
            setContentTitle(getString(R.string.msg_notification_title_start))
            setContentText(getString(R.string.msg_notification_text_start))
            val intent = Intent(this@SoundManageService, MainActivity::class.java).apply {
                putExtra("fromNotification", true)
            }
            val stopServiceIntent = PendingIntent.getActivity(
                this@SoundManageService,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            setContentIntent(stopServiceIntent)
            setAutoCancel(true)
        }.build()
        startForeground(200, notification)
    }

    @SuppressLint("MissingPermission")
    private fun onPlaybackEnd() {
        // 通知送る準備
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).run {
            setSmallIcon(android.R.drawable.ic_dialog_info)
            setContentTitle(getString(R.string.msg_notification_title_finish))
            setContentText(getString(R.string.msg_notification_text_finish))
        }.build()
        with(NotificationManagerCompat.from(this)) {
            notify(100, notification)
        }
        stopSelf()
    }
}