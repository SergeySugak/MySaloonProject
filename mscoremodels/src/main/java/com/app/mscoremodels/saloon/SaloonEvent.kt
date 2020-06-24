package com.app.mscoremodels.saloon

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt
import com.app.view_schedule.api.SchedulerEvent
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class SaloonEvent constructor(): SchedulerEvent,  Parcelable {
    var id: String = ""
    lateinit var master: SaloonMaster
    lateinit var services: List<SaloonService>
    lateinit var client: SaloonClient
    lateinit var savedWhenStart: Calendar
    lateinit var whenStart: Calendar
    lateinit var whenFinish: Calendar
    lateinit var notes: String
    var description: String = ""
    @ColorInt var color: Int = Color.WHITE
    var state: SaloonEventState = SaloonEventState.esScheduled

    constructor(
        id: String, master: SaloonMaster, services: List<SaloonService>, client: SaloonClient,
         whenStart: Calendar, whenFinish: Calendar, description: String,
        @ColorInt color: Int, state: SaloonEventState, notes: String = ""
    ) : this() {
        this.id = id
        this.master = master
        this.services = services
        this.client = client
        this.savedWhenStart = whenStart.clone() as Calendar
        this.whenStart = whenStart
        this.whenFinish = whenFinish
        this.description = description
        this.color = color
        this.state = state
        this.notes = notes
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
        result = 31 * result + description.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + notes.hashCode()
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
        parcel.writeInt(color)
        parcel.writeString(state.name)
        parcel.writeString(notes)
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
        color = parcel.readInt()
        state = SaloonEventState.valueOf(parcel.readString()!!)
        notes = parcel.readString() ?: ""
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

    override fun getDateTimeFinish() = whenFinish

    override fun getHeader() = master.name

    override fun getText() = services.joinToString()

    override fun getEventColor() = color
}