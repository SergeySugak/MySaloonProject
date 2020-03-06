package com.app.feature_newservice.di

import com.app.msa_scopes.scopes.FeatureScope
import com.app.mscorebase.ui.dialogs.SingleChoiceAdapter
import com.app.mscoremodels.services.ServiceDuration
import dagger.Module
import dagger.Provides

@Module
class NewServiceFeatureModule {
    @Provides
    @FeatureScope
    fun provideServiceDurationAdapter() = object: SingleChoiceAdapter<ServiceDuration>(){}
}