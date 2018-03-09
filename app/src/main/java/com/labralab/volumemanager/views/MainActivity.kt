package com.labralab.volumemanager.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.labralab.volumemanager.App
import com.labralab.volumemanager.R
import com.labralab.volumemanager.adapters.MainRecyclerViewAdapter
import com.labralab.volumemanager.models.DayParamsList
import com.labralab.volumemanager.repository.Repository
import com.labralab.volumemanager.views.dialogs.NewDayDialog
import com.labralab.volumemanager.volumeManager.VolumeManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var lists: MutableList<DayParamsList>
    private lateinit var adapter: MainRecyclerViewAdapter

    @Inject
    lateinit var repository: Repository
    @Inject
    lateinit var volumeManager: VolumeManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Injecting
        App.appComponents.inject(this)

        setSupportActionBar(toolbar)

        //get data
        lists = repository!!.getDayList()

        //first hint
        showHint()

        val layoutManager = LinearLayoutManager(this)
        adapter = MainRecyclerViewAdapter(lists, this)

        mainRecyclerView.layoutManager = layoutManager
        mainRecyclerView.adapter = adapter
        mainRecyclerView.addItemDecoration(DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL))

        fab.setOnClickListener {

            //creating new paramsList
            val newDayDialog = NewDayDialog()
            newDayDialog.show(this.supportFragmentManager, "TAG")
        }
    }


    override fun onResume() {
        super.onResume()
        adapterNotifyDataSetChanged()
    }

    //update data
    fun adapterNotifyDataSetChanged() {

        val repository = Repository()
        lists = repository.getDayList()
        adapter!!.items = lists
        adapter!!.notifyDataSetChanged()
        showHint()

    }

    //first hint
    private fun showHint() {

        if (lists.isEmpty()) {
            Toast.makeText(applicationContext, "Нажмите на \"+\" для сохдания списка настроек", Toast.LENGTH_SHORT)
                    .show()
        }
    }
}
