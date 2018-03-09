package com.labralab.volumemanager.dependencyInjection

import com.labralab.volumemanager.adapters.MainRecyclerViewAdapter
import com.labralab.volumemanager.adapters.ParamsRecyclerViewAdapter
import com.labralab.volumemanager.repository.Repository
import com.labralab.volumemanager.views.MainActivity
import com.labralab.volumemanager.volumeManager.VolumeManager
import dagger.Component
import javax.inject.Singleton

/**
 * Created by pc on 07.03.2018.
 */
@Singleton
@Component(
        modules = [(RepositoryModule::class), (ManagerModule::class)]
)
interface AppComponents {

    fun inject(mainActivity: MainActivity)
    fun inject(mainRecyclerViewAdapter: MainRecyclerViewAdapter)
    fun inject(repository: Repository)
    fun inject(volumeManager: VolumeManager)
    fun inject(paramsRecyclerViewAdapter: ParamsRecyclerViewAdapter)

}