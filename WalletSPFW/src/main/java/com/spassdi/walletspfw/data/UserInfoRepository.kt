package com.spassdi.walletspfw.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.spassdi.walletspfw.data.crypto.CryptoManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class UserInfoRepository(context: Context) {
    companion object {
        const val KEY_APP_UUID = "appUuid"
        const val KEY_LDAP_UUID = "ldapUuid"
        const val KEY_NAME = "name"
        const val KEY_NRIC = "nric"
        const val KEY_EMAIL = "email"
        const val KEY_PHONE = "phone"
        const val KEY_ADDRESS_LINE_1 = "addressLine1"
        const val KEY_ADDRESS_LINE_2 = "addressLine2"
        const val KEY_ADDRESS_LINE_3 = "addressLine3"
        const val KEY_ADDRESS_LINE_4 = "addressLine4"
        const val KEY_ADDRESS_LINE_5 = "addressLine5"
    }

    private val storage = StorageEngine.getInstance(context)
    private val cryptoManager = CryptoManager()

    private fun getKey(key: String) = stringPreferencesKey(key)

    suspend fun getInfo(key: String): String? {
        return storage.data.map { preferences ->
            val requestItems = preferences[getKey(key)]?.let { cryptoManager.decrypt(it) }
            requestItems
        }.first()
    }

    suspend fun getAllInfo(): Map<String, String> {
        val infos = mutableMapOf<String, String>()
        return storage.data.map { preferences ->
            preferences.asMap().forEach { (key, value) ->
                val decryptedValue = value.toString().let { cryptoManager.decrypt(it) }
                infos[key.name] = decryptedValue
            }
            infos
        }.first()
    }

    suspend fun saveInfo(key: String, value: String) {
        try {
            storage.edit { preferences ->
                preferences[getKey(key)] = cryptoManager.encrypt(value)
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun batchSaveInfo(data: Map<String, String>) {
        try {
            storage.edit { preferences ->
                data.forEach { (key, value) ->
                    preferences[getKey(key)] = cryptoManager.encrypt(value)
                }
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun clearUserInfo() {
        try {
            storage.edit { preferences ->
                preferences.clear()
            }
        } catch (ex: Exception) {
            throw ex
        }
    }
}

object StorageEngine {
    private const val DATASTORE_NAME = "user_info_storage"

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = DATASTORE_NAME
    )

    @Volatile
    private var INSTANCE: DataStore<Preferences>? = null

    fun getInstance(context: Context): DataStore<Preferences> {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: context.dataStore.also { INSTANCE = it }
        }
    }
}
