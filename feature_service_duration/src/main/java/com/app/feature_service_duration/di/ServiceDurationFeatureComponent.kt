package com.app.feature_service_duration.di

import com.app.feature_service_duration.api.ServiceDurationFeatureDependencies
import com.app.feature_service_duration.ui.ServiceDurationSelectionDialog
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(
    modules = [ServiceDurationFeatureBindingsModule::class,
        ServiceDurationFeatureViewModelsModule::class],
    dependencies = [ServiceDurationFeatureDependencies::class]
)
@FeatureScope
interface ServiceDurationFeatureComponent {
    fun inject(fragment: ServiceDurationSelectionDialog)
}