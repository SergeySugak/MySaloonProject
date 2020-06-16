package com.app.feature_select_services.di

import com.app.feature_select_services.api.SelectServicesFeatureDependencies
import com.app.feature_select_services.ui.ServicesSelectionDialog
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(
    modules = [SelectServicesFeatureViewModelsModule::class],
    dependencies = [SelectServicesFeatureDependencies::class]
)
@FeatureScope
interface SelectServicesFeatureComponent {
    fun inject(fragment: ServicesSelectionDialog)
}