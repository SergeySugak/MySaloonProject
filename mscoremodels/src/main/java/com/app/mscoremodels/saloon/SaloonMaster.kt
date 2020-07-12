package com.app.mscoremodels.saloon

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class SaloonMaster constructor() : Parcelable, Comparable<SaloonMaster> {
    var id: String = ""
    var name: String = ""
    var description: String = ""
    var portfolioUrl: String = ""
    var imageUrl: String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString() ?: ""
        name = parcel.readString() ?: ""
        description = parcel.readString() ?: ""
        portfolioUrl = parcel.readString() ?: ""
        imageUrl = parcel.readString() ?: ""
    }

    constructor(
        id: String, name: String, description: String,
        portfolioUrl: String = "", imageUrl: String = ""
    ) : this() {
        this.id = id
        this.name = name
        this.description = description
        this.portfolioUrl = portfolioUrl
        this.imageUrl = imageUrl
    }

    override fun compareTo(other: SaloonMaster) = name.compareTo(other.name)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SaloonMaster

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (portfolioUrl != other.portfolioUrl) return false
        if (imageUrl != other.imageUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + portfolioUrl.hashCode()
        result = 31 * result + imageUrl.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(portfolioUrl)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SaloonMaster> {
        override fun createFromParcel(parcel: Parcel): SaloonMaster {
            return SaloonMaster(parcel)
        }

        override fun newArray(size: Int): Array<SaloonMaster?> {
            return arrayOfNulls(size)
        }
    }
}