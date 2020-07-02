package com.app.feature_consumables.di

import com.app.feature_consumables.api.ConsumablesFeatureDependencies
import com.app.feature_consumables.ui.ConsumablesFragment
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(
    modules = [ConsumablesFeatureModule::class,
        ConsumablesFeatureViewModelsModule::class],
    dependencies = [ConsumablesFeatureDependencies::class]
)
@FeatureScope
interface ConsumablesFeatureComponent {
    fun inject(mastersFragment: ConsumablesFragment)
}