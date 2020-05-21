package com.app.feature_event_scheduler.ui

import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import java.util.*
import javax.inject.Inject

class DateTimeSelectionViewModel @Inject constructor(appState: AppStateManager): MSFragmentViewModel(appState)  {
    private val intMode = StatefulMutableLiveData(MODE_DATE)
    val mode: StatefulLiveData<Int> = intMode
    var hourOfDay: Int
    var minute: Int

    fun updateMode(){
        if (intMode.value == MODE_DATE)
            intMode.value = MODE_TIME
        else
            intMode.value = MODE_DATE
    }

    override fun saveState(writer: StateWriter) {

    }

    override fun restoreState(writer: StateWriter) {

    }

    fun setTime(hourOfDay: Int, minute: Int) {

    }

    companion object {
        private const val DEF_HOUR_FRACTION = 10
        const val MODE_DATE = 0
        const val MODE_TIME = 1
    }

    init {
        val now = Calendar.getInstance()
        hourOfDay = now.get(Calendar.HOUR_OF_DAY)
        minute = DEF_HOUR_FRACTION * now.get(Calendar.MINUTE) / DEF_HOUR_FRACTION
    }
}