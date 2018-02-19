package com.labralab.volumemanager.views.fragments


import android.app.AlertDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.labralab.volumemanager.R
import com.labralab.volumemanager.models.VolumeParams
import com.labralab.volumemanager.views.DayActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_new_params.*
import java.util.*


class NewParamsFragment : Fragment() {

    private var oldTitle: String? = null

    private var etTitle: EditText? = null
    private var etStart: EditText? = null
    private var etStop: EditText? = null
    private var dayActivity: DayActivity? = null
    private var realm = Realm.getDefaultInstance()

    private var startHours: Int = 14
    private var startMinutes: Int = 35
    private var stopHours: Int = 14
    private var stopMinutes: Int = 36

    private var hint: String = ""


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_new_params, container, false)
    }

    override fun onStart() {
        super.onStart()

        dayActivity = activity as DayActivity?

        etTitle = tILParamsTitle.editText as EditText
        etStart = tILStartTime.editText as EditText
        etStop = tILStopTime.editText as EditText


        val mgr: AudioManager = this.context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        notificationLevel.max = mgr.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
        ringLevel.max = mgr.getStreamMaxVolume(AudioManager.STREAM_RING)
        systemLevel.max = mgr.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)
        musicLevel.max = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        if (this.arguments != null) {

            oldTitle = arguments.getString("title")
            var volumeParams = dayActivity!!.dayParamList!!.getVolumeParamsItem(oldTitle!!)

            startHours = volumeParams!!.startHours
            startMinutes = volumeParams.startMinutes
            stopHours = volumeParams.stopHours
            stopMinutes = volumeParams.stopMinutes

            val stopMinFormat = String.format("%02d", stopMinutes)
            val startMinFormat = String.format("%02d", startMinutes)

            etTitle!!.setText(volumeParams!!.title)
            etStart!!.setText("$startHours : $startMinFormat")
            etStop!!.setText("$stopHours : $stopMinFormat")


            notificationLevel.progress = volumeParams!!.notificationLevel
            ringLevel.progress = volumeParams!!.ringLevel
            systemLevel.progress = volumeParams!!.systemLevel
            musicLevel.progress = volumeParams!!.musicLevel

        }

        tILStartTime.editText!!.setOnTouchListener { _, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_UP) {
                etStart!!.inputType = InputType.TYPE_NULL
                val tpd = TimePickerDialog(dayActivity, startCallBack, startHours, startMinutes, true)
                tpd.show()
            }
            true
        }

        tILStopTime.editText!!.setOnTouchListener { _, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_UP) {
                etStart!!.inputType = InputType.TYPE_NULL
                val tpd = TimePickerDialog(dayActivity, stopCallBack, stopHours, stopMinutes, true)

                tpd.show()
            }
            true
        }


        paramsDoneFab.setOnClickListener {

            if (!etStart!!.text.isEmpty() && !etStop!!.text.isEmpty() && !etTitle!!.text.isEmpty()) {

                if (isTimeImposition()) {


                    realm.executeTransaction({ _ ->

                        //TEST
                        val c = Calendar.getInstance()
                        c.timeInMillis = System.currentTimeMillis()


                        val volumeParams = VolumeParams()
                        volumeParams.title = etTitle!!.text.toString()
//
//                        volumeParams.startHours = c.get(Calendar.HOUR_OF_DAY)
//                        volumeParams.startMinutes = c.get(Calendar.MINUTE) + 1
//                        volumeParams.stopHours = c.get(Calendar.HOUR_OF_DAY)
//                        volumeParams.stopMinutes = c.get(Calendar.MINUTE) + 2
                        volumeParams.startHours = startHours
                        volumeParams.startMinutes = startMinutes
                        volumeParams.stopHours = stopHours
                        volumeParams.stopMinutes = stopMinutes
                        volumeParams.notificationLevel = notificationLevel.progress
                        volumeParams.ringLevel = ringLevel.progress
                        volumeParams.systemLevel = systemLevel.progress
                        volumeParams.musicLevel = musicLevel.progress
                        volumeParams.isVibration = vibrationCB.isChecked

                        if (this.arguments == null) {
                            dayActivity!!.dayParamList!!.paramsList!!.add(volumeParams)
                        } else {
                            var item = dayActivity!!.dayParamList!!.getVolumeParamsItem(oldTitle!!)
                            dayActivity!!.dayParamList!!.paramsList!!.remove(item)
                            dayActivity!!.dayParamList!!.paramsList!!.add(volumeParams)
                        }

                        dayActivity!!.supportFragmentManager.popBackStack()

                    })


                } else {
                    showDialog()
                }
            } else {
                if (etTitle!!.text.isEmpty()) {
                    Toast.makeText(activity, "Введите название", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Введите время", Toast.LENGTH_SHORT).show()
                }

            }

        }

    }

    private fun getMinutes(hours: Int, minutes: Int): Int {
        return hours * 60 + minutes
    }

    private var startCallBack: OnTimeSetListener = OnTimeSetListener { _, hourOfDay, minute ->
        startHours = hourOfDay
        startMinutes = minute
        val startMinFormat = String.format("%02d", startMinutes)

        etStart!!.setText("$startHours : $startMinFormat")
    }

    private var stopCallBack: OnTimeSetListener = OnTimeSetListener { _, hourOfDay, minute ->
        stopHours = hourOfDay
        stopMinutes = minute
        val stopMinFormat = String.format("%02d", minute)

        etStop!!.setText("$stopHours : $stopMinFormat")
    }

    private fun isTimeImposition(): Boolean {

        val paramsList = dayActivity!!.dayParamList!!.paramsList
        val startInMinutes = getMinutes(startHours, startMinutes)
        val stopInMinutes = getMinutes(stopHours, stopMinutes)

        paramsList!!
                .forEach {

                    val itStartInMin = getMinutes(it.startHours, it.startMinutes)
                    val itStopInMin = getMinutes(it.stopHours, it.stopMinutes)

                    if (startInMinutes in (itStartInMin + 1)..(itStopInMin - 1)) {
                        hint = "указанный период накладывается на период парамтра \"${it.title}\"" +
                                " (${it.startHours} : ${it.startMinutes} - ${it.stopHours} : ${it.stopMinutes})"
                        return false

                    }

                    if (stopInMinutes in (itStartInMin + 1)..(itStopInMin - 1)) {
                        hint = "Указанный период накладывается на период парамтра \"${it.title}\"" +
                                " (${it.startHours} : ${it.startMinutes} - ${it.stopHours} : ${it.stopMinutes})"
                        return false
                    }
                }

        return true
    }

    private fun showDialog() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("Упс!")
                .setMessage(hint)
                .setCancelable(false)
                .setPositiveButton("Исправить", { dialog, _ ->
                    dialog.cancel()

                })
        builder.show()
    }

}// Required empty public constructor
