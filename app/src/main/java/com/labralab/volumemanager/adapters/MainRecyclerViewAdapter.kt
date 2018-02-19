package com.labralab.volumemanager.adapters

import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.labralab.volumemanager.R
import com.labralab.volumemanager.models.DayParamsList
import com.labralab.volumemanager.views.DayActivity
import com.labralab.volumemanager.views.MainActivity
import io.realm.Realm
import java.util.*


class MainRecyclerViewAdapter(items: List<DayParamsList>, mainActivity: MainActivity) : RecyclerView.Adapter<MainRecyclerViewHolder>() {

    var items: List<DayParamsList> = ArrayList()
    var realm: Realm? = null
    var mainActivity: MainActivity? = null

    init {
        this.items = items
        this.mainActivity = mainActivity
        realm = Realm.getDefaultInstance()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_rv_item, parent, false)
        return MainRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainRecyclerViewHolder, position: Int) {

        val item = items[position]
        holder.position.text = Integer.toString(position + 1) + "."
        holder.title.text = item.title
        holder.isRunning.isChecked = item.state!!

        if (!item.paramsList!!.isEmpty()) {

            holder.isRunning.setOnCheckedChangeListener { _, b ->


                realm!!.executeTransaction({ _ ->

                    item.state = b

                    if (b) {
                        mainActivity!!.volumeManager!!.cancelAlarm()
                        mainActivity!!.volumeManager!!.checkTheSate(item)
                        mainActivity!!.volumeManager!!.setDefaultParams()
                        mainActivity!!.volumeManager!!.startAlarmManager(item)

                        Toast.makeText(mainActivity, "${item.title} запущен", Toast.LENGTH_SHORT).show()

                    } else {
                        mainActivity!!.volumeManager!!.cancelAlarm()

                        Toast.makeText(mainActivity, "${item.title} отключен", Toast.LENGTH_SHORT).show()
                    }

                    if (items.size > 1) {
                        if (b) {
                            for (day in items) {

                                if (item.title != day.title) {
                                    day.state = false
                                }
                            }
                            this@MainRecyclerViewAdapter.notifyDataSetChanged()
                        }
                    }
                })
            }

        } else {
            Toast.makeText(mainActivity, "Заполните распорядок дня", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}

class MainRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {


    var title: TextView
    var position: TextView
    var isRunning: Switch

    init {

        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
        title = itemView.findViewById<View>(R.id.dayTitle) as TextView
        position = itemView.findViewById<View>(R.id.position) as TextView
        isRunning = itemView.findViewById<View>(R.id.isRunning) as Switch
    }

    override fun onClick(view: View) {

        val intent = Intent(view.context, DayActivity::class.java)
        intent.putExtra("title", title.text.toString())
        view.context.startActivity(intent)

    }

    override fun onLongClick(p0: View?): Boolean {

        val builder = AlertDialog.Builder(p0!!.context)
        builder.setMessage("Удалить ${title.text}?")
        builder.setPositiveButton("Да", { dialog, _ ->
            removeDay(title.text.toString())

            var activity: MainActivity = p0.context as MainActivity
            activity.adapterNotifyDataSetChanged()

            dialog.cancel()
        })

        builder.setNegativeButton("Нет", { dialog, _ ->
            dialog.cancel()
        })
        val removeDialog = builder.create()
        removeDialog.show()

        return true
    }

    private fun removeDay(title: String) {
        DayParamsList.remove(title)
    }
}
