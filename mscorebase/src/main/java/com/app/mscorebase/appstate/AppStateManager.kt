package com.app.mscorebase.appstate

interface AppStateManager : StateWriter {
    fun attachStateManager(sm: StateHolder)
    fun detachStateManager(sm: StateHolder)
    fun save()
}