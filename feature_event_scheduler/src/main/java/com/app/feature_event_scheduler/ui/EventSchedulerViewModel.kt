package com.app.feature_event_scheduler.ui

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.feature_event_scheduler.R
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.Colorizer
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class EventSchedulerViewModel @Inject constructor(
    private val appState: AppStateManager,
    private val saloonFactory: SaloonFactory,
    private val dbRepository: DbRepository,
    private val eventColorizer: Colorizer
    ) : MSFragmentViewModel(appState) {

    private val intCalendar = StatefulMutableLiveData<Calendar>()
    val calendar: LiveData<Calendar> = intCalendar
    private val intMaster = StatefulMutableLiveData<SaloonMaster>()
    val master: StatefulLiveData<SaloonMaster> = intMaster
    private val intServices = StatefulMutableLiveData<List<SaloonService>>()
    val services: StatefulLiveData<List<SaloonService>> = intServices
    private val intEventInfo = StatefulMutableLiveData<SaloonEvent>()
    val eventInfo: StatefulLiveData<SaloonEvent> = intEventInfo
    private val intEventInfoSaveState = StatefulMutableLiveData<Boolean>()
    val eventInfoSaveState: StatefulLiveData<Boolean> = intEventInfoSaveState

    var masterId: String = ""
        private set
    private var clientName: String = ""
        private set
    private var clientPhone: String = ""
        private set
    private var clientEmail: String = ""
        private set
    var eventId: String = ""
        private set

    fun setEventDate(year: Int, month: Int, day: Int) {
        intCalendar.value!!.set(year, month, day)
        intCalendar.forceSetValue(intCalendar.value)
    }

    fun setEventTime(hour: Int, minute: Int) {
        intCalendar.value!!.set(Calendar.HOUR_OF_DAY, hour)
        intCalendar.value!!.set(Calendar.MINUTE, minute)
        intCalendar.forceSetValue(intCalendar.value)
    }

    fun setServices(services: List<ChoosableSaloonService>) {
        intServices.value = saloonFactory.convertToSaloonServices(services)
    }

    fun setMaster(masters: List<ChoosableSaloonMaster>) {
        if (masters.isEmpty()) {
            masterId = ""
            intMaster.value = null
        } else {
            masterId = masters[0].id ?: ""
            intMaster.value = saloonFactory.convertToSaloonMasters(masters)[0]
        }
    }

    fun setClientInfo(clientName: String, clientPhone: String, clientEmail: String) {
        this.clientName = clientName
        this.clientPhone = clientPhone
        this.clientEmail = clientEmail
    }

    fun loadEvent(eventId: String) {
        this.eventId = eventId
        if (!TextUtils.isEmpty(eventId)) {
            return
        }
    }

    fun saveEventInfo(description: String) {
        if (intMaster.value == null) {
            intError.value =
                Exception(appState.context.getString(R.string.str_master_empty))
            return
        }
        if (intServices.value == null) {
            intError.value =
                Exception(appState.context.getString(R.string.str_services_empty))
            return
        }
        if (TextUtils.isEmpty(clientName) && TextUtils.isEmpty(clientPhone)) {
            intError.value =
                Exception(appState.context.getString(R.string.str_name_and_phone_empty))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            intEventInfoSaveState.postValue(
                run {
                    var duration = 0
                    services.value!!.forEach {
                        duration += it.duration?.duration ?: 0
                    }
                    val whenStart = calendar.value!!
                    val whenFinish = whenStart.clone() as Calendar
                    whenFinish.add(Calendar.MINUTE, duration)
                    val client = saloonFactory.createSaloonClient(
                        clientName ?: "",
                        clientPhone ?: "",
                        clientEmail ?: ""
                    )
                    val event = saloonFactory.createSaloonEvent(
                        eventId,
                        master.value!!, services.value!!, client,
                        whenStart, whenFinish, description,
                        eventColorizer.getRandomColor(appState.context)
                    )
                    val result = dbRepository.saveEventInfo(event)
                    if (result is Result.Success) {
                        eventId = event.id
                        dbRepository.saveMasterServicesInfo(
                            event.id,
                            intServices.value ?: emptyList()
                        )
                        result.data
                    } else {
                        intError.postValue((result as Result.Error).exception)
                        false
                    }
                }
            )
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
        val minute =
            intCalendar.value!!.get(Calendar.MINUTE) / DEF_HOUR_FRACTION * DEF_HOUR_FRACTION
        intCalendar.value!!.set(Calendar.MINUTE, minute)
    }
}
