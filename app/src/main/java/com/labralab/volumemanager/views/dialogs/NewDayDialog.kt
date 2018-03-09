package com.labralab.volumemanager.views.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import com.labralab.volumemanager.R
import com.labralab.volumemanager.models.DayParamsList
import com.labralab.volumemanager.repository.Repository
import com.labralab.volumemanager.views.MainActivity

/**
 * Created by pc on 25.11.2017.
 */

class NewDayDialog : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        //object for working with Dialog
        val inflater = activity.layoutInflater
        //find the layout.dialog_task and all the elements within it.
        val container = inflater.inflate(R.layout.new_day_dialog_maket, null)


        builder.setView(container)

        val tilTitle: TextInputLayout = container.findViewById<View>(R.id.tILTitle) as TextInputLayout
        var etTitle: EditText = tilTitle.editText as EditText


        //Creates button OK in the bottom of the dialog
        builder.setPositiveButton(getString(R.string.next)) { dialog, _ ->

            val title: String = etTitle.text.toString()
            val newDay = DayParamsList()
            newDay.title = title
//
//           var list =  ArrayList<VolumeParams>()
//
//            for (i: Int in 1..10) {
//                val volumeParams = VolumeParams()
//                volumeParams.title = "param $i"
//                list.add(volumeParams)
//            }
//
//            newDay.paramsList = RealmList()
//            newDay.paramsList!!.addAll(list)
            val rep = Repository()
            rep.createDay(newDay)

            var activity: MainActivity = activity as MainActivity
            activity.adapterNotifyDataSetChanged()

            dialog.cancel()

        }

        //Creates button CANCEL in the bottom of the dialog
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }

        return builder.create()

    }
}