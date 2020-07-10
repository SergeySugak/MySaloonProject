package com.app.feature_select_consumables.di

import com.app.feature_select_consumables.api.SelectConsumablesFeatureDependencies
import com.app.feature_select_consumables.ui.ConsumablesSelectionDialog
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(
    modules = [SelectConsumablesFeatureViewModelsModule::class],
    dependencies = [SelectConsumablesFeatureDependencies::class]
)
@FeatureScope
interface SelectConsumablesFeatureComponent {
    fun inject(fragment: ConsumablesSelectionDialog)
}