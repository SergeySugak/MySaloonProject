package com.app.mscorebase.livedata

import androidx.lifecycle.LiveData

open class StatefulLiveData<T>: LiveData<T> {
    var isHandled = false

    constructor(value: T) : super(value)
    constructor() : super()
}