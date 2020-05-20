package com.app.feature_event_scheduler.ui

import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.SaloonEvent
import com.app.mscoremodels.saloon.SaloonFactory
import com.app.mscoremodels.saloon.SaloonMaster
import com.app.mscoremodels.saloon.SaloonService
import javax.inject.Inject

class EventSchedulerViewModel @Inject constructor(appState: AppStateManager,
                                                  private val saloonFactory: SaloonFactory,
                                                  private val dbRepository: DbRepository): MSFragmentViewModel(appState) {
    private val intEventInfoSaveState = StatefulMutableLiveData<Boolean>()
    val eventInfoSaveState: StatefulLiveData<Boolean> = intEventInfoSaveState
    var eventId: String = ""
    private val intEventInfo = StatefulMutableLiveData<SaloonEvent>()
    val eventInfo: StatefulLiveData<SaloonEvent> = intEventInfo

    var masterId: String = ""
    private val intMasterInfo = StatefulMutableLiveData<SaloonMaster>()
    val masterInfo: StatefulLiveData<SaloonMaster> = intMasterInfo
    private val intMasterServices = StatefulMutableLiveData<List<SaloonService>>()
    val masterServices: StatefulLiveData<List<SaloonService>> = intMasterServices


    override fun saveState(writer: StateWriter) {

    }

    override fun restoreState(writer: StateWriter) {

    }


}
