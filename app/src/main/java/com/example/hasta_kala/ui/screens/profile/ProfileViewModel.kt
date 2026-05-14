package com.example.hasta_kala.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.datastore.ShopPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(private val shopPreferences: ShopPreferences) : ViewModel() {

    val shopName = shopPreferences.shopName
    val ownerName = shopPreferences.ownerName
    val mobileNumber = shopPreferences.mobileNumber
    val profileImageUri = shopPreferences.profileImageUri

    fun updateShopDetails(name: String, owner: String, mobile: String) {
        viewModelScope.launch {
            shopPreferences.saveShopDetails(name, owner, mobile)
        }
    }

    fun updateProfileImage(uri: String?) {
        viewModelScope.launch {
            shopPreferences.updateProfileImage(uri)
        }
    }

    fun logout(onComplete: () -> Unit) {
        // Logic for logout if needed
        onComplete()
    }
}
