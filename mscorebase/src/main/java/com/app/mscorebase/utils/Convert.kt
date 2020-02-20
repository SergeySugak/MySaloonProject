package com.app.mscorebase.utils

import java.io.PrintWriter
import java.io.StringWriter

fun getStackTraceAsString(t: Throwable?): String? {
    return if (t != null) {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        t.printStackTrace(pw)
        sw.toString()
    } else {
        ""
    }
}
