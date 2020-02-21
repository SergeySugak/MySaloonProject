package com.app.msa_auth.di

import com.app.msa_auth.models.AuthValidator
import com.app.msa_auth.models.AuthValidatorImpl
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Module
import dagger.Provides

@Module
object AuthModule {
    @Provides
    @FeatureScope
    fun provideLoginValidator(): AuthValidator =
        AuthValidatorImpl()
}