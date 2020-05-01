package com.app.view_schedule.api

import kotlin.reflect.KProperty

//T - класс, содержащий элемент типа P
class ObservableProperty<T: Any, P: Any>(
    var propValue: P,
    private val changeSupport: PropertyChangeListener
) {
    operator fun getValue(p: T, prop: KProperty<*>): P = propValue
    operator fun setValue(p: T, prop: KProperty<*>, newValue: P) {
        val oldValue = propValue
        propValue = newValue
        changeSupport.onPropertyChanged(prop.name, oldValue, newValue)
    }

    interface PropertyChangeListener{
        fun onPropertyChanged(prop: String, oldValue: Any?, newValue: Any?)
    }
}