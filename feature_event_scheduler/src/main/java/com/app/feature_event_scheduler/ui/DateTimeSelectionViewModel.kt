package com.app.feature_event_scheduler.ui

import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import java.time.Year
import java.util.*
import javax.inject.Inject

class DateTimeSelectionViewModel @Inject constructor(appState: AppStateManager): MSFragmentViewModel(appState)  {
    private val intMode = StatefulMutableLiveData(MODE_DATE)
    val mode: StatefulLiveData<Int> = intMode
    private val intCalendar = StatefulMutableLiveData<Calendar>()
    val calendar: StatefulLiveData<Calendar> = intCalendar

    fun updateMode(){
        if (intMode.value == MODE_DATE)
            intMode.value = MODE_TIME
        else
            intMode.value = MODE_DATE
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        intCalendar.value!!.set(year, month, dayOfMonth)
        intCalendar.forceSetValue(intCalendar.value)
    }

    fun setTime(hourOfDay: Int, minute: Int) {
        val year = intCalendar.value!!.get(Calendar.YEAR)
        val month = intCalendar.value!!.get(Calendar.MONTH)
        val day = intCalendar.value!!.get(Calendar.DAY_OF_MONTH)
        intCalendar.value!!.set(year, month, day, hourOfDay, minute)
        intCalendar.forceSetValue(intCalendar.value)
    }

    override fun saveState(writer: StateWriter) {

    }

    override fun restoreState(writer: StateWriter) {

    }

    companion object {
        private const val DEF_HOUR_FRACTION = 10
        const val MODE_DATE = 0
        const val MODE_TIME = 1
    }

    init {
        intCalendar.value = Calendar.getInstance()
        val minute = DEF_HOUR_FRACTION * intCalendar.value!!.get(Calendar.MINUTE) / DEF_HOUR_FRACTION
        intCalendar.value!!.set(Calendar.MINUTE, minute)
    }
}