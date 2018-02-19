package com.labralab.volumemanager.adapters

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.labralab.volumemanager.R
import com.labralab.volumemanager.models.DayParamsList
import com.labralab.volumemanager.models.VolumeManager
import com.labralab.volumemanager.models.VolumeParams
import com.labralab.volumemanager.utils.TimeUtil
import com.labralab.volumemanager.views.DayActivity
import com.labralab.volumemanager.views.fragments.NewParamsFragment
import io.realm.Realm
import java.util.*

/**
 * Created by pc on 11.12.2017.
 */
class ParamsRecyclerViewAdapter(items: List<VolumeParams>, state: Boolean) : RecyclerView.Adapter<ParamsRecyclerViewHolder>() {

    var items: List<VolumeParams> = ArrayList()
    var realm: Realm? = null
    var state: Boolean? = false

    init {

        this.items = items.sortedWith(compareBy({ it.stopHours }, { it.stopMinutes }))
        this.state = state
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParamsRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.params_maket, parent, false)
        return ParamsRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParamsRecyclerViewHolder, position: Int) {

        val item = items[position]
        holder.position.text = Integer.toString(position + 1) + "."
        holder.title.text = item.title

        val stopMinFormat = String.format("%02d", item.stopMinutes)
        val startMinFormat = String.format("%02d", item.startMinutes)
        holder.timeStart.text = "${item.startHours}:$startMinFormat"
        holder.timeStop.text = "${item.stopHours}:$stopMinFormat"

        if (state!!) {

            val currentPos = TimeUtil.getNearestTime(items)
            if (position == currentPos) {

                holder.position.setTextColor(holder.itemView.context.resources.getColor(R.color.colorAccent))
                holder.title.setTextColor(holder.itemView.context.resources.getColor(R.color.colorAccent))
                holder.timeStart.setTextColor(holder.itemView.context.resources.getColor(R.color.colorAccent))
                holder.timeStop.setTextColor(holder.itemView.context.resources.getColor(R.color.colorAccent))
            }
            if (position < currentPos) {
                holder.position.setTextColor(holder.itemView.context.resources.getColor(R.color.done))
                holder.title.setTextColor(holder.itemView.context.resources.getColor(R.color.done))
                holder.timeStart.setTextColor(holder.itemView.context.resources.getColor(R.color.done))
                holder.timeStop.setTextColor(holder.itemView.context.resources.getColor(R.color.done))

            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }


}

class ParamsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

    var realm: Realm? = null
    var vm: VolumeManager? = null

    var title: TextView
    var position: TextView
    var timeStart: TextView
    var timeStop: TextView

    init {

        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
        title = itemView.findViewById<View>(R.id.paramsTitle) as TextView
        position = itemView.findViewById<View>(R.id.position) as TextView
        timeStart = itemView.findViewById<View>(R.id.timeStart) as TextView
        timeStop = itemView.findViewById<View>(R.id.timeStop) as TextView
        realm = Realm.getDefaultInstance()
    }

    override fun onClick(view: View) {

        val bundle = Bundle()
        bundle.putString("title", title.text.toString())
        var newParamsFragment = NewParamsFragment()
        newParamsFragment.arguments = bundle

        var dayActivity: DayActivity = view.context as DayActivity
        dayActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.dayContainer, newParamsFragment)
                .addToBackStack(null)
                .commit()
    }

    override fun onLongClick(p0: View?): Boolean {


        val dayActivity = p0!!.context as DayActivity
        val day = dayActivity.dayParamList


        val builder = AlertDialog.Builder(p0!!.context)
        builder.setMessage("Удалить ${title.text}?")
        builder.setPositiveButton("Да", { dialog, _ ->

            realm!!.beginTransaction()
            day!!.getVolumeParamsItem(title.text.toString())!!.deleteFromRealm()
            realm!!.commitTransaction()

            var activity: DayActivity = p0.context as DayActivity
            activity.dayFragment!!.onStart()

            returnState(day, p0.context)

            dialog.cancel()
        })

        builder.setNegativeButton("Нет", { dialog, _ ->
            dialog.cancel()
        })
        val removeDialog = builder.create()
        removeDialog.show()

        return true
    }

    private fun returnState(day: DayParamsList, context: Context) {

        vm = VolumeManager(context)
        if (day.state!!) {
            vm!!.cancelAlarm()
            if (!day.paramsList!!.isEmpty()) {
                vm!!.startAlarmManager(day)
            }
        }
    }
}