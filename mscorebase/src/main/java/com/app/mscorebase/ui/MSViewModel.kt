package com.app.mscorebase.ui

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateHolder
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import kotlinx.coroutines.Job

abstract class MSViewModel (private val appState: AppStateManager) : ViewModel(), StateHolder {
    internal val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title
    internal val _subtitle = MutableLiveData<String>()
    val subtitle: LiveData<String> = _subtitle

    protected val _isInProgress = StatefulMutableLiveData<Boolean>()
    val isInProgress: StatefulLiveData<Boolean> = _isInProgress
    protected val _noInternetConnectionError = StatefulMutableLiveData<Int>()
    val noInternetConnectionError: StatefulLiveData<Int> = _noInternetConnectionError
    protected val _HTTPError = StatefulMutableLiveData<Throwable>()
    val genericHTTPError: StatefulLiveData<Throwable> = _HTTPError
    protected val _error = StatefulMutableLiveData<Throwable>()
    val error: StatefulLiveData<Throwable> = _error

    protected val mCompositeJob = mutableListOf<Job>()

    fun addJob(job: Job){
        mCompositeJob.add(job)
    }

    fun setInProgress(inProgress: Boolean) {
        _isInProgress.value = inProgress
    }

    protected fun disposeJobs(){
        for (job in mCompositeJob)
            if (!job.isCancelled) {
                job.cancel()
            }
        mCompositeJob.clear()
    }

    protected fun cancelJob(job: Job){
        if (!job.isCancelled) {
            job.cancel()
        }
        mCompositeJob.remove(job)
    }

    fun saveInstanceState() {
        saveState(appState)
    }

    fun clearInstanceState() {
        clearState(appState)
    }

    override fun clearState(writer: StateWriter) {
        writer.clearState(this, true)
    }

    fun onCreate(savedInstanceState: Bundle?) {
        appState.attachStateManager(this)
    }

    fun onDestroy() {}

    fun logout() {
        appState.authManager.logout()
    }
}