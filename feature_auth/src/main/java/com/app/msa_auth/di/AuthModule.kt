package com.app.msa_auth.di

import com.app.msa_auth.models.AuthValidator
import com.app.msa_auth.models.AuthValidatorImpl
import com.app.msa_scopes.scopes.FeatureScope
import dagger.Binds
import dagger.Module

@Module
abstract class AuthModule {
    @Binds
    @FeatureScope
    abstract fun bindLoginValidator(authValidatorImpl: AuthValidatorImpl): AuthValidator
}