package com.app.feature_services.di

import com.app.feature_services.models.ServicesAdapter
import com.app.feature_services.models.ServicesAdapterImpl
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Binds
import dagger.Module

@Module
abstract class ServicesFeatureModule {

    @Binds
    @FeatureScope
    abstract fun bindServicesAdapter(servicesAdapter: ServicesAdapterImpl): ServicesAdapter

}