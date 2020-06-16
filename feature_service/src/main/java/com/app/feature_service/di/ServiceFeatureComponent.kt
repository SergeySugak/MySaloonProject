package com.app.feature_service.di

import com.app.feature_service.api.ServiceFeatureDependencies
import com.app.feature_service.ui.ServiceDurationSelectionDialog
import com.app.feature_service.ui.ServiceFragment
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(
    modules = [NewServiceFeatureBindingsModule::class,
        ServiceFeatureViewModelsModule::class],
    dependencies = [ServiceFeatureDependencies::class]
)
@FeatureScope
interface ServiceFeatureComponent {
    fun inject(fragment: ServiceFragment)
    fun inject(fragment: ServiceDurationSelectionDialog)
}