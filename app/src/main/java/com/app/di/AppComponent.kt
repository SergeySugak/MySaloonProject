package com.app.di

import android.content.Context
import com.app.msa.App
import com.app.msa_auth.api.AuthFeatureDependencies
import com.app.msa_db_repo.repository.di.DbRepositoryModule
import com.app.msa_main.api.MainFeatureDependencies
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class,
                        BindingsModule::class,
                        DbRepositoryModule::class,
                        ComponentDependenciesModule::class])
interface AppComponent: AuthFeatureDependencies, MainFeatureDependencies {

    fun inject(app: App)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }
}