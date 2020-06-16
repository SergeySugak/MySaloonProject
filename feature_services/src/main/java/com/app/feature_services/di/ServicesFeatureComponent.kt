package com.app.feature_services.di

import com.app.feature_services.api.ServicesFeatureDependencies
import com.app.feature_services.ui.ServicesFragment
import com.app.msa_db_repo.repository.di.DbRepositoryModule
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Component

@Component(
    modules = [ServicesFeatureViewModelsModule::class,
        ServicesFeatureModule::class,
        DbRepositoryModule::class],
    dependencies = [ServicesFeatureDependencies::class]
)
@FeatureScope
interface ServicesFeatureComponent {
    fun inject(servicesFragment: ServicesFragment)
}