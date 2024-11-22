package com.example.wwbinspectionapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.wwbinspectionapp.utils.Constants.PREFS_TOKEN_FILE
import com.example.wwbinspectionapp.utils.Constants.USER_DEPARTMENT
import com.example.wwbinspectionapp.utils.Constants.USER_TOKEN
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private var prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
    // Save department type
    fun saveDepartmentType(departmentType: String) {
        val editor = prefs.edit()
        editor.putString(USER_DEPARTMENT, departmentType)
        editor.apply()
    }
    fun getDepartmentType(): String? {
        return prefs.getString(USER_DEPARTMENT, null)
    }

    // Method to clear the saved token and department type
    fun clearToken() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.remove(USER_DEPARTMENT)
        editor.apply()
    }
}