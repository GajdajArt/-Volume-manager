package com.labralab.volumemanager.utils

import android.os.Bundle
import com.labralab.volumemanager.models.DayParamsList
import com.labralab.volumemanager.models.VolumeParams
import java.util.*

/**
 * Created by pc on 08.02.2018.
 */

class TimeUtil {

    companion object {

        const val MONDAY = 1
        const val TUESDAY = 2
        const val WEDNESDAY = 3
        const val THURSDAY = 4
        const val FRIDAY = 5
        const val SATURDAY = 6
        const val SUNDAY = 7

        const val TORN_OFF = 0
        const val TORN_ON = 1


        fun getNearestTime(params: List<VolumeParams>): Int {


            var startPos = -1
            var stopPos = -1

            val startTime = Calendar.getInstance()
            val stopTime = Calendar.getInstance()

            val c = Calendar.getInstance()
            c.timeInMillis = System.currentTimeMillis()
            val nawTime = Calendar.getInstance()

            nawTime.timeInMillis = 0
            nawTime.add(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY))
            nawTime.add(Calendar.MINUTE, c.get(Calendar.MINUTE))


            var sortParams = params.sortedWith(compareBy({ it.stopHours }, { it.stopMinutes }))


            for (it in sortParams) {

                stopTime.timeInMillis = 0
                stopTime.add(Calendar.HOUR, it.stopHours)
                stopTime.add(Calendar.MINUTE, it.stopMinutes)

                if (stopTime.after(nawTime)) {
                    stopPos = params.indexOf(it)
                    return stopPos
                }
            }


            if (stopPos == -1) {
                return params.indexOf(sortParams[0])
            }

            return stopPos
        }

        fun getDiff(volParams: VolumeParams?): Bundle {

            var b = Bundle()

            val date = Date()

            var interval: Long
            var state: Int


            var nawTime = Calendar.getInstance()
            var startTime = Calendar.getInstance()
            var stopTime = Calendar.getInstance()

            startTime.time = date
            startTime.set(Calendar.HOUR_OF_DAY, 0)
            startTime.set(Calendar.MINUTE, 0)
            startTime.set(Calendar.SECOND, 0)
            startTime.set(Calendar.MILLISECOND, 0)

            stopTime.time = date
            stopTime.set(Calendar.HOUR_OF_DAY, 0)
            stopTime.set(Calendar.MINUTE, 0)
            stopTime.set(Calendar.SECOND, 0)
            stopTime.set(Calendar.MILLISECOND, 0)

            nawTime.timeInMillis = System.currentTimeMillis()

            startTime.add(Calendar.HOUR_OF_DAY, volParams!!.startHours)
            startTime.add(Calendar.MINUTE, volParams!!.startMinutes)

            stopTime.add(Calendar.HOUR_OF_DAY, volParams!!.stopHours)
            stopTime.add(Calendar.MINUTE, volParams!!.stopMinutes)





            if (startTime < nawTime) {
                interval = stopTime.timeInMillis
                state = TORN_OFF

            } else {
                interval = startTime.timeInMillis
                state = TORN_ON
            }

            if (stopTime < nawTime) {
                startTime.add(Calendar.HOUR_OF_DAY, 24)
                interval = startTime.timeInMillis
                state = TORN_ON
            }

            b.putLong("interval", interval)
            b.putInt("state", state)

            return b
        }


        fun getState(volParams: VolumeParams?): Int {

            val date = Date()

            var state: Int

            var nawTime = Calendar.getInstance()
            var startTime = Calendar.getInstance()
            var stopTime = Calendar.getInstance()

            startTime.time = date
            startTime.set(Calendar.HOUR_OF_DAY, 0)
            startTime.set(Calendar.MINUTE, 0)
            startTime.set(Calendar.SECOND, 0)
            startTime.set(Calendar.MILLISECOND, 0)

            stopTime.time = date
            stopTime.set(Calendar.HOUR_OF_DAY, 0)
            stopTime.set(Calendar.MINUTE, 0)
            stopTime.set(Calendar.SECOND, 0)
            stopTime.set(Calendar.MILLISECOND, 0)

            nawTime.time = date


            startTime.add(Calendar.HOUR_OF_DAY, volParams!!.startHours)
            startTime.add(Calendar.MINUTE, volParams!!.startMinutes)

            stopTime.add(Calendar.HOUR_OF_DAY, volParams!!.stopHours)
            stopTime.add(Calendar.MINUTE, volParams!!.stopMinutes)

            if (startTime < nawTime) {
                state = TORN_OFF
            } else {
                state = TORN_ON
            }

            if (stopTime < nawTime) {
                state = TORN_ON
            }
            return state
        }

        fun isTheDayOfWeek(dayParamsList: DayParamsList): Boolean{

            var isTheDay = false

            val myDate = Calendar.getInstance()
            val dow = myDate.get(Calendar.DAY_OF_WEEK)

            var dayOfWeek = if(dow == 1){
                7
            }else{
                dow - 1
            }


            for (day in dayParamsList!!.dayOfWeekList!!) {
                if (day == dayOfWeek) {
                    isTheDay = true
                }
            }

            return isTheDay

        }
    }
}