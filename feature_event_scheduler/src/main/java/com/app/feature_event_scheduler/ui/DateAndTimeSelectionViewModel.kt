package com.app.feature_event_scheduler.ui

import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import java.util.*
import javax.inject.Inject

class DateAndTimeSelectionViewModel @Inject constructor(appState: AppStateManager): MSFragmentViewModel(appState)  {
    private val intCalendar = StatefulMutableLiveData<Calendar>()
    val calendar: StatefulLiveData<Calendar> = intCalendar

    fun setDate(year: Int, month: Int, day: Int) {
        intCalendar.value!!.set(year, month, day)
        intCalendar.forceSetValue(intCalendar.value)
    }

    fun setTime(hour: Int, minute: Int) {
        intCalendar.value!!.set(Calendar.HOUR_OF_DAY, hour)
        intCalendar.value!!.set(Calendar.MINUTE, minute)
        intCalendar.forceSetValue(intCalendar.value)
    }

    override fun saveState(writer: StateWriter) {

    }

    override fun restoreState(writer: StateWriter) {

    }

    init {
        intCalendar.value = Calendar.getInstance()
    }
}