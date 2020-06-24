package com.app.mscorebase.appstate

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import main.java.com.app.mscorebase.auth.AuthManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AppState(
    override val context: Context, //Application context
    override val authManager: AuthManager,
    private val appSharedPreferences: SharedPreferences,
    private val gson: Gson
) : AppStateManager {

    private val stateHolders: MutableMap<String, StateHolder> =
        ConcurrentHashMap()

    override fun attachStateHolder(sh: StateHolder) {
        if (appSharedPreferences.contains(sh.id)) {
            sh.restoreState(this)
        }
        stateHolders[sh.id] = sh
    }

    @SuppressLint("ApplySharedPref")
    override fun detachStateHolder(sh: StateHolder) {
        clearState(sh, false)
        stateHolders.remove(sh.id)
    }

    @SuppressLint("ApplySharedPref")
    override fun writeState(sh: StateHolder, state: Map<String, String>) {
        val curState = state.toMutableMap()
        val stateHolderManager = stateHolders[sh.id]
        if (stateHolderManager != null && state.isNotEmpty()) {
            if (sh is InterruptedStateHolder) {
                curState[getInterruptedStateKey(sh)] = sh.uniqueId
            }
            appSharedPreferences.edit().putString(sh.id, gson.toJson(curState)).commit()
        }
        Log.d(TAG, "State written for " + sh.id)
    }

    override fun readState(sh: StateHolder): Map<String, String>? {
        val typeOfHashMap = object : TypeToken<Map<String?, String?>?>() {}.type
        val json: String?
        if (appSharedPreferences.contains(sh.id)) {
            json = appSharedPreferences.getString(sh.id, null)
            if (!TextUtils.isEmpty(json)) {
                val state: MutableMap<String, String> = gson.fromJson(json, typeOfHashMap)
                if (sh is InterruptedStateHolder) {
                    val current = isCurrent(sh, state)
                    state.remove(getInterruptedStateKey(sh))
                    if (!current) {
                        return state
                    }
                } else {
                    return state
                }
            }
        }
        Log.d(TAG, "State read for " + sh.id)
        return null
    }

    private fun isCurrent(sh: InterruptedStateHolder, state: Map<String, String>): Boolean {
        return if (state.containsKey(getInterruptedStateKey(sh))) {
            state[getInterruptedStateKey(sh)] == sh.uniqueId
        } else {
            false
        }
    }

    private fun getInterruptedStateKey(sh: StateHolder): String = "${sh.id}_$STATE_HOLDER_HASH_CODE"

    @SuppressLint("ApplySharedPref")
    override fun clearState(sh: StateHolder, detach: Boolean) {
        if (appSharedPreferences.contains(sh.id)) {
            appSharedPreferences.edit().remove(sh.id).commit()
        }
        if (detach) {
            stateHolders.remove(sh.id)
        }
    }

    override fun save() {
        for (sh in stateHolders.values) {
            sh.saveState(this)
        }
    }

    override fun clear() {
        appSharedPreferences.edit().clear().commit()
    }

    companion object {
        private val TAG = AppState::class.java.simpleName
        private const val STATE_HOLDER_HASH_CODE = "_STATE_HOLDER_HASH_CODE"
    }

}