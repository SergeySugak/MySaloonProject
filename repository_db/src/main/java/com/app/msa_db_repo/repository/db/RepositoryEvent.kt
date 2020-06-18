package com.app.msa_db_repo.repository.db

import android.graphics.Color
import androidx.annotation.ColorInt
import com.app.mscoremodels.saloon.*
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class RepositoryEvent constructor() {
    var id: String = ""
    lateinit var masterId: String
    lateinit var serviceIds: List<String>
    lateinit var client: SaloonClient
    var whenStart: Long = 0
    var whenFinish: Long = 0
    var description: String = ""
    @ColorInt var color: Int = Color.WHITE
    var state: SaloonEventState = SaloonEventState.esScheduled

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
        if (color != other.color) return false
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
        result = 31 * result + color.hashCode()
        result = 31 * result + state.hashCode()
        return result
    }
}