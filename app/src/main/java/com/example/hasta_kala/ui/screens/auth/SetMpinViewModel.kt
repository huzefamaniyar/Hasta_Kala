package com.example.hasta_kala.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.datastore.ShopPreferences
import kotlinx.coroutines.launch

class SetMpinViewModel(private val shopPreferences: ShopPreferences) : ViewModel() {
    fun saveMpin(mpin: String) {
        viewModelScope.launch {
            shopPreferences.saveMpin(mpin)
        }
    }
}
