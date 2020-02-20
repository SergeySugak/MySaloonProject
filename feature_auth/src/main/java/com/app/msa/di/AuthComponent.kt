package com.app.msa.di

import com.app.msa.api.AuthFeatureDependencies
import com.app.msa.repository.di.AuthRepositoryModule
import com.app.msa.scopes.AuthScope
import com.app.msa.ui.AuthActivity
import com.app.mscorebase.di.ViewModelFactoryModule
import dagger.Component

@AuthScope
@Component(modules = [AuthModule::class,
    ViewModelFactoryModule::class,
    AuthViewModelsModule::class,
    AuthRepositoryModule::class],
    dependencies = [AuthFeatureDependencies::class])
interface AuthComponent {
    fun inject(authActivity: AuthActivity)
}