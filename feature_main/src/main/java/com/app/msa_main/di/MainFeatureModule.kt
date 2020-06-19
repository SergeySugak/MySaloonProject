package com.app.msa_main.di

import com.app.feature_masters.ui.MastersFragment
import com.app.feature_schedule.ui.ScheduleFragment
import com.app.feature_services.ui.ServicesFragment
import com.app.msa_main.main.MainActivity
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Module
import dagger.Provides

@Module
object MainFeatureModule {
    //Надо бы понять почему для scoped штук требуется
    //сохранять статические инстансы - почему это не происходит автоматически?
    private var savedMastersFragment: MastersFragment? = null
    private var savedServicesFragment: ServicesFragment? = null
    private var savedScheduleFragment: ScheduleFragment? = null

    fun reset() {
        savedMastersFragment = null
        savedServicesFragment = null
        savedScheduleFragment = null
    }

    @Provides
    @FeatureScope
    fun provideMastersFragment(): MastersFragment {
        if (savedMastersFragment == null) {
            savedMastersFragment = MastersFragment.newInstance()
        }
        return savedMastersFragment!!
    }

    @Provides
    @FeatureScope
    fun provideServicesFragment(): ServicesFragment {
        if (savedServicesFragment == null) {
            savedServicesFragment = ServicesFragment.newInstance()
        }
        return savedServicesFragment!!
    }

    @Provides
    @FeatureScope
    fun provideScheduleFragment(): ScheduleFragment {
        if (savedScheduleFragment == null) {
            savedScheduleFragment = ScheduleFragment.newInstance()
        }
        return savedScheduleFragment!!
    }
}