package com.app.feature_event_scheduler.ui

import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.SaloonEvent
import javax.inject.Inject

class EventSchedulerViewModel @Inject constructor(appState: AppStateManager): MSFragmentViewModel(appState) {
    private val intEventInfoSaveState = StatefulMutableLiveData<Boolean>()
    val eventInfoSaveState: StatefulLiveData<Boolean> = intEventInfoSaveState
    var eventId: String = ""
    private val intEventInfo = StatefulMutableLiveData<SaloonEvent>()
    val eventInfo: StatefulLiveData<SaloonEvent> = intEventInfo

    override fun saveState(writer: StateWriter) {

    }

    override fun restoreState(writer: StateWriter) {

    }


}
