package com.estrano.core.session

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        "estrano_secure_session",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_SIGNED_IN = "signed_in"
        private const val KEY_NAME     = "name"
        private const val KEY_EMAIL    = "email"
    }

    fun saveUser(name: String?, email: String?) {
        preferences.edit().apply {
            putBoolean(KEY_SIGNED_IN, true)
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            apply()
        }
    }

    fun isSignedIn(): Boolean = preferences.getBoolean(KEY_SIGNED_IN, false)

    fun getUser(): UserProfile? {
        if (!isSignedIn()) return null
        return UserProfile(
            displayName = preferences.getString(KEY_NAME, null),
            email       = preferences.getString(KEY_EMAIL, null)
        )
    }

    fun getName(): String  = preferences.getString(KEY_NAME,  "User")              ?: "User"
    fun getEmail(): String = preferences.getString(KEY_EMAIL, "") ?: ""

    fun signOut() { preferences.edit().clear().apply() }
    fun clear() = signOut()
}

data class UserProfile(val displayName: String?, val email: String?)
