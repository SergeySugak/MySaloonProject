package com.app.mscorebase.common

import android.content.Context

interface Initializable {
    @Throws(Exception::class)
    fun init(context: Context)
}