package com.app.msa.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.mobifix.models.auth.AuthValidator
import com.app.msa.auth.R
import com.app.msa.models.AuthFormState
import com.app.msa.repository.auth.AuthRepository
import com.app.mscorebase.appstate.AppState
import com.app.mscorebase.appstate.StateWriter
import com.app.mscorebase.common.Result
import com.app.mscorebase.ui.MSActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class AuthActivityViewModel
    @Inject constructor(appState: AppState,
                        private val authRepository: AuthRepository,
                        private val authValidator: AuthValidator): MSActivityViewModel(appState) {

    private val _loginForm = MutableLiveData<AuthFormState>()
    val authFormState: LiveData<AuthFormState> = _loginForm

    private val _createAccountResult = MutableLiveData<Result<Boolean>>()
    val createAccountResult: LiveData<Result<Boolean>> = _createAccountResult

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //Пытаемся войти
            val actionResult = authRepository.login(username, password)
            //Если вход выполнен успешно обновляем статус и фиксируем userId
            if (actionResult is Result.Success){
                _loginResult.postValue(actionResult)
            }
            else {
                //Проверяем зарегистрирован ли указанный email
                val accountCheckResult = authRepository.isAccountRegistered(username)
                //Если при проверке не было ошибок
                if (accountCheckResult is Result.Success){
                    val accountExists = accountCheckResult.data
                    if (accountExists){
                        //Если при этом не удалось войти, то что-то не так с e-mail-ом или паролем
                        //значит выдаем сообщение, полученное при попытке войти
                        _loginResult.postValue(actionResult)
                    }
                    else {
                        //Если указанный email НЕ зарегистрирован - регистрируем, отправляем письмо
                        //и передаем обратно результат
                        _createAccountResult.postValue(authRepository.createAccount(username, password))
                    }
                }
                else {
                    //Если при проверке произошли ошибки, то выдаем их обратно
                    _loginResult.postValue(Result.Error((accountCheckResult as Result.Error).exception))
                }
            }
        }
    }

    fun validate(username: String, password: String): Boolean {
        if (!authValidator.validateUserName(username)) {
            _loginForm.value =
                AuthFormState(usernameError = R.string.invalid_username)
        } else if (!authValidator.validatePassword(password)) {
            _loginForm.value =
                AuthFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value =
                AuthFormState(isDataValid = true)
        }
        return authFormState.value?.isDataValid ?: false
    }

    override fun restoreState(writer: StateWriter) {
        //nulls may probably appear as a side effect of some changes to codebase
        val state: Map<String, String?> = writer.readState(this)
        //read state item by item
    }

    override fun saveState(writer: StateWriter) {
        //no meaning to save null
        val state: MutableMap<String, String> = HashMap()
        //write state item by item
        writer.writeState(this, state)
    }
}