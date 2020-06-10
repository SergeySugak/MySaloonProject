package com.app.feature_event_scheduler.ui

import androidx.lifecycle.LiveData
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.*
import java.util.*
import javax.inject.Inject

class EventSchedulerViewModel @Inject constructor(appState: AppStateManager,
                                                  private val saloonFactory: SaloonFactory,
                                                  private val dbRepository: DbRepository): MSFragmentViewModel(appState) {
    private val intCalendar = StatefulMutableLiveData<Calendar>()
    val calendar: LiveData<Calendar> = intCalendar

    private val intEventInfoSaveState = StatefulMutableLiveData<Boolean>()
    val eventInfoSaveState: StatefulLiveData<Boolean> = intEventInfoSaveState
    var eventId: String = ""
    private val intEventInfo = StatefulMutableLiveData<SaloonEvent>()
    val eventInfo: StatefulLiveData<SaloonEvent> = intEventInfo

    var masterId: String = ""
        private set
    private val intMaster = StatefulMutableLiveData<SaloonMaster>()
    val master: StatefulLiveData<SaloonMaster> = intMaster
    private val intMasterServices = StatefulMutableLiveData<List<SaloonService>>()
    val masterServices: StatefulLiveData<List<SaloonService>> = intMasterServices

    fun setEventDate(year: Int, month: Int, day: Int) {
        intCalendar.value!!.set(year, month, day)
        intCalendar.forceSetValue(intCalendar.value)
    }

    fun setEventTime(hour: Int, minute: Int) {
        intCalendar.value!!.set(Calendar.HOUR_OF_DAY, hour)
        intCalendar.value!!.set(Calendar.MINUTE, minute)
        intCalendar.forceSetValue(intCalendar.value)
    }

    fun setMasterServices(services: List<ChoosableSaloonService>){
        intMasterServices.value = saloonFactory.convertToSaloonServices(services)
    }

    fun setMaster(masters: List<ChoosableSaloonMaster>){
        if (masters.isEmpty()){
            masterId = ""
            intMaster.value = null
        }
        else {
            masterId = masters[0].id ?: ""
            intMaster.value = saloonFactory.convertToSaloonMasters(masters)[0]
        }
    }

    override fun saveState(writer: StateWriter) {

    }

    override fun restoreState(writer: StateWriter) {

    }

    companion object {
        private const val DEF_HOUR_FRACTION = 15
    }

    init {
        intCalendar.value = Calendar.getInstance()
        intCalendar.value!!.add(Calendar.DAY_OF_MONTH, 1)
        val minute = intCalendar.value!!.get(Calendar.MINUTE) / DEF_HOUR_FRACTION * DEF_HOUR_FRACTION
        intCalendar.value!!.set(Calendar.MINUTE, minute)
    }
}
