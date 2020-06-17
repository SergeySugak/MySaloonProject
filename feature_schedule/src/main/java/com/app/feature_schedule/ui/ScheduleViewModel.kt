package com.app.feature_schedule.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.Colorizer
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.SaloonEvent
import com.app.view_schedule.api.SchedulerEvent
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.*

class ScheduleViewModel
@Inject constructor(
    private val appState: AppStateManager,
    val eventColorizer: Colorizer,
    private val dbRepository: DbRepository,
    private val gson: Gson
) : MSFragmentViewModel(appState) {
    private val eventsMap = ConcurrentHashMap<String, MutableList<SaloonEvent>>()
    private var subscription: Disposable
    private val dateFormatter = SimpleDateFormat(KEY_FORMAT, Locale.getDefault())
    val notifier: PublishSubject<Pair<Calendar, Calendar>> = PublishSubject.create()
    private val intNewEventsLoaded = StatefulMutableLiveData<List<SchedulerEvent>>()
    val newEventsLoaded: StatefulLiveData<List<SchedulerEvent>> = intNewEventsLoaded

    fun loadData(from: Calendar, to: Calendar, force: Boolean = false) {
        val daysBetween = TimeUnit.DAYS.convert(to.time.time - from.time.time, TimeUnit.MILLISECONDS).toInt()
        setInProgress(true)
        val newEvents = mutableListOf<SchedulerEvent>()
        val jobs = mutableListOf<Deferred<*>>()
        viewModelScope.launch(Dispatchers.IO){
            for (i in 0 .. daysBetween){
                jobs.add(i, async {
                    val date = from.clone() as Calendar
                    date.add(Calendar.DATE, i)
                    val events: MutableList<SaloonEvent>
                    val key = dateFormatter.format(date.time)
                    if (!eventsMap.containsKey(key) || force) {
                        val requestResult = dbRepository.getEvents(date)
                        if (requestResult is Result.Success){
                            events = requestResult.data.toMutableList()
                            if (eventsMap[key] != null) {
                                events.removeAll{ event ->
                                    eventsMap[key]?.contains(event) ?: false
                                }
                                eventsMap[key]!!.addAll(events)
                            }
                            else {
                                eventsMap[key] = mutableListOf<SaloonEvent>().apply { addAll(events) }
                            }
                            if (events.size > 0) {
                                synchronized(this){
                                    newEvents.addAll(events.map{ event ->
                                        object : SchedulerEvent {
                                            override val id = event.id
                                            override val dateTimeStart = event.whenStart
                                            override val dateTimeFinish = event.whenFinish
                                            override val header = event.master.name
                                            override val text = event.services.joinToString()
                                            override val color = eventColorizer.getRandomColor(appState.context)
                                        }
                                    })
                                }
                            }
                        }
                        else {
                            withContext(Dispatchers.Main){
                                setInProgress(false)
                                intError.value = (requestResult as Result.Error).exception
                            }
                        }
                    }
                })
            }
            for (i in 0 .. daysBetween){
                jobs[i].await()
            }
            withContext(Dispatchers.Main){
                if (newEvents.size > 0) {
                    Log.d(javaClass.simpleName, "Loading data newEvents size = ${newEvents.size ?: 0}")
                    intNewEventsLoaded.value = newEvents
                }
                setInProgress(false)
            }
        }
    }

    override fun onCleared() {
        subscription.dispose()
        super.onCleared()
    }

    override fun restoreState(writer: StateWriter) {
//        val state: Map<String, String?> = writer.readState(this)
    }

    override fun saveState(writer: StateWriter) {
//        val state: MutableMap<String, String> = HashMap()
//        writer.writeState(this, state)
    }

    companion object {
        private const val KEY_FORMAT = "dd.MM.yyyy"
        private const val TIMEOUT = 300L
    }

    init {
        subscription = notifier
            .debounce(TIMEOUT, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ pair ->
                            loadData(pair.first, pair.second)
                        },
                        { t -> intError.value = t}
            )
    }
}
