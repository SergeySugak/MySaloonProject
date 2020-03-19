package com.app.mscorebase.appstate

import android.content.Context
import main.java.com.app.mscorebase.auth.AuthManager

interface AppStateManager : StateWriter {
    val context: Context
    val authManager: AuthManager
    fun attachStateManager(sm: StateHolder)
    fun detachStateManager(sm: StateHolder)
    fun save()
}