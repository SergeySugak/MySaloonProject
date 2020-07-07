package com.app.feature_select_uom.di

import com.app.feature_select_uom.api.SelectUomFeatureDependencies
import com.app.feature_select_uom.api.SelectUomFeatureViewModelsModule
import com.app.feature_select_uom.ui.UomSelectionDialog
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(
    modules = [SelectUomFeatureViewModelsModule::class],
    dependencies = [SelectUomFeatureDependencies::class]
)
@FeatureScope
interface SelectUomFeatureComponent {
    fun inject(fragment: UomSelectionDialog)
}