package com.labralab.volumemanager

import android.app.Application
import io.realm.Realm

/**
 * Created by pc on 05.12.2017.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}