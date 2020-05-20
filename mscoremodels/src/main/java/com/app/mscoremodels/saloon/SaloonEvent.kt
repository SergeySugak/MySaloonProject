package com.app.mscoremodels.saloon

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class SaloonEvent constructor() {
    var id: String = ""
    lateinit var who: SaloonMaster
    lateinit var what: List<SaloonService>
    lateinit var client: SaloonClient
    lateinit var whenStart: Calendar
    lateinit var whenFinish: Calendar
    var description: String = ""
    var state: SaloonEventState = SaloonEventState.esScheduled

    constructor(id: String, who: SaloonMaster, what: List<SaloonService>, client: SaloonClient,
                whenStart: Calendar, whenFinish: Calendar, description: String,
                state: SaloonEventState): this(){
        this.id = id
        this.who = who
        this.what = what
        this.client = client
        this.whenStart = whenStart
        this.whenFinish = whenFinish
        this.description = description
        this.state = state
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SaloonEvent

        if (id != other.id) return false
        if (who != other.who) return false
        if (what != other.what) return false
        if (client != other.client) return false
        if (whenStart != other.whenStart) return false
        if (whenFinish != other.whenFinish) return false
        if (description != other.description) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + who.hashCode()
        result = 31 * result + what.hashCode()
        result = 31 * result + client.hashCode()
        result = 31 * result + whenStart.hashCode()
        result = 31 * result + whenFinish.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + state.hashCode()
        return result
    }

    override fun toString(): String {
        return "SaloonEvent $description"
    }
}