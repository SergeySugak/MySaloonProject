@file:Suppress("unused")

package com.app.mscorebase.di

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.MapKey
import dagger.Module
import dagger.multibindings.Multibinds
import kotlin.reflect.KClass

interface ComponentDependencies

inline fun <reified T : ComponentDependencies> Fragment.findComponentDependencies(): T {
    return findFragmentComponentDependenciesProvider()[T::class.java] as T
}

inline fun <reified T : ComponentDependencies> AppCompatActivity.findComponentDependencies(): T {
    return findActivityComponentDependenciesProvider()[T::class.java] as T
}

typealias ComponentDependenciesProvider = Map<Class<out ComponentDependencies>, @JvmSuppressWildcards ComponentDependencies>

interface HasComponentDependencies {
    val dependencies: ComponentDependenciesProvider
}

@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class ComponentDependenciesKey(val value: KClass<out ComponentDependencies>)

@Module
abstract class DummyComponentDependenciesModule private constructor() {
    @Multibinds
    abstract fun componentDependencies(): ComponentDependenciesProvider
}

fun Fragment.findFragmentComponentDependenciesProvider(): ComponentDependenciesProvider {
    var current: Fragment? = parentFragment
    while (current !is HasComponentDependencies?) {
        current = current?.parentFragment
    }

    val hasDaggerProviders = current ?: when {
        activity is HasComponentDependencies -> activity as HasComponentDependencies
        activity?.application is HasComponentDependencies -> activity?.application as HasComponentDependencies
        else -> throw IllegalStateException("Can not find suitable dagger provider for $this")
    }
    return hasDaggerProviders.dependencies
}

fun AppCompatActivity.findActivityComponentDependenciesProvider(): ComponentDependenciesProvider {
    val hasDaggerProviders = when {
        this is HasComponentDependencies -> this
        this.application is HasComponentDependencies -> this.application as HasComponentDependencies
        else -> throw IllegalStateException("Can not find suitable dagger provider for $this")
    }
    return hasDaggerProviders.dependencies
}
