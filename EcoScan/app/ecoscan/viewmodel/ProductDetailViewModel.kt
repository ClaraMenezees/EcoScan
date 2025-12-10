package com.ifpe.ecoscan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpe.ecoscan.screens.ProductRepository   // import corrigido para o repo em data
import com.ifpe.ecoscan.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repository: ProductRepository = ProductRepository // usa o singleton (object)
) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun loadProduct(barcode: String) {
        viewModelScope.launch {
            val result = repository.fetchProductFromApi(barcode)
            _product.value = result
        }
    }
}
