package com.app.feature_consumables.di

import com.app.feature_consumables.adapters.ConsumablesAdapter
import com.app.feature_consumables.adapters.ConsumablesAdapterImpl
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Binds
import dagger.Module

@Module
abstract class ConsumablesFeatureModule {

    @Binds
    @FeatureScope
    abstract fun bindConsumablesAdapter(mastersAdapter: ConsumablesAdapterImpl): ConsumablesAdapter
}