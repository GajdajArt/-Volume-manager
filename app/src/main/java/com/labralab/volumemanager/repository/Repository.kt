package com.labralab.volumemanager.repository

import com.labralab.volumemanager.App
import com.labralab.volumemanager.models.DayParamsList
import com.labralab.volumemanager.models.VolumeParams
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import javax.inject.Inject

/**
 * Created by pc on 26.11.2017.
 */
class Repository {

    @Inject
    lateinit var realm: Realm

    //Inject
    init {
        App.appComponents.inject(this)
    }


    //Create new day and write it down
    fun createDay(day: DayParamsList) {

        realm.beginTransaction()
        realm.insert(day)
        realm.commitTransaction()
    }

    //Get list of days from Realm
    fun getDayList(): ArrayList<DayParamsList> {

        val query: RealmQuery<DayParamsList> = realm.where(DayParamsList::class.java)
        val realmResult: RealmResults<DayParamsList> = query.findAll()

        var list: ArrayList<DayParamsList> = ArrayList()
        list.addAll(realmResult)

        return list
    }

    //Remove day from Realm
    fun removeDay(title: String) {

        realm.executeTransaction { realm ->
            val result = realm.where(DayParamsList::class.java)
                    .equalTo("title", title)
                    .findAll()
            result.deleteAllFromRealm()
        }
    }

    //Get the day from Realm
    fun getDay(title: String): DayParamsList {

        var dayParamsList = DayParamsList()

        realm.executeTransaction { realm ->
            val result: RealmResults<DayParamsList> = realm.where(DayParamsList::class.java)
                    .equalTo("title", title)
                    .findAll()
            dayParamsList = result.first()!!
        }

        return dayParamsList
    }


    //Saving current params to Realm
    fun setDefaultParams(volParams: VolumeParams) {


        val result: RealmResults<VolumeParams> = realm.where(VolumeParams::class.java)
                .equalTo("title", "default")
                .findAll()

        if (result.isEmpty()) {
            realm.insert(volParams)
        } else {
            var oldParams = result.first()

            oldParams!!.systemLevel = volParams!!.systemLevel
            oldParams!!.musicLevel = volParams!!.musicLevel
            oldParams!!.notificationLevel = volParams!!.notificationLevel
            oldParams!!.ringLevel = volParams!!.ringLevel
        }
    }

    //Get default params from Realm
    fun getDefaultParams(): VolumeParams? {

        val result: RealmResults<VolumeParams> = realm.where(VolumeParams::class.java)
                .equalTo("title", "default")
                .findAll()

        return result.first()

    }

}