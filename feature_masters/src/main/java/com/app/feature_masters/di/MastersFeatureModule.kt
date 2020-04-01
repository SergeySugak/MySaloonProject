package com.app.feature_masters.di

import com.app.feature_masters.models.MastersAdapter
import com.app.feature_masters.models.MastersAdapterImpl
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Binds
import dagger.Module

@Module
abstract class MastersFeatureModule {

    @Binds
    @FeatureScope
    abstract fun bindMastersAdapter(mastersAdapter: MastersAdapterImpl): MastersAdapter

}