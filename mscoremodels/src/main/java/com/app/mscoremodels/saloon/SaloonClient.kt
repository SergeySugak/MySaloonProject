package com.app.mscoremodels.saloon

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class SaloonClient constructor() : Parcelable {
    var name: String = ""
    var phone: String = ""
    var email: String = ""

    constructor(parcel: Parcel) : this() {
        name = parcel.readString() ?: ""
        phone = parcel.readString() ?: ""
        email = parcel.readString() ?: ""
    }

    constructor(
        name: String,
        phone: String,
        email: String
    ) : this() {
        this.name = name
        this.phone = phone
        this.email = email
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SaloonClient> {
        override fun createFromParcel(parcel: Parcel): SaloonClient {
            return SaloonClient(parcel)
        }

        override fun newArray(size: Int): Array<SaloonClient?> {
            return arrayOfNulls(size)
        }
    }
}