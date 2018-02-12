package com.labralab.volumemanager.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.labralab.volumemanager.R
import com.labralab.volumemanager.models.DayParamsList
import com.labralab.volumemanager.views.fragments.DayFragment

class DayActivity : AppCompatActivity() {

    var dayFragment: DayFragment? = null
    var dayTitle: String? = null
    var dayParamList: DayParamsList? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        dayFragment = DayFragment()

        //Getting title from intent
        dayTitle = intent.getStringExtra("title")

        dayParamList = DayParamsList.getInstance(dayTitle!!)


        supportActionBar!!.title = dayTitle

        //Setting title to dayFragment
        val bundle = Bundle()
        bundle.putString("title", dayTitle)
        dayFragment!!.arguments = bundle




        //Start dayFragment
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                .add(R.id.dayContainer, dayFragment)
                .commit()


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
