package com.app.feature_schedule.ui

import androidx.lifecycle.viewModelScope
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.SaloonEvent
import com.app.view_schedule.api.SchedulerEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleViewModel
@Inject constructor(
    appState: AppStateManager,
    private val dbRepository: DbRepository
) : MSFragmentViewModel(appState) {
    private val eventsMap = ConcurrentHashMap<String, MutableList<SaloonEvent>>()
    private var subscription: Disposable
    private var filter: String = ""
    private val dateFormatter = SimpleDateFormat(KEY_FORMAT, Locale.getDefault())
    val notifier: PublishSubject<Pair<Calendar, Calendar>> = PublishSubject.create()
    private val intNewEventsLoaded = StatefulMutableLiveData<List<SchedulerEvent>>()
    val newEventsLoaded: StatefulLiveData<List<SchedulerEvent>> = intNewEventsLoaded
    private val intEventDeleted = StatefulMutableLiveData<SaloonEvent>()
    val eventDeleted: StatefulLiveData<SaloonEvent> = intEventDeleted

    private fun getKey(date: Calendar) = dateFormatter.format(date.time)

    private fun daysBetween(from: Calendar, to: Calendar) =
        TimeUnit.DAYS.convert(to.time.time - from.time.time, TimeUnit.MILLISECONDS).toInt()

    fun loadData(from: Calendar, to: Calendar, force: Boolean = false) {
        setInProgress(true)
        val newEvents = mutableListOf<SchedulerEvent>()
        val daysBetween = daysBetween(from, to)
        val jobs = mutableListOf<Deferred<*>>()
        viewModelScope.launch(Dispatchers.IO){
            for (i in 0 .. daysBetween){
                jobs.add(i, async {
                    val date = from.clone() as Calendar
                    date.add(Calendar.DATE, i)
                    var events: MutableList<SaloonEvent>
                    val key = getKey(date)
                    if (!eventsMap.containsKey(key) || force) {
                        val requestResult = dbRepository.getEvents(date)
                        if (requestResult is Result.Success){
                            events = requestResult.data.toMutableList()
                            if (eventsMap[key] != null) {
                                events.removeAll{ event ->
                                    eventsMap[key]?.contains(event) ?: false
                                }
                                events = filterEvents(events, filter)
                                eventsMap[key]!!.addAll(events)
                            }
                            else {
                                events = filterEvents(events, filter)
                                eventsMap[key] = mutableListOf<SaloonEvent>().apply { addAll(events) }
                            }
                            if (events.size > 0) {
                                synchronized(this){
                                    newEvents.addAll(events)
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
                    intNewEventsLoaded.value = newEvents
                }
                setInProgress(false)
            }
        }
    }

    private fun filterEvents(events: MutableList<SaloonEvent>, filter: String): MutableList<SaloonEvent> {
        return events.filter { event ->
            event.client.name.contains(filter, true) ||
            event.client.email.contains(filter, true) ||
            event.client.phone.contains(filter, true) ||
            event.master.name.contains(filter, true) ||
            event.description.contains(filter, true) ||
            event.notes.contains(filter, true)
        }.toMutableList()
    }

    fun onEventInserted(event: SaloonEvent) {
        loadData(event.whenStart, event.whenStart, true)
    }

    fun onEventUpdated(event: SaloonEvent) {
        removeEvent(event)
        loadData(event.whenStart, event.whenStart, true)
    }

    fun onEventDeleted(event: SaloonEvent) {
        removeEvent(event)
    }

    private fun removeEvent(deletedEvent: SaloonEvent){
        val mapIterator = eventsMap.iterator()
        var mapEntry: MutableList<SaloonEvent>
        while (mapIterator.hasNext()){
            mapEntry = mapIterator.next().value
            val iterator = mapEntry.iterator()
            var event: SaloonEvent
            while (iterator.hasNext()){
                event = iterator.next()
                if (event == deletedEvent){
                    iterator.remove()
                    intEventDeleted.value = deletedEvent
                    return
                }
            }
        }
    }

    override fun onCleared() {
        subscription.dispose()
        super.onCleared()
    }

    override fun restoreState(writer: StateWriter) {
        val state = writer.readState(this) ?: return
        filter = state[STATE_FILTER] ?: ""
//        val type = object: TypeToken<ConcurrentHashMap<String, MutableList<SaloonEvent>>>(){}.type
//        eventsMap.putAll(gson.fromJson(state[STATE_EVENTS_MAP], type))
    }

    override fun saveState(writer: StateWriter) {
        val state = hashMapOf<String, String>()
        state[STATE_FILTER] = filter
//        state[STATE_EVENTS_MAP] = gson.toJson(eventsMap)
        writer.writeState(this, state)
    }

    fun setFilter(filter: String?) {
        this.filter = filter ?: ""
    }

    fun getFilter() = filter

    fun applyFilter(from: Calendar, to: Calendar){
        eventsMap.clear()
        loadData(from, to)
    }

    companion object {
        private const val KEY_FORMAT = "dd.MM.yyyy"
        private const val TIMEOUT = 300L

        private val STATE_EVENTS_MAP: String = "${ScheduleViewModel::class.java.simpleName}_STATE_EVENTS_MAP"
        private val STATE_FILTER: String = "${ScheduleViewModel::class.java.simpleName}_STATE_FILTER"
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
