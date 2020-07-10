package com.app.msa_db_repo.repository.db

import android.graphics.Color
import androidx.annotation.ColorInt
import com.app.mscoremodels.saloon.SaloonClient
import com.app.mscoremodels.saloon.SaloonEvent
import com.app.mscoremodels.saloon.SaloonEventState
import com.app.mscoremodels.saloon.SaloonUsedConsumable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class RepositoryEvent constructor() {
    var id: String = ""
    var masterId: String = ""
    var serviceIds: List<String> = mutableListOf()
    var client = SaloonClient()
    var whenStart: Long = 0
    var whenFinish: Long = 0
    var description: String = ""
    @ColorInt
    var color: Int = Color.WHITE
    var state: SaloonEventState = SaloonEventState.esScheduled
    var notes: String = ""
    var usedConsumables: List<SaloonUsedConsumable> = emptyList()
    var amount: Double = 0.0
    var userDuration: Int = 0

    constructor(event: SaloonEvent) : this() {
        this.id = event.id
        this.masterId = event.master.id
        this.serviceIds = event.services.map { s -> s.id }
        this.client = event.client
        this.whenStart = event.whenStart.timeInMillis
        this.whenFinish = event.whenFinish.timeInMillis
        this.description = event.description
        this.color = event.color
        this.state = event.state
        this.notes = event.notes
        this.usedConsumables = event.usedConsumables
        this.userDuration = event.userDuration
        this.amount = event.amount
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RepositoryEvent

        return id == other.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + masterId.hashCode()
        result = 31 * result + serviceIds.hashCode()
        result = 31 * result + client.hashCode()
        result = 31 * result + whenStart.hashCode()
        result = 31 * result + whenFinish.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + usedConsumables.hashCode()
        result = 31 * result + userDuration.hashCode()
        result = 31 * result + amount.hashCode()
        return result
    }
}