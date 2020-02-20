package com.app.mscorebase.ui

import androidx.lifecycle.ViewModel

interface ViewModelHolder<VM : ViewModel> {
    fun getViewModel(): VM
    fun clearViewModelInstanceState()
}