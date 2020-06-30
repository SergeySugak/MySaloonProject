package com.app.feature_service_duration.di

import com.app.feature_service_duration.adapters.ServiceDurationAdapter
import com.app.msa_scopes.scopes.FeatureScope
import com.app.mscorebase.ui.dialogs.choicedialog.SimpleChoiceAdapter
import com.app.mscoremodels.saloon.ChoosableServiceDuration
import dagger.Binds
import dagger.Module

@Module
abstract class ServiceDurationFeatureBindingsModule {
    @Binds
    @FeatureScope
    abstract fun bindServiceDurationAdapter(serviceDurationAdapter: ServiceDurationAdapter): SimpleChoiceAdapter<ChoosableServiceDuration>
}