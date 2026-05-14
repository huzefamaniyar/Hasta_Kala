package com.example.hasta_kala.ui.screens.bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.db.entities.SaleWithItems
import com.example.hasta_kala.data.repository.SaleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BillViewModel(
    private val saleId: Int,
    private val saleRepository: SaleRepository
) : ViewModel() {

    val saleWithItems: StateFlow<SaleWithItems?> = saleRepository.getSaleWithItems(saleId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
