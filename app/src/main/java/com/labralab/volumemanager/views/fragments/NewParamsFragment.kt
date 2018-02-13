package com.labralab.volumemanager.views.fragments


import android.app.AlertDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
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


class NewParamsFragment : Fragment() {

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

        if (this.arguments != null) {

            var title = arguments.getString("title")
            var volumeParams = dayActivity!!.dayParamList!!.getVolumeParamsItem(title)

            etTitle!!.setText(volumeParams!!.title)
            notificationLevel.progress = volumeParams!!.notificationLevel
//            .....

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

                        val volumeParams = VolumeParams()
                        volumeParams.title = etTitle!!.text.toString()
                        volumeParams.startHours = startHours
                        volumeParams.startMinutes = startMinutes
                        volumeParams.stopHours = stopHours
                        volumeParams.stopMinutes = stopMinutes
                        volumeParams.notificationLevel = notificationLevel.progress
                        volumeParams.ringLevel = ringLevel.progress
                        volumeParams.voiceCallLevel = voiceCallLevel.progress
                        volumeParams.systemLevel = systemLevel.progress
                        volumeParams.musicLevel = musicLevel.progress
                        volumeParams.isVibration = vibrationCB.isChecked

                        dayActivity!!.dayParamList!!.paramsList!!.add(volumeParams)
//                dayActivity!!.dayFragment!!.adapter!!.notifyDataSetChanged()

                        dayActivity!!.supportFragmentManager.popBackStack()
                    })
                } else {
                    showDialog()
                }
            }else{
                if(etTitle!!.text.isEmpty()){
                    Toast.makeText(activity, "Введите название", Toast.LENGTH_SHORT).show()
                }else{
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
        etStart!!.setText("$startHours : $startMinutes")
    }

    private var stopCallBack: OnTimeSetListener = OnTimeSetListener { _, hourOfDay, minute ->
        stopHours = hourOfDay
        stopMinutes = minute
        etStop!!.setText("$stopHours : $stopMinutes")
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
