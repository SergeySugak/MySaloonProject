package main.java.com.app.mscorebase.auth

interface AuthManager {
    enum class State {
        loggedIn, loggedOut
    }

    fun getState(): State
    fun getUserName(): String
    fun getUserId(): String
    fun login(username: String, userId: String)
    fun logout()
}