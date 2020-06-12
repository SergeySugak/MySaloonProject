package com.app.msa_db_repo.repository.db

import com.app.mscoremodels.saloon.SaloonClient
import com.app.mscoremodels.saloon.SaloonEvent
import com.app.mscoremodels.saloon.SaloonEventState
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class RepositoryEvent constructor() {
    var id: String = ""
    lateinit var masterId: String
    lateinit var serviceIds: List<String>
    lateinit var client: SaloonClient
    lateinit var whenStart: Calendar
    lateinit var whenFinish: Calendar
    var description: String = ""
    var state: SaloonEventState = SaloonEventState.esScheduled

    constructor(event: SaloonEvent) : this() {
        this.id = event.id
        this.masterId = event.master.id
        this.serviceIds = event.services.map{ s -> s.id }
        this.client = event.client
        this.whenStart = event.whenStart
        this.whenFinish = event.whenFinish
        this.description = event.description
        this.state = event.state
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RepositoryEvent

        if (id != other.id) return false
        if (masterId != other.masterId) return false
        if (serviceIds != other.serviceIds) return false
        if (client != other.client) return false
        if (whenStart != other.whenStart) return false
        if (whenFinish != other.whenFinish) return false
        if (description != other.description) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + masterId.hashCode()
        result = 31 * result + serviceIds.hashCode()
        result = 31 * result + client.hashCode()
        result = 31 * result + whenStart.hashCode()
        result = 31 * result + whenFinish.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + state.hashCode()
        return result
    }
}