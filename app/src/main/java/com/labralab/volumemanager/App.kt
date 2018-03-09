package com.labralab.volumemanager

import android.app.Application
import android.content.Context
import com.labralab.volumemanager.dependencyInjection.AppComponents
import com.labralab.volumemanager.dependencyInjection.DaggerAppComponents
import com.labralab.volumemanager.dependencyInjection.ManagerModule
import com.labralab.volumemanager.dependencyInjection.RepositoryModule
import io.realm.Realm

/**
 * Created by pc on 05.12.2017.
 */
class App : Application() {

    lateinit var context: Context

    companion object {
        lateinit var appComponents: AppComponents
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        appComponents = buildComponents()
        Realm.init(this)
    }

    fun buildComponents(): AppComponents{
        return DaggerAppComponents.builder()
                .managerModule(ManagerModule(context))
                .repositoryModule(RepositoryModule())
                .build()
    }
}