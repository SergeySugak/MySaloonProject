package com.app.mobifix.models.auth

/**
 * Проверяет корректность введенных имени пользователя и пароля.
 * Возвращаемое знчение - код ошибки: 0 - корректно
 * другие значения - коды ошибок, интерпретируемые приложением.
 */
interface AuthValidator {
    fun validateUserName(username: String?): Boolean
    fun validatePassword(password: String?): Boolean
}