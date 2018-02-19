package com.labralab.volumemanager.models

import io.realm.RealmObject

/**
 * Created by pc on 24.11.2017.
 */

open class VolumeParams : RealmObject(){

    internal var title: String = ""

    internal var startHours: Int = 0
    internal var startMinutes: Int = 0
    internal var stopHours: Int = 0
    internal var stopMinutes: Int = 0

    var notificationLevel: Int = 0
    var ringLevel: Int = 0
     var systemLevel: Int = 0
    var musicLevel: Int = 0
    var isVibration: Boolean = false
}


