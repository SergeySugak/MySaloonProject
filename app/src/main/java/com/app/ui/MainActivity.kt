package com.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
