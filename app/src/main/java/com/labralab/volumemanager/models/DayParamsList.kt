package com.labralab.volumemanager.models

import com.labralab.volumemanager.repository.Repository
import io.realm.RealmList
import io.realm.RealmObject


open class DayParamsList : RealmObject(){

    var dayOfWeekList: RealmList<Int>? = RealmList()

    var title: String? = ""
    var paramsList: RealmList<VolumeParams>? = RealmList()
    var state: Boolean? = false

    fun getVolumeParamsItem(title: String): VolumeParams?{

        return paramsList!!.firstOrNull { it.title == title }
    }

    companion object {

        fun remove(title: String) {
            val repository = Repository()
            repository.removeDay(title)
        }

        fun getInstance(title: String) : DayParamsList{

            val repository = Repository()
            return repository.getDay(title)
        }
    }
}
