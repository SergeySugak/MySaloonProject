package com.app.feature_select_event.di

import com.app.feature_select_event.api.SelectEventFeatureDependencies
import com.app.feature_select_event.ui.EventSelectionDialog
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(
    modules = [
        SelectEventFeatureViewModelsModule::class],
    dependencies = [SelectEventFeatureDependencies::class]
)
@FeatureScope
interface SelectEventFeatureComponent {
    fun inject(eventSelectionDialog: EventSelectionDialog)
}