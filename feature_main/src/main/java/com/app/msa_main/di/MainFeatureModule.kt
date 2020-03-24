package com.app.msa_main.di

import com.app.feature_masters.ui.MastersFragment
import com.app.feature_newservice.ui.NewServiceFragment
import com.app.feature_schedule.ui.ScheduleFragment
import com.app.feature_services.ui.ServicesFragment
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Module
import dagger.Provides

@Module
object MainFeatureModule {
    //Надо бы понять почему для scoped штук требуется
    //сохранять статические инстансы - почему это не происходит автоматически?
    private lateinit var savedMastersFragment: MastersFragment
    private lateinit var savedServicesFragment: ServicesFragment
    private lateinit var savedScheduleFragment: ScheduleFragment

    @Provides
    @FeatureScope
    fun provideMastersFragment(): MastersFragment {
        if (!::savedMastersFragment.isInitialized) {
            savedMastersFragment = MastersFragment.newInstance()
            savedMastersFragment.retainInstance = true
        }
        return savedMastersFragment
    }

    @Provides
    @FeatureScope
    fun provideServicesFragment(): ServicesFragment {
        if (!::savedServicesFragment.isInitialized) {
            savedServicesFragment = ServicesFragment.newInstance()
            savedServicesFragment.retainInstance = true
        }
        return savedServicesFragment
    }

    @Provides
    @FeatureScope
    fun provideScheduleFragment(): ScheduleFragment {
        if (!::savedScheduleFragment.isInitialized) {
            savedScheduleFragment = ScheduleFragment.newInstance()
            savedScheduleFragment.retainInstance = true
        }
        return savedScheduleFragment
    }
}