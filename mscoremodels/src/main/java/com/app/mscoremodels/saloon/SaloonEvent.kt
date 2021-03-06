package com.app.mscoremodels.saloon

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt
import com.app.view_schedule.api.SchedulerEvent
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class SaloonEvent constructor() : SchedulerEvent, Parcelable {
    var id: String = ""
    lateinit var master: SaloonMaster
    var services: List<SaloonService> = emptyList()
    lateinit var client: SaloonClient
    lateinit var savedWhenStart: Calendar
    lateinit var whenStart: Calendar
    lateinit var whenFinish: Calendar
    var userDuration: Int = 0
    lateinit var notes: String
    var description: String = ""
    var usedConsumables: List<SaloonUsedConsumable> = emptyList()
    var amount: Double = 0.0
    var usedConsumablesAmount: Double = 0.0

    @ColorInt
    var color: Int = Color.WHITE
    var state: SaloonEventState = SaloonEventState.esScheduled

    constructor(
        id: String, master: SaloonMaster, services: List<SaloonService>, client: SaloonClient,
        whenStart: Calendar, whenFinish: Calendar, description: String,
        @ColorInt color: Int, state: SaloonEventState, notes: String = "",
        userDuration: Int, usedConsumables: List<SaloonUsedConsumable> = emptyList(),
        amount: Double = 0.0, usedConsumablesAmount: Double = 0.0) : this() {
        this.id = id
        this.master = master
        this.services = services
        this.client = client
        this.savedWhenStart = whenStart.clone() as Calendar
        this.whenStart = whenStart
        this.whenFinish = whenFinish
        this.userDuration = userDuration
        this.description = description
        this.color = color
        this.state = state
        this.notes = notes
        this.usedConsumables = usedConsumables
        this.amount = amount
        this.usedConsumablesAmount = usedConsumablesAmount
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SaloonEvent
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + master.hashCode()
        result = 31 * result + services.hashCode()
        result = 31 * result + client.hashCode()
        result = 31 * result + savedWhenStart.hashCode()
        result = 31 * result + whenStart.hashCode()
        result = 31 * result + whenFinish.hashCode()
        result = 31 * result + userDuration.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + usedConsumables.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + usedConsumablesAmount.hashCode()
        return result
    }

    override fun toString(): String {
        return "SaloonEvent $description"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(master, flags)
        parcel.writeTypedList(services)
        parcel.writeParcelable(client, flags)
        parcel.writeString(description)
        parcel.writeSerializable(savedWhenStart)
        parcel.writeSerializable(whenStart)
        parcel.writeSerializable(whenFinish)
        parcel.writeSerializable(userDuration)
        parcel.writeInt(color)
        parcel.writeString(state.name)
        parcel.writeString(notes)
        parcel.writeTypedList(usedConsumables)
        parcel.writeDouble(amount)
        parcel.writeDouble(usedConsumablesAmount)
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()!!
        master = parcel.readParcelable(SaloonMaster::class.java.classLoader)!!
        services = parcel.createTypedArrayList(SaloonService)!!
        client = parcel.readParcelable(SaloonClient::class.java.classLoader)!!
        description = parcel.readString()!!
        savedWhenStart = parcel.readSerializable() as Calendar
        whenStart = parcel.readSerializable() as Calendar
        whenFinish = parcel.readSerializable() as Calendar
        userDuration = parcel.readInt()
        color = parcel.readInt()
        state = SaloonEventState.valueOf(parcel.readString()!!)
        notes = parcel.readString() ?: ""
        usedConsumables = parcel.createTypedArrayList(SaloonUsedConsumable)!!
        amount = parcel.readDouble()
        usedConsumablesAmount = parcel.readDouble()
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SaloonEvent> {
        override fun createFromParcel(parcel: Parcel): SaloonEvent {
            return SaloonEvent(parcel)
        }

        override fun newArray(size: Int): Array<SaloonEvent?> {
            return arrayOfNulls(size)
        }
    }

    override fun getEventId() = id

    override fun getDateTimeStart() = whenStart

    override fun getDateTimeFinish(): Calendar {
        return if (userDuration <= 0)
            whenFinish
        else {
            val userWhenFinish = whenStart.clone() as Calendar
            userWhenFinish.add(Calendar.MINUTE, userDuration)
            userWhenFinish
        }
    }

    override fun getHeader() = master.name

    override fun getText() = services.joinToString()

    override fun getEventColor() = color
}