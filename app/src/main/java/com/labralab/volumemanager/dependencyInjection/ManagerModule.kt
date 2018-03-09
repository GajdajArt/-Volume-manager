package com.labralab.volumemanager.dependencyInjection

import android.content.Context
import com.labralab.volumemanager.volumeManager.VolumeManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class ManagerModule(var context: Context){

    @Provides
    @Singleton
    fun provideManager(): VolumeManager{
        return VolumeManager(context)
    }
}