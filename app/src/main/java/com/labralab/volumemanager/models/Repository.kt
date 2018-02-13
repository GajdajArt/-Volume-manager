package com.labralab.volumemanager.models

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults

/**
 * Created by pc on 26.11.2017.
 */
class Repository() {

    val realm = Realm.getDefaultInstance()

    fun createDay (day: DayParamsList){

        realm.beginTransaction()
        realm.insert(day)
        realm.commitTransaction()
    }

    fun getDayList(): ArrayList<DayParamsList> {

        val query: RealmQuery<DayParamsList> = realm.where(DayParamsList::class.java)
        val realmResult: RealmResults<DayParamsList> = query.findAll()

        var list: ArrayList<DayParamsList> = ArrayList()
        list.addAll(realmResult)

        return list
    }

    fun removeDay(title: String){

        realm.executeTransaction { realm ->
            val result = realm.where(DayParamsList::class.java)
                    .equalTo("title", title)
                    .findAll()
            result.deleteAllFromRealm()
        }
    }

    fun getDay(title: String): DayParamsList{

        var dayParamsList = DayParamsList()

        realm.executeTransaction { realm ->
            val result: RealmResults<DayParamsList> = realm.where(DayParamsList::class.java)
                    .equalTo("title", title)
                    .findAll()
            dayParamsList = result.first()!!
        }

        return dayParamsList
    }

    fun setDefaultParams(volParams: VolumeParams){



        val result: RealmResults<VolumeParams> = realm.where(VolumeParams::class.java)
                .equalTo("title", "default")
                .findAll()

        if (result.isEmpty()){
            realm.insert(volParams)
        } else{
            var oldParams = result.first()

            oldParams!!.systemLevel = volParams!!.systemLevel
            oldParams!!.musicLevel = volParams!!.musicLevel
            oldParams!!.voiceCallLevel = volParams!!.voiceCallLevel
            oldParams!!.notificationLevel = volParams!!.notificationLevel
            oldParams!!.ringLevel = volParams!!.ringLevel
        }
    }

    fun getDefaultParams(): VolumeParams?{

        val result: RealmResults<VolumeParams> = realm.where(VolumeParams::class.java)
                .equalTo("title", "default")
                .findAll()

        return result.first()

    }

}