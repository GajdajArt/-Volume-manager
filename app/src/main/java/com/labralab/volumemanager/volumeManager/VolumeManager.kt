package com.labralab.volumemanager.volumeManager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.os.Build
import com.labralab.volumemanager.App
import com.labralab.volumemanager.R
import com.labralab.volumemanager.models.DayParamsList
import com.labralab.volumemanager.models.VolumeParams
import com.labralab.volumemanager.repository.Repository
import com.labralab.volumemanager.utils.TimeUtil
import com.labralab.volumemanager.views.MainActivity
import io.realm.Realm
import javax.inject.Inject


class VolumeManager(var context: Context? = null) {

    private var am: AlarmManager
    private lateinit var pi: PendingIntent

    @Inject
    lateinit var realm: Realm
    @Inject
    lateinit var repository: Repository


    init {
        App.appComponents.inject(this)
        am = this.context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    //Setting necessary params
    @SuppressLint("ServiceCast")
    fun setParams(params: VolumeParams, state: Int) {

        var mgr: AudioManager = this.context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        when (state) {
            TimeUtil.TORN_ON -> {

                mgr.ringerMode = if (params.isVibration) {
                    AudioManager.RINGER_MODE_VIBRATE
                } else {
                    AudioManager.RINGER_MODE_SILENT
                }
                mgr.setStreamVolume(AudioManager.STREAM_SYSTEM, params.systemLevel, AudioManager.FLAG_PLAY_SOUND)
                mgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION, params.notificationLevel, AudioManager.FLAG_PLAY_SOUND)
                mgr.setStreamVolume(AudioManager.STREAM_RING, params.ringLevel, AudioManager.FLAG_PLAY_SOUND)
                mgr.setStreamVolume(AudioManager.STREAM_MUSIC, params.musicLevel, AudioManager.STREAM_MUSIC)
            }

            TimeUtil.TORN_OFF -> {

                val default = repository!!.getDefaultParams()

                mgr.ringerMode = if (default!!.isVibration) {
                    AudioManager.RINGER_MODE_VIBRATE
                } else {
                    AudioManager.RINGER_MODE_SILENT
                }
                mgr.setStreamVolume(AudioManager.STREAM_SYSTEM, default!!.systemLevel, AudioManager.FLAG_PLAY_SOUND)
                mgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION, default!!.notificationLevel, AudioManager.FLAG_PLAY_SOUND)
                mgr.setStreamVolume(AudioManager.STREAM_RING, default!!.ringLevel, AudioManager.FLAG_PLAY_SOUND)
                mgr.setStreamVolume(AudioManager.STREAM_MUSIC, default!!.musicLevel, AudioManager.STREAM_MUSIC)

            }
        }
    }

    //Running alarm manager when it's necessary
    fun startAlarmManager(day: DayParamsList) {

        var pos = TimeUtil.getNearestTime(day.paramsList!!)
        var volParams = day!!.paramsList!![pos]
        val b = TimeUtil.getDiff(volParams)

        val interval = b.getLong("interval")
        val state = b.getInt("state")


        var intent = Intent("volumeManager")
        intent.putExtra("title", day.title)
        intent.putExtra("pos", pos)
        intent.putExtra("state", state)

        pi = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am!!.setExact(AlarmManager.RTC_WAKEUP, interval, pi)
        } else {
            am!!.set(AlarmManager.RTC_WAKEUP, interval, pi)
        }
    }

    //Cancel alarm
    fun cancelAlarm() {

        var intent = Intent("volumeManager")

        pi = PendingIntent.getBroadcast(this.context, 0, intent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am!!.setExact(AlarmManager.RTC_WAKEUP, 1, pi)
        } else {
            am!!.set(AlarmManager.RTC_WAKEUP, 1, pi)
        }

        // Cancel alarms
        try {
            am!!.cancel(pi)
        } catch (e: Exception) {
        }

    }

    //Setting default params when the other params are off
    fun setDefaultParams() {

        var mgr: AudioManager = this.context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        var def = VolumeParams()
        def.title = "default"
        def.musicLevel = mgr.getStreamVolume(AudioManager.STREAM_MUSIC)
        def.notificationLevel = mgr.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
        def.ringLevel = mgr.getStreamVolume(AudioManager.STREAM_RING)
        def.systemLevel = mgr.getStreamVolume(AudioManager.STREAM_SYSTEM)

        repository!!.setDefaultParams(def)
    }

    //Running notification
    fun showNotification(dayTitle: String, state: Int) {

        val stateStr = if (state == TimeUtil.TORN_ON) {
            "включен"
        } else {
            "выключен"
        }

        val notificationIntent = Intent(this.context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this.context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = Notification.Builder(this.context)

        builder.setContentIntent(contentIntent)
                .setLargeIcon(BitmapFactory.decodeResource(context!!.resources,
                        R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(context!!.getString(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Параметры звука изменены")
                .setContentText("$dayTitle $stateStr") // Текст уведомления

        var notification = builder.build()

        val notificationManager = this.context!!
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    //Method for checking the state
    fun checkTheSate(day: DayParamsList) {

        val pos = TimeUtil.getNearestTime(day.paramsList!!)
        val volParams = day!!.paramsList!![pos]
        val state = TimeUtil.getState(volParams)

        if (state == TimeUtil.TORN_OFF) {
            setParams(volParams!!, TimeUtil.TORN_ON)
            showNotification(day.title!!, TimeUtil.TORN_ON)
        }
    }
}