package com.labralab.volumemanager.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.labralab.volumemanager.R
import com.labralab.volumemanager.adapters.MainRecyclerViewAdapter
import com.labralab.volumemanager.models.DayParamsList
import com.labralab.volumemanager.models.Repository
import com.labralab.volumemanager.models.VolumeManager
import com.labralab.volumemanager.views.dialogs.NewDayDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var lists: MutableList<DayParamsList> = ArrayList()
    var adapter: MainRecyclerViewAdapter? = null
    var repository: Repository? = Repository()
    var volumeManager: VolumeManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        volumeManager = VolumeManager(applicationContext)

        lists = repository!!.getDayList()

        val layoutManager = LinearLayoutManager(this)
        adapter = MainRecyclerViewAdapter(lists, this)

        mainRecyclerView.layoutManager = layoutManager
        mainRecyclerView.adapter = adapter
        mainRecyclerView.addItemDecoration(DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL))

        fab.setOnClickListener {
            val newDayDialog = NewDayDialog()
            newDayDialog.show(this.supportFragmentManager, "TAG")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    fun adapterNotifyDataSetChanged(){

        val repository = Repository()

        adapter!!.items = repository.getDayList()
        adapter!!.notifyDataSetChanged()
    }
}
