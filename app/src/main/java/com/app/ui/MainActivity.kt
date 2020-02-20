package com.app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.di.DaggerAppComponent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerAppComponent
            .builder()
            .context(application)
            .build()
            .appNavigator().navigateToAuthActivity(this)
        super.onCreate(savedInstanceState)
        finish()
    }
}
