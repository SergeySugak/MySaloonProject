package com.app.feature_consumable.ui

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.app.feature_consumable.R
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSFragmentViewModel
import com.app.mscoremodels.saloon.SaloonFactory
import com.app.mscoremodels.saloon.SaloonConsumable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Double.valueOf
import javax.inject.Inject

class ConsumableViewModel
@Inject constructor(
    val appState: AppStateManager,
    private val saloonFactory: SaloonFactory,
    private val dbRepository: DbRepository
) : MSFragmentViewModel(appState) {

    val intConsumableInfoSaveState = StatefulMutableLiveData<Boolean>()
    val consumableInfoSaveState: StatefulLiveData<Boolean> = intConsumableInfoSaveState
    var consumableId: String = ""
    val intConsumableInfo = StatefulMutableLiveData<SaloonConsumable>()
    val consumableInfo: StatefulLiveData<SaloonConsumable> = intConsumableInfo

    override fun restoreState(writer: StateWriter) {

    }

    override fun saveState(writer: StateWriter) {

    }

    fun saveConsumableInfo(
        name: String,
        price: String,
        uom: String
    ) {
        if (!TextUtils.isEmpty(name) &&
            !TextUtils.isEmpty(price) &&
            !TextUtils.isEmpty(uom)) {
            viewModelScope.launch(Dispatchers.IO) {
                intConsumableInfoSaveState.postValue(
                    run {
                        val result = dbRepository.saveConsumableInfo(
                            saloonFactory.createSaloonConsumable(
                                consumableId, name, valueOf(price), uom)
                        )
                        if (result is Result.Success) {
                            result.data
                        } else {
                            intError.postValue((result as Result.Error).exception)
                            false
                        }
                    }
                )
            }
        } else {
            intError.postValue(Exception(appState.context.getString(R.string.err_fill_required_before_save)))
        }
    }

    fun loadData(consumableId: String) {
        this.consumableId = consumableId
        viewModelScope.launch(Dispatchers.IO) {
            val consumableResult = dbRepository.loadConsumableInfo(consumableId)
            if (consumableResult is Result.Success) {
                intConsumableInfo.postValue(consumableResult.data)
            } else {
                intError.postValue((consumableResult as Result.Error).exception)
            }
        }
    }
}
