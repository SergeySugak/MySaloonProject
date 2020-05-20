package com.app.feature_master.di

import com.app.feature_master.api.MasterFeatureDependencies
import com.app.feature_master.ui.MasterFragment
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(modules = [MasterFeatureViewModelsModule::class],
            dependencies = [MasterFeatureDependencies::class])
@FeatureScope
interface MasterFeatureComponent {
    fun inject(fragment: MasterFragment)
}