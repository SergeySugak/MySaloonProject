package com.app.mscorebase.livedata

open class StatefulMutableLiveData<T> :
    StatefulLiveData<T> {
    constructor(value: T) : super(value) {}
    constructor() : super() {}

    public override fun setValue(value: T?) {
        if (value != null && value !== getValue() ||
            getValue() != null && getValue() !== value
        ) {
            isHandled = false
        }
        super.setValue(value)
    }

    fun forceSetValue(value: T?) {
        isHandled = false
        super.setValue(value)
    }

    public override fun postValue(value: T?) {
        if (value != null && value != getValue() ||
            getValue() != null && getValue() != value
        ) {
            isHandled = false
        }

        super.postValue(value)
    }

    fun forcePostValue(value: T?) {
        isHandled = false
        super.postValue(value)
    }
}