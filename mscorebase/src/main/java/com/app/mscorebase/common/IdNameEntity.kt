package com.app.mscorebase.common

import android.os.Parcelable

interface IdNameEntity<T> : Parcelable {
    val id: T?
    val name: String?
    val idName: String?
}