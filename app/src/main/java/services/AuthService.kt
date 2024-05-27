package services

import android.content.Context
import android.content.SharedPreferences
import database.DatabaseHelper
import model.User

class AuthService(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val SHARED_PREFS_NAME = "auth_prefs"
        private const val PREF_USER_ID = "user_id"
    }


    fun registerUser(username: String, password: String): Boolean {
        if (dbHelper.checkUser(username)) return false
        dbHelper.addUser(username, password)
        return true
    }

    fun loginUser(username: String, password: String): Boolean {
        if (dbHelper.checkUserCredentials(username, password)) {
            val user = dbHelper.getUser(username)
            user?.let {
                saveUserId(it.id)
                return true
            }
        }
        return false
    }

    private fun saveUserId(userId: Int) {
        sharedPreferences.edit().putInt(PREF_USER_ID, userId).apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(PREF_USER_ID, -1)
    }

    fun logoutUser() {
        sharedPreferences.edit().remove(PREF_USER_ID).apply()
    }

    fun getUserInfo(): User? {
        return dbHelper.getUserById(getUserId())
    }

    fun isLoggedIn(): Boolean {
        val userId = getUserId()
        return userId != -1
    }

    fun deleteAccount(): Boolean {
        val userId = getUserId()
        val success = dbHelper.deleteUser(userId)

        if (success) {
            logoutUser()
        }

        return success
    }
}