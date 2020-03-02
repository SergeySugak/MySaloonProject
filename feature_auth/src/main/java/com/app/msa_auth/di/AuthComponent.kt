package com.app.msa_auth.di

import com.app.msa_auth.api.AuthFeatureDependencies
import com.app.msa_auth.ui.AuthActivity
import com.app.msa_auth_repo.repository.di.AuthRepositoryModule
import com.app.msa_scopes.scopes.FeatureScope
import com.app.mscorebase.di.ViewModelFactoryModule
import dagger.Component

@FeatureScope
@Component(modules = [AuthModule::class,
                        ViewModelFactoryModule::class,
                        AuthViewModelsModule::class,
                        AuthRepositoryModule::class],
                        dependencies = [AuthFeatureDependencies::class])
interface AuthComponent {
    fun inject(authActivity: AuthActivity)
}