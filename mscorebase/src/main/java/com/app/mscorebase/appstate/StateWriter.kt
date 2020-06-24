package com.app.mscorebase.appstate

interface StateWriter {
    fun writeState(sm: StateHolder, state: Map<String, String>)

    fun readState(sm: StateHolder): Map<String, String>?
    fun clearState(sm: StateHolder, detach: Boolean)
}