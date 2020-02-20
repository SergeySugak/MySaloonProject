package com.app.msa.di

import com.app.msa.api.MastersFeatureDependencies
import com.app.msa.masters.MastersFragment
import com.app.msa.scopes.MastersScope
import dagger.Component

@MastersScope
@Component(modules = [MastersFeatureViewModelsModule::class],
    dependencies = [MastersFeatureDependencies::class])
interface MastersFeatureComponent {
    fun inject(mastersFragment: MastersFragment)
}