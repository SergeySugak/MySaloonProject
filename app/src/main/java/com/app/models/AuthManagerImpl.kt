package com.app.models

import android.text.TextUtils
import com.app.mscorebase.appstate.StateHolder
import com.app.mscorebase.appstate.StateWriter
import main.java.com.app.mscorebase.auth.AuthManager
import javax.inject.Inject

class AuthManagerImpl @Inject constructor(): AuthManager, StateHolder {
    private var username = ""

    override fun getUsername() = username

    override fun getState(): AuthManager.State {
        return if (TextUtils.isEmpty(username)) {
            AuthManager.State.loggedOut
        } else {
            AuthManager.State.loggedIn
        }
    }

    override fun logIn(username: String) {
        this.username = username
    }

    override fun logOut() {
        username = ""
    }

    override fun clearState(writer: StateWriter) {
        writer.clearState(this, false)
    }

    override fun restoreState(writer: StateWriter) {
        val state: Map<String, String?> = writer.readState(this)
        if (state.containsKey(STATE_USER_NAME)) {
            username = state[STATE_USER_NAME] ?: ""
        }
    }

    override fun saveState(writer: StateWriter) {
        val state: MutableMap<String, String> = HashMap()
        state[STATE_USER_NAME] = username
        writer.writeState(this, state)
    }

    companion object {
        private val STATE_USER_NAME: String = AuthManagerImpl::class.java.simpleName + "_STATE_REGISTRATION_REQUEST_HTTP_ERROR"
    }
}