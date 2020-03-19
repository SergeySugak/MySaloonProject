package com.app.feature_newservice.di

import com.app.feature_newservice.models.ServiceDurationAdapter
import com.app.msa_scopes.scopes.FeatureScope
import com.app.mscorebase.ui.dialogs.choicedialog.SingleChoiceAdapter
import com.app.mscoremodels.saloon.ServiceDuration
import dagger.Binds
import dagger.Module

@Module
abstract class NewServiceFeatureBindingsModule {
    @Binds
    @FeatureScope
    abstract fun bindServiceDurationAdapter(serviceDurationAdapter: ServiceDurationAdapter): SingleChoiceAdapter<ServiceDuration>
}