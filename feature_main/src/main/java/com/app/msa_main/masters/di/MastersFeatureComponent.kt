package com.app.msa_main.masters.di

import com.app.msa_main.masters.MastersFragment
import com.app.msa_main.masters.api.MastersFeatureDependencies
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(modules = [MastersFeatureViewModelsModule::class],
    dependencies = [MastersFeatureDependencies::class])
@FeatureScope
interface MastersFeatureComponent {
    fun inject(mastersFragment: MastersFragment)
}