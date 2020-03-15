package com.app.mscorebase.ui.dialogs.choicedialog

import android.os.Parcel
import android.os.Parcelable
import com.app.mscorebase.common.IdNameEntity
import java.io.Serializable

open class ChoiceItem<T : Serializable?>(override var id: T?, override var name: String?) : IdNameEntity<T>, Parcelable {

    override val idName: String
        get() = id?.toString() + " - " + name

    var isEnabled: Boolean = true
    var isVisible: Boolean = true
    var isSelected: Boolean = false

    private constructor() : this(null, null) {
        isEnabled = true
        isVisible = true
        isSelected = false
    }

    constructor(
        id: T,
        name: String?,
        enabled: Boolean,
        visible: Boolean
    ) : this(id, name) {
        isEnabled = enabled
        isVisible = visible
    }

    constructor(
        id: T,
        name: String?,
        enabled: Boolean,
        visible: Boolean,
        selected: Boolean
    ) : this(id, name, enabled, visible) {
        isSelected = selected
    }

    private constructor(`in`: Parcel) : this(null, null) {
        @Suppress("UNCHECKED_CAST")
        id = `in`.readSerializable() as T?
        name = `in`.readString()
        isEnabled = `in`.readByte().toInt() != 0
        isVisible = `in`.readByte().toInt() != 0
        isSelected = `in`.readByte().toInt() != 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(id)
        dest.writeString(name)
        dest.writeByte((if (isEnabled) 1 else 0).toByte())
        dest.writeByte((if (isVisible) 1 else 0).toByte())
        dest.writeByte((if (isSelected) 1 else 0).toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    enum class ChoiceMode {
        cmSingle, cmMulti
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ChoiceItem<*>> =
            object : Parcelable.Creator<ChoiceItem<*>> {
                override fun createFromParcel(`in`: Parcel): ChoiceItem<*> {
                    return ChoiceItem<Serializable>(
                        `in`
                    )
                }

                override fun newArray(size: Int): Array<ChoiceItem<*>?> {
                    return arrayOfNulls(size)
                }
            }
    }
}