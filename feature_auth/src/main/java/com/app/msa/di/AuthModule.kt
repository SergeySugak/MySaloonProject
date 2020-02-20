package com.app.msa.di

import com.app.mobifix.models.auth.AuthValidator
import com.app.msa.models.AuthValidatorImpl
import com.app.msa.scopes.AuthScope
import dagger.Module
import dagger.Provides

@Module
object AuthModule {
    @Provides
    @AuthScope
    fun provideLoginValidator(): AuthValidator  = AuthValidatorImpl()
}