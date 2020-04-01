package com.app.feature_master.di

import com.app.feature_master.api.MasterFeatureDependencies
import com.app.feature_master.ui.MasterFragment
import com.app.feature_master.ui.MasterServicesSelectionDialog
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(modules = [MasterFeatureBindingsModule::class,
                      MasterFeatureViewModelsModule::class],
            dependencies = [MasterFeatureDependencies::class])
@FeatureScope
interface MasterFeatureComponent {
    fun inject(fragment: MasterFragment)
    fun inject(fragment: MasterServicesSelectionDialog)
}