package com.sylva.sinema.utils

import android.content.Context
import android.content.SharedPreferences
import com.sylva.sinema.model.User

object Preferences {

    fun init(context: Context, name: String): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    private fun editor(context: Context, name: String): SharedPreferences.Editor {
        val sharedPref = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sharedPref.edit()
    }

    fun saveEmail(email: String, context: Context){
        val editor = editor(context, "onSignIn")
        editor.putString("email", email)
        editor.apply()
    }

    fun checkEmail(context: Context): Boolean{
        val sharedPref = init(context, "onSignIn")
        val username = sharedPref.getString("email", null)
        return username != null
    }

    fun getEmail(context: Context): String? {
        val sharedPref = init(context, "onSignIn")
        return sharedPref.getString("email", null)
    }

    fun saveUserInfo(user: User, context: Context) {
        val editor = editor(context, "onSignIn")
        editor.putString("name", user.name)
        editor.putString("phoneNumber", user.phoneNumber)
        editor.putString("email", user.email)
        editor.putString("password", user.password)
        editor.apply()
    }

    fun getUserInfo(context: Context): User? {
        val sharedPref = init(context, "onSignIn")
        return User(
            name = sharedPref.getString("name", null) ?: "",
            email = sharedPref.getString("email", null) ?: "",
            password = sharedPref.getString("password", null) ?: "",
            phoneNumber = sharedPref.getString("phoneNumber", null) ?: "",
        )
    }

    fun logout(context: Context){
        val editor = editor(context, "onSignIn")
        editor.remove("name")
        editor.remove("status")
        editor.remove("email")
        editor.remove("password")
        editor.remove("phoneNumber")
        // Hapus properti lainnya sesuai kebutuhan
        editor.apply()
    }
}