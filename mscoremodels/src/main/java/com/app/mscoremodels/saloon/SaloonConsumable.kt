package com.app.mscoremodels.saloon

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class SaloonConsumable constructor() : Parcelable, Serializable {
    var id: String = ""
    var name: String = ""
    var uom: String = ""
    var price: Double = 0.0

    constructor(id: String, name: String, price: Double, uom: String): this() {
        this.id = id
        this.name = name
        this.price = price
        this.uom = uom
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString() ?: ""
        name = parcel.readString() ?: ""
        uom = parcel.readString() ?: ""
        price = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(uom)
        parcel.writeDouble(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SaloonConsumable

        if (id != other.id) return false
        if (name != other.name) return false
        if (uom != other.uom) return false
        if (price != other.price) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + uom.hashCode()
        result = 31 * result + price.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<SaloonConsumable> {
        override fun createFromParcel(parcel: Parcel): SaloonConsumable {
            return SaloonConsumable(parcel)
        }

        override fun newArray(size: Int): Array<SaloonConsumable?> {
            return arrayOfNulls(size)
        }
    }


}