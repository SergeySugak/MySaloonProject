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
import kotlinx.coroutines.withContext
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
    private val intEventInfoSaveState = StatefulMutableLiveData<ActionType>()
    val eventInfoSaveState: StatefulLiveData<ActionType> = intEventInfoSaveState
    private val intUsedConsumables = StatefulMutableLiveData<List<SaloonUsedConsumable>>()
    val usedConsumables: StatefulLiveData<List<SaloonUsedConsumable>> = intUsedConsumables

    var masterId: String = ""
        private set
    private var clientName: String = ""
    private var clientPhone: String = ""
    private var clientEmail: String = ""
    private var description: String = ""
    private var notes: String = ""
    private var state: SaloonEventState = SaloonEventState.esScheduled
    private var userDuration: Int = 0
    private var amount: Double = 0.0
    private var usedConsumablesAmount: Double = 0.0

    fun setEventDate(year: Int, month: Int, day: Int) {
        intCalendar.value!!.set(year, month, day)
        intCalendar.forceSetValue(intCalendar.value)
    }

    fun setEventTime(hour: Int, minute: Int) {
        intCalendar.value!!.set(Calendar.HOUR_OF_DAY, hour)
        intCalendar.value!!.set(Calendar.MINUTE, minute)
        intCalendar.forceSetValue(intCalendar.value)
    }

    fun setEventDateTime(calendar: Calendar){
        intCalendar.value = calendar
    }

    fun setServices(services: List<ChoosableSaloonService>) {
        intServices.value = saloonFactory.convertToSaloonServices(services)
    }

    fun setConsumables(consumables: List<ChoosableSaloonConsumable>) {
        intUsedConsumables.value = saloonFactory.convertToSaloonConsumables(consumables)
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

    fun setDescription(description: String) {
        this.description = description
    }

    fun setNotes(notes: String) {
        this.notes = notes
    }

    fun setDone(done: Boolean) {
        state = if (done) SaloonEventState.esDone else SaloonEventState.esScheduled
    }

    fun getDone(): Boolean {
        return state === SaloonEventState.esDone
    }

    fun setEvent(event: SaloonEvent?) {
        intEventInfo.postValue(event)
        if (event != null) {
            intMaster.postValue(event.master)
            intServices.postValue(event.services)
            intCalendar.postValue(event.whenStart)
            intUsedConsumables.postValue(event.usedConsumables)
            notes = event.notes
            description = event.description
            state = event.state
            userDuration = event.userDuration
            amount = event.amount
            usedConsumablesAmount = event.usedConsumablesAmount
        }
    }

    fun saveEventInfo(action: ActionType) {
        if (action !== ActionType.DELETE) {
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
        }
        setInProgress(true)
        viewModelScope.launch(Dispatchers.IO) {
            if (action === ActionType.DELETE) {
                val event = eventInfo.value
                if (event != null) {
                    val result = dbRepository.deleteEventInfo(eventInfo.value!!)
                    if (result is Result.Success) {
                        intEventInfoSaveState.postValue(action)
                        stopProgress()
                    } else {
                        intError.postValue((result as Result.Error).exception)
                        stopProgress()
                        intEventInfoSaveState.postValue(ActionType.ERROR)
                    }
                } else {
                    intEventInfoSaveState.postValue(ActionType.ERROR)
                }
            } else {
                val event = createEvent(description, notes)
                val result = dbRepository.saveEventInfo(event)
                if (result is Result.Success) {
                    event.savedWhenStart = event.whenStart
                    withContext(Dispatchers.Main) {
                        intEventInfo.value = event
                        intEventInfoSaveState.value = action
                    }
                    stopProgress()
                } else {
                    intError.postValue((result as Result.Error).exception)
                    stopProgress()
                    intEventInfoSaveState.postValue(ActionType.ERROR)
                }
            }
        }
    }

    private suspend fun stopProgress() {
        withContext(Dispatchers.Main) {
            setInProgress(false)
        }
    }

    private fun createEvent(description: String, notes: String): SaloonEvent {
        val duration = getTotalServicesPlanDuration(services.value)
        val whenStart = calendar.value!!
        val whenFinish = whenStart.clone() as Calendar
        whenFinish.add(Calendar.MINUTE, duration)
        val client = saloonFactory.createSaloonClient(
            clientName ?: "",
            clientPhone ?: "",
            clientEmail ?: ""
        )

        return if (eventInfo.value == null) {
            saloonFactory.createSaloonEvent(
                "",
                master.value!!, services.value!!, client,
                whenStart, whenFinish, description,
                eventColorizer.getRandomColor(appState.context),
                notes, userDuration, intUsedConsumables.value.orEmpty(),
                amount, usedConsumablesAmount)
        } else {
            val evt = eventInfo.value!!
            evt.master = master.value!!
            evt.services = services.value!!
            evt.client = client
            evt.whenStart = whenStart
            evt.whenFinish = whenFinish
            evt.description = description
            evt.notes = notes
            evt.state = state
            evt.userDuration = userDuration
            evt.usedConsumables = intUsedConsumables.value.orEmpty()
            evt.amount = amount
            evt.usedConsumablesAmount = usedConsumablesAmount
            evt
        }
    }

    override fun saveState(writer: StateWriter) {

    }

    override fun restoreState(writer: StateWriter) {

    }

    fun getTotalServicesPlanDuration(services: List<SaloonService>?): Int {
        var result = 0
        services?.forEach {
            result += it.duration?.duration ?: 0
        }
        return result
    }

    fun getTotalWorkAmount(): Double {
        var result = 0.0
        services.value.orEmpty().forEach {
            result += it.price
        }
        return result
    }

    fun getTotalConsumablesAmount(): Double {
        var result = 0.0
        usedConsumables.value.orEmpty().forEach {
            result += it.price * it.qty
        }
        return result
    }

    fun getTotalAmount() = getTotalWorkAmount() + getTotalConsumablesAmount()

    fun setUserDuration(duration: Int?) {
        userDuration = duration ?: 0
    }

    fun getUserDuration() = userDuration

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
