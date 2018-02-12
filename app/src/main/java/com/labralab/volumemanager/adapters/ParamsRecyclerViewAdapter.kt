package com.labralab.volumemanager.adapters

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.labralab.volumemanager.R
import com.labralab.volumemanager.models.DayParamsList
import com.labralab.volumemanager.models.VolumeParams
import com.labralab.volumemanager.views.DayActivity
import com.labralab.volumemanager.views.fragments.NewParamsFragment
import io.realm.Realm
import java.util.*

/**
 * Created by pc on 11.12.2017.
 */
class ParamsRecyclerViewAdapter (items: List<VolumeParams>) : RecyclerView.Adapter<ParamsRecyclerViewHolder>() {

    var items: List<VolumeParams> = ArrayList()
    var realm: Realm? = null

    init {
        this.items = items
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
        holder.time.text = "${item.startHours}:${item.startMinutes} - ${item.stopHours}:${item.stopMinutes}"

    }

    override fun getItemCount(): Int {
        return items.size
    }


}

class ParamsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {


    var title: TextView
    var position: TextView
    var time: TextView

    init {

        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
        title = itemView.findViewById<View>(R.id.paramsTitle) as TextView
        position = itemView.findViewById<View>(R.id.position) as TextView
        time = itemView.findViewById<View>(R.id.time) as TextView
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

//        val builder = AlertDialog.Builder(p0!!.context)
//        builder.setMessage("Удалить?")
//        builder.setPositiveButton("Да", { dialog, _ ->
//            removeDay(title.text.toString())
//
//            var activity: MainActivity = p0.context as MainActivity
//            activity.adapterNotifyDataSetChanged()
//
//            dialog.cancel()
//        })
//
//        builder.setNegativeButton("Нет", { dialog, _ ->
//            dialog.cancel()
//        })
//        val removeDialog = builder.create()
//        removeDialog.show()

        return true
    }

    private fun removeDay(title: String) {
        DayParamsList.remove(title)
    }
}