package com.labralab.volumemanager.volumeManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.labralab.volumemanager.repository.Repository

/**
 * Created by pc on 12.02.2018.
 */

class TestReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val pos = intent.getIntExtra("pos", 0)
        val state = intent.getIntExtra("state", 0)
        val title = intent.getStringExtra("title")
        val repository = Repository()
        val day = repository.getDay(title)
        val volumeParams = day.paramsList!![pos]
        val volumeManager = VolumeManager(context)

        volumeManager.setParams(volumeParams!!, state)
        volumeManager.startAlarmManager(day)
        volumeManager.showNotification(volumeParams.title, state)

    }
}
