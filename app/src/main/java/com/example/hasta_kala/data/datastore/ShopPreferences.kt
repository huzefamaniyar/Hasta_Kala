package com.example.hasta_kala.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "shop_preferences")

object ShopPreferencesKeys {
    val SHOP_NAME = stringPreferencesKey("shop_name")
    val OWNER_NAME = stringPreferencesKey("owner_name")
    val MOBILE_NUMBER = stringPreferencesKey("mobile_number")
    val MPIN_HASH = stringPreferencesKey("mpin_hash")
    val HAS_MPIN_SET = booleanPreferencesKey("has_mpin_set")
    val DAILY_TARGET = doublePreferencesKey("daily_target")
    val WEEKLY_TARGET = doublePreferencesKey("weekly_target")
    val MONTHLY_TARGET = doublePreferencesKey("monthly_target")
    val YEARLY_TARGET = doublePreferencesKey("yearly_target")
    val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
}

class ShopPreferences(private val context: Context) {
    val shopName: Flow<String> = context.dataStore.data.map { it[ShopPreferencesKeys.SHOP_NAME] ?: "Hasta-Kala" }
    val ownerName: Flow<String> = context.dataStore.data.map { it[ShopPreferencesKeys.OWNER_NAME] ?: "" }
    val mobileNumber: Flow<String> = context.dataStore.data.map { it[ShopPreferencesKeys.MOBILE_NUMBER] ?: "" }
    val profileImageUri: Flow<String?> = context.dataStore.data.map { it[ShopPreferencesKeys.PROFILE_IMAGE_URI] }
    val hasMpinSet: Flow<Boolean> = context.dataStore.data.map { it[ShopPreferencesKeys.HAS_MPIN_SET] ?: false }

    val dailyTarget: Flow<Double> = context.dataStore.data.map { it[ShopPreferencesKeys.DAILY_TARGET] ?: 5000.0 }
    val weeklyTarget: Flow<Double> = context.dataStore.data.map { it[ShopPreferencesKeys.WEEKLY_TARGET] ?: 30000.0 }
    val monthlyTarget: Flow<Double> = context.dataStore.data.map { it[ShopPreferencesKeys.MONTHLY_TARGET] ?: 120000.0 }
    val yearlyTarget: Flow<Double> = context.dataStore.data.map { it[ShopPreferencesKeys.YEARLY_TARGET] ?: 1500000.0 }

    suspend fun updateProfileImage(uri: String?) {
        context.dataStore.edit {
            if (uri == null) it.remove(ShopPreferencesKeys.PROFILE_IMAGE_URI)
            else it[ShopPreferencesKeys.PROFILE_IMAGE_URI] = uri
        }
    }

    suspend fun updateTarget(range: String, amount: Double) {
        context.dataStore.edit {
            when (range) {
                "Daily" -> it[ShopPreferencesKeys.DAILY_TARGET] = amount
                "Weekly" -> it[ShopPreferencesKeys.WEEKLY_TARGET] = amount
                "Monthly" -> it[ShopPreferencesKeys.MONTHLY_TARGET] = amount
                "Yearly" -> it[ShopPreferencesKeys.YEARLY_TARGET] = amount
            }
        }
    }

    suspend fun saveMpin(rawMpin: String) {
        val hash = MessageDigest.getInstance("SHA-256")
            .digest(rawMpin.toByteArray()).joinToString("") { "%02x".format(it) }
        context.dataStore.edit {
            it[ShopPreferencesKeys.MPIN_HASH] = hash
            it[ShopPreferencesKeys.HAS_MPIN_SET] = true
        }
    }

    suspend fun verifyMpin(rawMpin: String): Boolean {
        val hash = MessageDigest.getInstance("SHA-256")
            .digest(rawMpin.toByteArray()).joinToString("") { "%02x".format(it) }
        return context.dataStore.data.map { it[ShopPreferencesKeys.MPIN_HASH] == hash }.first()
    }

    suspend fun saveShopDetails(shopName: String, ownerName: String, mobile: String) {
        context.dataStore.edit {
            it[ShopPreferencesKeys.SHOP_NAME] = shopName
            it[ShopPreferencesKeys.OWNER_NAME] = ownerName
            it[ShopPreferencesKeys.MOBILE_NUMBER] = mobile
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
