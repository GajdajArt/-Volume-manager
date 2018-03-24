package com.labralab.volumemanager.volumeManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.labralab.volumemanager.repository.Repository
import com.labralab.volumemanager.utils.TimeUtil

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

        if (TimeUtil.isTheDayOfWeek(day)) {

            volumeManager.setParams(volumeParams!!, state)
            volumeManager.showNotification(volumeParams.title, state)


            if (state == TimeUtil.TORN_OFF) {
                //if pos is not last position in paramsList
                if (day.paramsList!!.size > pos + 1) {

                    //if next paramsList starting when this paramsList stopping
                    if (day.paramsList!![pos]!!.stopHours == day.paramsList!![pos + 1]!!.startHours
                            && day.paramsList!![pos]!!.stopMinutes == day.paramsList!![pos + 1]!!.startMinutes) {

                        val nextVolParams = day.paramsList!![pos + 1]
                        volumeManager.setParams(nextVolParams!!, TimeUtil.TORN_ON)
                        volumeManager.showNotification(nextVolParams!!.title, TimeUtil.TORN_ON)
                    }
                }
            }
        }
        volumeManager.startAlarmManager(day)


    }
}
