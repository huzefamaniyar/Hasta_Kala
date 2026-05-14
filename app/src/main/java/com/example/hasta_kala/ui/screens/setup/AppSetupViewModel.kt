package com.example.hasta_kala.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.datastore.ShopPreferences
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

class AppSetupViewModel(private val shopPreferences: ShopPreferences) : ViewModel() {
    private val _generatedOtp = MutableStateFlow("")
    val generatedOtp: StateFlow<String> = _generatedOtp

    fun saveShopDetails(shopName: String, ownerName: String, mobile: String) {
        viewModelScope.launch {
            shopPreferences.saveShopDetails(shopName, ownerName, mobile)
            generateAndSendOtp(mobile)
        }
    }

    fun generateAndSendOtp(mobile: String) {
        val otp = (100000..999999).random().toString()
        _generatedOtp.value = otp
        // In a real app, this would call an SMS API.
        println("SIMULATED OTP for $mobile: $otp")
    }
}
