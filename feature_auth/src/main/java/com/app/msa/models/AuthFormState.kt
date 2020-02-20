package com.app.msa.models

/**
 * Data validation state of the login form.
 */
data class AuthFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
