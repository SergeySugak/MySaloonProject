package com.app.mscoremodels.saloon

import android.os.Parcel
import android.os.Parcelable

class SaloonUsedConsumable : SaloonConsumable {
    var qty: Int = 0

    constructor(): super()

    constructor(id: String, name: String, price: Double, uom: String, qty: Int): super(id, name, price, uom) {
        this.qty = qty
    }

    constructor(parcel: Parcel) : super(parcel) {
        qty = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(qty)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SaloonUsedConsumable> {
        override fun createFromParcel(parcel: Parcel): SaloonUsedConsumable {
            return SaloonUsedConsumable(parcel)
        }

        override fun newArray(size: Int): Array<SaloonUsedConsumable?> {
            return arrayOfNulls(size)
        }
    }
}