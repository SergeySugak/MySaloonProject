package com.app.mobifix.appstate

import com.app.mscorebase.appstate.StateHolder
import com.app.mscorebase.appstate.StateWriter

interface AppStateManager : StateWriter {
    fun attachStateManager(sm: StateHolder)
    fun detachStateManager(sm: StateHolder)
    fun save()
    fun logout()
}