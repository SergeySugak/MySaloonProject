package com.app.msa_auth_repo.repository.di

import dagger.Component

@Component(modules = [AuthRepositoryModule::class])
interface AuthRepositoryComponent {
    @Component.Builder
    interface Builder {
        fun build(): AuthRepositoryComponent
    }
}