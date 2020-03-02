package com.app.feature_masters.di

import com.app.feature_masters.api.MastersFeatureDependencies
import com.app.feature_masters.ui.MastersFragment
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(modules = [MastersFeatureViewModelsModule::class],
    dependencies = [MastersFeatureDependencies::class])
@FeatureScope
interface MastersFeatureComponent {
    fun inject(mastersFragment: MastersFragment)
}