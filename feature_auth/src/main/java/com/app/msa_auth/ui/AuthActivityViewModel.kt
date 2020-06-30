package com.app.msa_auth.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.msa.auth.R
import com.app.msa_auth.models.AuthFormState
import com.app.msa_auth.models.AuthValidator
import com.app.msa_auth_repo.repository.auth.AuthRepository
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscorebase.appstate.AppStateManager
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.livedata.StatefulLiveData
import com.app.mscorebase.livedata.StatefulMutableLiveData
import com.app.mscorebase.ui.MSActivityViewModel
import com.app.mscorebase.utils.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class AuthActivityViewModel
@Inject constructor(
    private val appState: AppStateManager,
    private val authRepository: AuthRepository,
    private val authValidator: AuthValidator,
    private val dbRepository: DbRepository
) : MSActivityViewModel(appState) {

    private val intAuthFormState = MutableLiveData<AuthFormState>()
    val authFormState: LiveData<AuthFormState> = intAuthFormState
    private val intCreateAccountResult = MutableLiveData<Result<Boolean>>()
    val createAccountResult: LiveData<Result<Boolean>> = intCreateAccountResult
    private val intLoginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = intLoginResult
    private val intRestoredUserName = StatefulMutableLiveData<String>()
    val restoredUserName: StatefulLiveData<String> = intRestoredUserName

    private fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //Пытаемся войти
            val loginActionResult = authRepository.login(username, password)
            //Если вход выполнен успешно обновляем статус и фиксируем userId
            if (loginActionResult is Result.Success) {
                authRepository.storeUserName(username)
                appState.clear()
                appState.attachStateHolder(this@AuthActivityViewModel)
                //Проверяем, что для этого администратора создана ветка в базе
                val checkResult = dbRepository.checkSaloonRoot(loginActionResult.data)
                if (checkResult is Result.Success) {
                    //Если нет - создаем
                    if (!checkResult.data) {
                        val createUserRootResult =
                            dbRepository.createSaloonRoot(loginActionResult.data, username)
                        //Если при создании ошибка, то работать нельзя
                        if (createUserRootResult is Result.Error) {
                            intLoginResult.postValue(Result.Error(createUserRootResult.exception))
                            return@launch
                        }
                    }
                    dbRepository.initialize(loginActionResult.data)
                } else {
                    //Если при проверке ошибка, то работать нельзя
                    intLoginResult.postValue(Result.Error((checkResult as Result.Error).exception))
                    return@launch
                }
                //Если все нормально
                intLoginResult.postValue(loginActionResult)
                appState.authManager.login(username, loginActionResult.data)
            } else {
                //Проверяем зарегистрирован ли указанный email
                val accountCheckResult = authRepository.isAccountRegistered(username)
                //Если при проверке не было ошибок
                if (accountCheckResult is Result.Success) {
                    val accountExists = accountCheckResult.data
                    if (accountExists) {
                        //Если при этом не удалось войти, то что-то не так с e-mail-ом или паролем
                        //значит выдаем сообщение, полученное при попытке войти
                        intLoginResult.postValue(loginActionResult)
                    } else {
                        //Если указанный email НЕ зарегистрирован - регистрируем, отправляем письмо
                        //и передаем обратно результат
                        intCreateAccountResult.postValue(
                            authRepository.createAccount(
                                username,
                                password
                            )
                        )
                    }
                } else {
                    //Если при проверке произошли ошибки, то выдаем их обратно
                    intLoginResult.postValue(Result.Error((accountCheckResult as Result.Error).exception))
                }
            }
        }
    }

    fun validate(username: String, password: String): Boolean {
        if (!authValidator.validateUserName(username)) {
            intAuthFormState.value =
                AuthFormState(usernameError = R.string.invalid_username)
        } else if (!authValidator.validatePassword(password)) {
            intAuthFormState.value =
                AuthFormState(passwordError = R.string.invalid_password)
        } else {
            intAuthFormState.value =
                AuthFormState(isDataValid = true)
        }
        return authFormState.value?.isDataValid ?: false
    }

    override fun restoreState(writer: StateWriter) {
        //nulls may probably appear as a side effect of some changes to codebase
        val state: Map<String, String?> = writer.readState(this) ?: return
        //read state item by item
    }

    override fun saveState(writer: StateWriter) {
        //no meaning to save null
        val state: MutableMap<String, String> = HashMap()
        //write state item by item
        writer.writeState(this, state)
    }

    fun tryLogin(context: Context, username: String, password: String) {
        if (validate(username, password)) {
            if (isOnline(context)) {
                intIsInProgress.value = true
                login(username, password)
            } else {
                intNoInternetConnectionError.value = R.string.err_no_internet
            }
        }
    }

    init {
        intRestoredUserName.value = authRepository.reStoreUserName()
    }
}