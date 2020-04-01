package com.app.feature_service.di

import com.app.feature_service.models.ServiceDurationAdapter
import com.app.msa_scopes.scopes.FeatureScope
import com.app.mscorebase.ui.dialogs.choicedialog.SimpleChoiceAdapter
import com.app.mscoremodels.saloon.ChoosableServiceDuration
import dagger.Binds
import dagger.Module

@Module
abstract class NewServiceFeatureBindingsModule {
    @Binds
    @FeatureScope
    abstract fun bindServiceDurationAdapter(serviceDurationAdapter: ServiceDurationAdapter): SimpleChoiceAdapter<ChoosableServiceDuration>
}