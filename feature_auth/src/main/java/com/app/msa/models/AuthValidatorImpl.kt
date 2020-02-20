package com.app.msa.models

import android.util.Patterns
import com.app.mobifix.models.auth.AuthValidator

open class AuthValidatorImpl : AuthValidator {
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
        return password != null && password.trim { it <= ' ' }.length > MIN_PASSWD_LEN
    }

    companion object{
        const val MIN_PASSWD_LEN = 5
    }
}