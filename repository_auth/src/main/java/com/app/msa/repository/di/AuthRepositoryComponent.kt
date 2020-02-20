package com.app.msa.repository.di

import dagger.Component

@Component(modules = [AuthRepositoryModule::class])
interface AuthRepositoryComponent {
    @Component.Builder
    interface Builder {
        fun build(): AuthRepositoryComponent
    }
}