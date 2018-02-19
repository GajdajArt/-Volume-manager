package com.labralab.volumemanager.views.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.labralab.volumemanager.R
import com.labralab.volumemanager.adapters.ParamsRecyclerViewAdapter
import com.labralab.volumemanager.models.VolumeParams
import com.labralab.volumemanager.views.DayActivity
import kotlinx.android.synthetic.main.fragment_day.*


class DayFragment : Fragment() {

    var title: String? = null
    var dayActivity: DayActivity? = null

    var lists: ArrayList<VolumeParams> = ArrayList()
    var adapter: ParamsRecyclerViewAdapter? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_day, container, false)
    }

    override fun onStart() {
        super.onStart()

        dayActivity = activity as DayActivity?


        if (this.arguments != null) {

            title = arguments.getString("title")

            lists.clear()
            lists.addAll(dayActivity!!.dayParamList!!.paramsList!!)
            val state = dayActivity!!.dayParamList!!.state

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
}// Required empty public constructor

