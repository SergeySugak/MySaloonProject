package com.app.msa

import android.app.Application
import com.app.di.AppComponent
import com.app.di.DaggerAppComponent
import com.app.mscorebase.di.ComponentDependenciesProvider
import com.app.mscorebase.di.HasComponentDependencies
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class App: Application(), HasComponentDependencies {
    companion object {
        private val TAG = App::class.java.simpleName
        lateinit var appComponent: AppComponent
    }

    init {
        appComponent = DaggerAppComponent.builder().context(this).build()
        appComponent.inject(this)
    }

    @Inject
    override lateinit var dependencies: ComponentDependenciesProvider
        protected set
}