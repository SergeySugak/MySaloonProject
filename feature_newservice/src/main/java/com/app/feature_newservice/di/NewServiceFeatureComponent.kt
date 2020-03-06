package com.app.feature_newservice.di

import com.app.feature_newservice.api.NewServiceFeatureDependencies
import com.app.feature_newservice.ui.NewServiceFragment
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(modules = [NewServiceFeatureModule::class,
                        NewServiceFeatureViewModelsModule::class],
    dependencies = [NewServiceFeatureDependencies::class])
@FeatureScope
interface NewServiceFeatureComponent {
    fun inject(newServiceFragment: NewServiceFragment)
}