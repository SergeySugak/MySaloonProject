package com.app.mscoremodels.saloon

import android.graphics.Color
import androidx.annotation.ColorInt
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class SaloonEvent constructor() {
    var id: String = ""
    lateinit var master: SaloonMaster
    lateinit var services: List<SaloonService>
    lateinit var client: SaloonClient
    lateinit var whenStart: Calendar
    lateinit var whenFinish: Calendar
    var description: String = ""
    @ColorInt var color: Int = Color.WHITE
    var state: SaloonEventState = SaloonEventState.esScheduled

    constructor(
        id: String, master: SaloonMaster, services: List<SaloonService>, client: SaloonClient,
        whenStart: Calendar, whenFinish: Calendar, description: String, @ColorInt color: Int,
        state: SaloonEventState
    ) : this() {
        this.id = id
        this.master = master
        this.services = services
        this.client = client
        this.whenStart = whenStart
        this.whenFinish = whenFinish
        this.description = description
        this.color = color
        this.state = state
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SaloonEvent

        if (id != other.id) return false
        if (master != other.master) return false
        if (services != other.services) return false
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
        result = 31 * result + master.hashCode()
        result = 31 * result + services.hashCode()
        result = 31 * result + client.hashCode()
        result = 31 * result + whenStart.hashCode()
        result = 31 * result + whenFinish.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + state.hashCode()
        return result
    }

    override fun toString(): String {
        return "SaloonEvent $description"
    }
}