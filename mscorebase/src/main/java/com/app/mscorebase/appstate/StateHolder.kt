package com.app.mscorebase.appstate

interface StateHolder {
    val id: String
        get() = javaClass.simpleName

    fun saveState(writer: StateWriter)
    fun restoreState(writer: StateWriter)
    fun clearState(writer: StateWriter)
}