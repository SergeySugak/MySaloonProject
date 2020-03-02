package com.app.msa_main.di

import com.app.feature_masters.ui.MastersFragment
import com.app.feature_schedule.ui.ScheduleFragment
import com.app.feature_services.ui.ServicesFragment
import com.app.msa.repository.auth.FirebaseDbRepository
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object MainFeatureModule {
    @Provides
    @FeatureScope
    fun provideMastersFragment(): MastersFragment {
        val result = MastersFragment.newInstance()
        result.retainInstance = true
        return result
    }

    @Provides
    @FeatureScope
    fun provideServicesFragment(): ServicesFragment {
        val result = ServicesFragment.newInstance()
        result.retainInstance = true
        return result
    }

    @Provides
    @FeatureScope
    fun provideScheduleFragment(): ScheduleFragment {
        val result = ScheduleFragment.newInstance()
        result.retainInstance = true
        return result
    }
}