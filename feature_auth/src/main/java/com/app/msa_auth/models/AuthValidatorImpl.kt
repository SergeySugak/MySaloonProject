package com.app.msa_auth.models

import android.util.Patterns
import javax.inject.Inject

open class AuthValidatorImpl @Inject constructor(): AuthValidator {
    override fun validateUserName(username: String?): Boolean {
        if (username == null) {
            return false
        }
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.trim { it <= ' ' }.isNotEmpty()
        }
    }

    override fun validatePassword(password: String?): Boolean {
        return password != null && password.trim { it <= ' ' }.length > MIN_PASSWORD_LEN
    }

    companion object{
        private const val MIN_PASSWORD_LEN = 5
    }
}