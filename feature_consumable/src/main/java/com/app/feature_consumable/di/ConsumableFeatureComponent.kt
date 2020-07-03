package com.app.feature_consumable.di

import com.app.feature_consumable.api.ConsumableFeatureDependencies
import com.app.feature_consumable.ui.ConsumableFragment
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(
    modules = [ConsumableFeatureViewModelsModule::class],
    dependencies = [ConsumableFeatureDependencies::class]
)
@FeatureScope
interface ConsumableFeatureComponent {
    fun inject(fragment: ConsumableFragment)
}