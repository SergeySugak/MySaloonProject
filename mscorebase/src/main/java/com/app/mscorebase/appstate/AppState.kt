package com.app.mscorebase.appstate

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import main.java.com.app.mscorebase.auth.AuthManager

class AppState(val authManager: AuthManager,
               private val appSharedPreferences: SharedPreferences,
               private val gson: Gson) : AppStateManager {

    private val stateManagers: MutableMap<String, StateHolder> =
        ConcurrentHashMap()

    override fun attachStateManager(sm: StateHolder) {
        if (appSharedPreferences.contains(sm.id)) {
            sm.restoreState(this)
        }
        stateManagers[sm.id] = sm
    }

    @SuppressLint("ApplySharedPref")
    override fun detachStateManager(sm: StateHolder) {
        clearState(sm, false)
        stateManagers.remove(sm.id)
    }

    @SuppressLint("ApplySharedPref")
    override fun writeState(
        sm: StateHolder,
        state: Map<String, String>
    ) {
        val stateHolderManager = stateManagers[sm.id]
        if (stateHolderManager != null && state.isNotEmpty()) {
            appSharedPreferences.edit().putString(sm.id, gson.toJson(state)).commit()
        }
        Log.d(TAG, "State written for " + sm.id)
    }

    override fun readState(sm: StateHolder): Map<String, String> {
        val typeOfHashMap = object :
            TypeToken<Map<String?, String?>?>() {}.type
        val json: String?
        if (appSharedPreferences.contains(sm.id)) {
            json = appSharedPreferences.getString(sm.id, null)
            if (!TextUtils.isEmpty(json)) {
                return gson.fromJson(
                    json,
                    typeOfHashMap
                )
            }
        }
        Log.d(TAG, "State read for " + sm.id)
        return HashMap()
    }

    @SuppressLint("ApplySharedPref")
    override fun clearState(sm: StateHolder, detach: Boolean) {
        if (appSharedPreferences.contains(sm.id)) {
            appSharedPreferences.edit().remove(sm.id).commit()
        }
        if (detach) {
            stateManagers.remove(sm.id)
        }
    }

    override fun save() {
        for (sm in stateManagers.values) {
            sm.saveState(this)
        }
    }

    companion object {
        private val TAG = AppState::class.java.simpleName
    }

}