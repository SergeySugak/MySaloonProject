package com.app.mscoremodels.saloon

import android.content.Context
import android.os.Build
import com.app.mscorebase.ui.Colorizer
import com.app.mscoremodels.R
import javax.inject.Inject
import kotlin.random.Random

class EventColorizer @Inject constructor(): Colorizer {
    override fun getRandomColor(context: Context): Int {
        @Suppress("DEPRECATION")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.resources.getColor(DEF_FILL_COLORS[Random.nextInt(16)], null)
        } else {
            context.resources.getColor(DEF_FILL_COLORS[Random.nextInt(16)])
        }
    }

    companion object {
        val DEF_FILL_COLORS = arrayOf(
            R.color.colorFill1, R.color.colorFill2, R.color.colorFill3, R.color.colorFill4,
            R.color.colorFill5, R.color.colorFill6, R.color.colorFill7, R.color.colorFill8,
            R.color.colorFill9, R.color.colorFill10, R.color.colorFill11, R.color.colorFill12,
            R.color.colorFill13, R.color.colorFill14, R.color.colorFill15, R.color.colorFill16)
    }
}