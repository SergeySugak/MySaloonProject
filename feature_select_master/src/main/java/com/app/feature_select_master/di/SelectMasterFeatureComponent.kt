package com.app.feature_select_master.di

import com.app.feature_select_master.api.SelectMasterFeatureDependencies
import com.app.feature_select_master.ui.MasterSelectionDialog
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(
    modules = [SelectMasterFeatureViewModelsModule::class],
    dependencies = [SelectMasterFeatureDependencies::class]
)
@FeatureScope
interface SelectMasterFeatureComponent {
    fun inject(fragment: MasterSelectionDialog)
}