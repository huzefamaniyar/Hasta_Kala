package com.example.hasta_kala.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.datastore.ShopPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MpinLoginViewModel(private val shopPreferences: ShopPreferences) : ViewModel() {
    val shopName: Flow<String> = shopPreferences.shopName
    
    private val _isVerified = MutableStateFlow<Boolean?>(null)
    val isVerified: StateFlow<Boolean?> = _isVerified

    fun verifyMpin(mpin: String) {
        viewModelScope.launch {
            val result = shopPreferences.verifyMpin(mpin)
            _isVerified.value = result
            if (!result) {
                // Reset after delay or on next input
            }
        }
    }
    
    fun resetVerification() {
        _isVerified.value = null
    }
}
