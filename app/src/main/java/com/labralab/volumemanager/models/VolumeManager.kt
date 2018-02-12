package com.labralab.volumemanager.models

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import com.labralab.volumemanager.R
import com.labralab.volumemanager.utils.TimeUtil
import com.labralab.volumemanager.views.MainActivity
import io.realm.Realm
import java.util.*


class VolumeManager(var context: Context? = null) : BroadcastReceiver() {


    //    var context: Context? = null
    private var am: AlarmManager? = null
    var realm: Realm = Realm.getDefaultInstance()
    private var repository: Repository? = null
    private var pi: PendingIntent? = null

    companion object {

        const val TORN_OFF = 0
        const val TORN_ON = 1
    }

    init {

//        this.context = context
        this.repository = Repository()
        am = this.context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val pos = intent!!.getIntExtra("pos", 0)
        val title = intent!!.getStringExtra("title")
        val state = intent!!.getIntExtra("state", 1)

        val day = repository!!.getDay(title)
        showNotification(title, day.paramsList!![pos]!!.title)
        startAlarmManager(day)

    }


    @SuppressLint("ServiceCast")
    fun setParams(params: VolumeParams) {

        var mgr: AudioManager = this.context!!.getSystemService(Context.ACCOUNT_SERVICE) as AudioManager

        mgr.setStreamVolume(AudioManager.STREAM_SYSTEM, params.systemLevel, AudioManager.FLAG_PLAY_SOUND)
        mgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION, params.notificationLevel, AudioManager.FLAG_PLAY_SOUND)
        mgr.setStreamVolume(AudioManager.STREAM_RING, params.ringLevel, AudioManager.FLAG_PLAY_SOUND)
        mgr.setStreamVolume(AudioManager.STREAM_VOICE_CALL, params.voiceCallLevel, AudioManager.FLAG_PLAY_SOUND)
    }

    fun startAlarmManager(day: DayParamsList) {

        var state: Int

        var pos = TimeUtil.getNearestTime(day.paramsList!!)
        var volParams = day!!.paramsList!![pos]

        var interval = TimeUtil.getDiff(volParams)


        var intent = Intent("volumeManager")
        intent.putExtra("title", day.title)
        intent.putExtra("pos", pos)
        intent.putExtra("state", TimeUtil.getState(volParams))

        pi = PendingIntent.getBroadcast(this.context, 0, intent, 0)


//Test
        val c = Calendar.getInstance()
        c.timeInMillis = System.currentTimeMillis()
        c.add(Calendar.SECOND, 2)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am!!.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pi)
        } else {
            am!!.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, pi)
        }
    }

    fun cancelAlarm() {


        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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

    fun setDefaultParams() {

        var def: VolumeParams = VolumeParams()
        repository!!.setDefaultParams(def)
    }

    fun showNotification(dayTitle: String, paramsTitle: String) {

        val notificationIntent = Intent(this.context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this.context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT)

        val res = this.context!!.resources
        val builder = Notification.Builder(this.context)

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.notification_icon_background)
                // большая картинк

                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker("Последнее китайское предупреждение!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle(dayTitle)
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText(paramsTitle) // Текст уведомления

        // Notification notification = builder.getNotification(); // до API 16
        var notification: Notification? = null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build()
        }

        val notificationManager = this.context!!
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}