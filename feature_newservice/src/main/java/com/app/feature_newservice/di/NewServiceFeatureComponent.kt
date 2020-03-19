package com.app.feature_newservice.di

import com.app.feature_newservice.api.NewServiceFeatureDependencies
import com.app.feature_newservice.ui.NewServiceFragment
import com.app.feature_newservice.ui.ServiceDurationSelectionDialog
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(modules = [NewServiceFeatureBindingsModule::class,
                      NewServiceFeatureViewModelsModule::class],
    dependencies = [NewServiceFeatureDependencies::class])
@FeatureScope
interface NewServiceFeatureComponent {
    fun inject(fragment: NewServiceFragment)
    fun inject(fragment: ServiceDurationSelectionDialog)
}