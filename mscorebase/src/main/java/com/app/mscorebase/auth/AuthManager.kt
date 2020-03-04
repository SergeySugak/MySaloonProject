package main.java.com.app.mscorebase.auth

interface AuthManager {
    enum class State {
        loggedIn, loggedOut
    }
    fun getState(): State
    fun getUsername(): String
    fun logIn(username: String)
    fun logOut()
}