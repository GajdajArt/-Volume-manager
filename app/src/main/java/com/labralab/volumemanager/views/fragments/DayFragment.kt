package com.labralab.volumemanager.views.fragments


import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import com.labralab.volumemanager.App
import com.labralab.volumemanager.R
import com.labralab.volumemanager.adapters.ParamsRecyclerViewAdapter
import com.labralab.volumemanager.models.VolumeParams
import com.labralab.volumemanager.repository.Repository
import com.labralab.volumemanager.utils.TimeUtil
import com.labralab.volumemanager.views.DayActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.days.*
import kotlinx.android.synthetic.main.fragment_day.*
import javax.inject.Inject


class DayFragment : Fragment() {

    lateinit var title: String

    private lateinit var dayActivity: DayActivity

    private lateinit var lists: ArrayList<VolumeParams>
    private lateinit var adapter: ParamsRecyclerViewAdapter

    @Inject
    lateinit var repository: Repository
    @Inject
    lateinit var realm: Realm


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        App.appComponents.inject(this)
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_day, container, false)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()


        //create CheckBoxes for days of week
        createCheckBoxes(mnCB, TimeUtil.MONDAY)
        createCheckBoxes(tuCB, TimeUtil.TUESDAY)
        createCheckBoxes(weCB, TimeUtil.WEDNESDAY)
        createCheckBoxes(thCB, TimeUtil.THURSDAY)
        createCheckBoxes(frCB, TimeUtil.FRIDAY)
        createCheckBoxes(saCB, TimeUtil.SATURDAY)
        createCheckBoxes(suCB, TimeUtil.SUNDAY)

        dayActivity = (activity as DayActivity?)!!


        if (this.arguments != null) {

            title = arguments.getString("title")


            lists = ArrayList()
            lists.addAll(dayActivity!!.dayParamList!!.paramsList!!)
            val state = dayActivity!!.dayParamList!!.state

            setDaysOfWeek()

            //Show hint
            showHint()

            val layoutManager = LinearLayoutManager(dayActivity)
            adapter = ParamsRecyclerViewAdapter(lists, state!!)

            paramRecyclerView.layoutManager = layoutManager
            paramRecyclerView.adapter = adapter
            paramRecyclerView.addItemDecoration(DividerItemDecoration(dayActivity,
                    LinearLayoutManager.VERTICAL))

            newParamsFab.setOnClickListener {

                var newParamsFragment = NewParamsFragment()

                dayActivity!!.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.dayContainer, newParamsFragment)
                        .addToBackStack(null)
                        .commit()
            }

        }
    }

    //Show hint
    private fun showHint() {

        if (lists.isEmpty()) {
            Toast.makeText(activity, "Нажмите на \"+\" для добавления настроек", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun setDaysOfWeek(){

        var checkBoxList:  ArrayList<CheckBox> = ArrayList()
        checkBoxList.add(mnCB)
        checkBoxList.add(tuCB)
        checkBoxList.add(weCB)
        checkBoxList.add(thCB)
        checkBoxList.add(frCB)
        checkBoxList.add(saCB)
        checkBoxList.add(suCB)

        for(day in  dayActivity!!.dayParamList!!.dayOfWeekList!!){

            checkBoxList[day - 1].isChecked = true

        }
    }

    private fun createCheckBoxes(checkBox: CheckBox, day: Int) {

        checkBox.setOnClickListener {
            realm.executeTransaction {

                if (checkBox.isChecked) {

                    dayActivity!!.dayParamList!!.dayOfWeekList!!.add(day)

                } else {

                    dayActivity!!.dayParamList!!.dayOfWeekList!!.remove(day)

                }
            }
        }
    }

}// Required empty public constructor

