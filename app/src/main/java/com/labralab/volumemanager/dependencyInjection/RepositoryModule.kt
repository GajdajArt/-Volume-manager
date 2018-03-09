package com.labralab.volumemanager.dependencyInjection

import com.labralab.volumemanager.repository.Repository
import dagger.Module
import dagger.Provides
import io.realm.Realm
import javax.inject.Singleton


@Module
class RepositoryModule{

    @Provides
    @Singleton
    fun provideRepository(): Repository {
        return Repository()
    }

    @Provides
    fun provideRealm(): Realm{
        return Realm.getDefaultInstance()
    }
}