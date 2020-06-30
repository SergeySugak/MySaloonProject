package com.app.mscoremodels.saloon

import androidx.annotation.ColorInt
import com.app.mscorebase.appstate.AppStateManager
import java.util.*
import javax.inject.Inject

class SaloonFactory @Inject constructor(val appState: AppStateManager) {
    fun createSaloonService(
        id: String, name: String, price: Double,
        duration: ServiceDuration?, description: String,
        imageUrl: String = ""
    ) =
        SaloonService(id, name, price, duration, description, imageUrl)

    fun createSaloonMaster(
        id: String, name: String, description: String,
        portfolioUrl: String = "", imageUrl: String = ""
    ) =
        SaloonMaster(id, name, description, portfolioUrl, imageUrl)

    fun createSaloonClient(name: String, phone: String, email: String) =
        SaloonClient(name, phone, email)

    fun createSaloonEvent(
        id: String, master: SaloonMaster,
        services: List<SaloonService>, client: SaloonClient,
        whenStart: Calendar, whenFinish: Calendar, description: String, @ColorInt color: Int,
        notes: String, userDuration: Int = 0, usedConsumables: List<SaloonConsumable> = emptyList(),
        amount: Double = 0.0, state: SaloonEventState = SaloonEventState.esScheduled
    ) =
        SaloonEvent(
            id,
            master,
            services,
            client,
            whenStart,
            whenFinish,
            description,
            color,
            state,
            notes,
            userDuration,
            usedConsumables,
            amount
        )

    fun createServiceDuration(duration: Int?, name: String?) = ServiceDuration(duration, name)
    fun createServiceDuration(duration: ChoosableServiceDuration?) =
        ServiceDuration(duration?.serviceDuration?.duration, duration?.serviceDuration?.description)

    fun createChoosableServiceDuration(duration: ServiceDuration?) =
        ChoosableServiceDuration(duration)

    fun createChoosableServiceDurations(serviceDurations: List<ServiceDuration>): List<ChoosableServiceDuration> {
        val result = mutableListOf<ChoosableServiceDuration>()
        serviceDurations.mapTo(result) { serviceDuration ->
            ChoosableServiceDuration(serviceDuration)
        }
        return result
    }

    fun convertToChoosableServices(servicesList: List<SaloonService>): List<ChoosableSaloonService> {
        val result = mutableListOf<ChoosableSaloonService>()
        servicesList.forEach { saloonService ->
            ChoosableSaloonService(saloonService)
        }
        return result
    }

    fun convertToSaloonServices(servicesList: List<ChoosableSaloonService>): List<SaloonService> {
        val result = mutableListOf<SaloonService>()
        servicesList.forEach { choosableSaloonService ->
            result.add(choosableSaloonService.service)
        }
        return result
    }

    fun createChoosableServices(
        allServicesList: List<SaloonService>,
        selectedServicesList: List<SaloonService>
    ): List<ChoosableSaloonService> {
        val result = mutableListOf<ChoosableSaloonService>()
        allServicesList.mapTo(result) { saloonService ->
            ChoosableSaloonService(saloonService).apply {
                isSelected = selectedServicesList.indexOf(saloonService) != -1
            }
        }
        return result
    }

    fun convertToSaloonMasters(mastersList: List<ChoosableSaloonMaster>): List<SaloonMaster> {
        val result = mutableListOf<SaloonMaster>()
        mastersList.forEach { choosableSaloonMaster ->
            result.add(choosableSaloonMaster.master)
        }
        return result
    }

    fun createChoosableMasters(
        allMastersList: List<SaloonMaster>,
        selectedMastersList: List<SaloonMaster>
    ): List<ChoosableSaloonMaster> {
        val result = mutableListOf<ChoosableSaloonMaster>()
        allMastersList.mapTo(result) { saloonMaster ->
            ChoosableSaloonMaster(saloonMaster).apply {
                isSelected = selectedMastersList.indexOf(saloonMaster) != -1
            }
        }
        return result
    }

    fun createChoosableEvents(
        allEventsList: List<SaloonEvent>,
        selectedEventsList: List<SaloonEvent>,
        filter: String
    ): List<ChoosableSaloonEvent> {
        val result = mutableListOf<ChoosableSaloonEvent>()
        allEventsList
            .filter{ event ->
                event.client.name.contains(filter, true) ||
                event.client.email.contains(filter, true) ||
                event.client.phone.contains(filter, true) ||
                event.master.name.contains(filter, true) ||
                event.description.contains(filter, true) ||
                event.notes.contains(filter, true)
            }
            .sortedByDescending { it.whenStart }
            .mapTo(result) { saloonEvent ->
            ChoosableSaloonEvent(appState.context, saloonEvent).apply {
                isSelected = selectedEventsList.indexOf(saloonEvent) != -1
            }
        }
        return result
    }
}