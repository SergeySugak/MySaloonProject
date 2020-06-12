package com.app.mscoremodels.saloon

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
enum class SaloonEventState constructor(val value: Int) {
    esScheduled(0), esDone(1), esMissed(2), esCancelled(3), esError(4);

    companion object {
        fun fromInt(value: Int): SaloonEventState {
            return when (value) {
                1 -> esDone
                2 -> esMissed
                3 -> esCancelled
                4 -> esError
                else -> esScheduled
            }
        }
    }
}