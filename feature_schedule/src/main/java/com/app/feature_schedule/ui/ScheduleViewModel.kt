package com.app.feature_schedule.ui

import android.util.Log
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.ui.Colorizer
import com.app.mscorebase.ui.MSFragmentViewModel
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleViewModel
@Inject constructor(
    private val appState: AppStateManager,
    val eventColorizer: Colorizer,
    private val dbRepository: DbRepository,
    private val gson: Gson
) : MSFragmentViewModel(appState) {
    private lateinit var fromDate: Calendar
    private lateinit var toDate: Calendar
    private lateinit var subscription: Disposable
    val notifier: PublishSubject<Pair<Calendar, Calendar>> = PublishSubject.create()

    fun loadData(fromDate: Calendar, toDate: Calendar) {
        Log.d(javaClass.simpleName, "Loading data")
    }

    override fun onCleared() {
        subscription.dispose()
        super.onCleared()
    }

    private fun checkNewRange(from: Calendar, to: Calendar): Boolean {
        if (!::fromDate.isInitialized || !::toDate.isInitialized) {
            return true
        }
        var result = true

        return result
    }

    override fun restoreState(writer: StateWriter) {
        val state: Map<String, String?> = writer.readState(this)
        if (state.containsKey(STATE_FROM_DATE)) {
            fromDate = gson.fromJson(state[STATE_FROM_DATE], Calendar::class.java)
        }
        if (state.containsKey(STATE_TO_DATE)) {
            toDate = gson.fromJson(state[STATE_TO_DATE], Calendar::class.java)
        }
    }

    override fun saveState(writer: StateWriter) {
        val state: MutableMap<String, String> = HashMap()
        if (::fromDate.isInitialized) {
            state[STATE_FROM_DATE] = gson.toJson(fromDate)
        }
        if (::toDate.isInitialized) {
            state[STATE_TO_DATE] = gson.toJson(fromDate)
        }
    }

    companion object {
        private const val TIMEOUT = 300L
        private val STATE_FROM_DATE = "$this.javaClass.simpleName_STATE_FROM_DATE"
        private val STATE_TO_DATE = "$this.javaClass.simpleName_STATE_TO_DATE"
    }

    init {
        subscription = notifier
            .debounce(TIMEOUT, TimeUnit.MILLISECONDS)
            .subscribe { pair ->
                if (checkNewRange(pair.first, pair.second)) {
                    loadData(pair.first, pair.second)
                }
            }
    }
}
