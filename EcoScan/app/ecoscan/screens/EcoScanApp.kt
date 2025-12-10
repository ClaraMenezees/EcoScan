package com.ifpe.ecoscan.screens

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.ifpe.ecoscan.model.Product
import com.ifpe.ecoscan.screens.ProductDetailScreen
import com.ifpe.ecoscan.viewmodel.*

import java.lang.reflect.Field
import java.lang.reflect.Method

@Composable
fun EcoScanApp() {
    val navController = rememberNavController()

    val authVm: AuthViewModel = viewModel()
    val scanVm: ScanViewModel = viewModel()
    val homeVm: HomeViewModel = viewModel()
    val historyVm: HistoryViewModel = viewModel()

    val authState by authVm.state.collectAsState()
    val scanState by scanVm.uiState.collectAsState()

    val context = LocalContext.current

    // efeitos do scanner
    LaunchedEffect(scanVm) {
        scanVm.effect.collect { effect ->
            when (effect) {
                is ScanEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()

                is ScanEffect.NavigateToDetails -> {
                    val code = extractBarcodeFromEffect(effect) ?: effect.barcode
                    if (!code.isNullOrBlank()) {
                        navController.navigate("details/$code")
                    } else {
                        Toast.makeText(context, "Código do produto não encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // navegação após login
    LaunchedEffect(authState) {
        if (authState is AuthUiState.LoggedIn) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authState is AuthUiState.LoggedIn) "home" else "login"
    ) {

        composable("login") {
            LoginScreen(
                onLoginClick = { email, pass -> authVm.login(email, pass) },
                isLoading = authState is AuthUiState.Loading,
                errorMessage = (authState as? AuthUiState.Error)?.message
            )
        }

        composable("home") {
            HomeScreen(
                viewModel = homeVm,
                onScanClick = { navController.navigate("scanner") },
                onHistoryClick = { navController.navigate("history") },
                onProfileClick = { navController.navigate("profile") },
                onLogout = {
                    authVm.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onProductClick = { product: Product ->
                    product.barcode?.let { navController.navigate("details/$it") }
                }
            )
        }

        composable("scanner") {
            ScannerScreen(
                onBarcodeDetected = { code -> scanVm.fetchProduct(code) },
                onBack = { navController.popBackStack() },
                isLoading = scanState is ScanUiState.Loading
            )
        }

        composable("details/{barcode}") { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            ProductDetailScreen(
                barcode = barcode,
                historyVm = historyVm
            )
        }

        composable("history") {
            HistoryScreen(
                viewModel = historyVm,
                onBack = { navController.popBackStack() },
                onProductClick = { product: Product ->
                    product.barcode?.let { navController.navigate("details/$it") }
                }
            )
        }

        composable("profile") {
            ProfileScreen(viewModel = viewModel())
        }
    }
}

// mesmo helper que você já tinha
private fun extractBarcodeFromEffect(effect: Any): String? {
    val candidateGetters = listOf("getBarcode", "getCode", "getValue", "barcode", "code", "value")
    for (name in candidateGetters) {
        try {
            val m: Method? = try {
                effect::class.java.getMethod(name)
            } catch (_: NoSuchMethodException) {
                null
            }
            if (m != null) {
                val res = m.invoke(effect)
                if (res is String && res.isNotBlank()) return res
            }
        } catch (_: Exception) { }
    }

    val candidateFields = listOf("barcode", "code", "value")
    for (fName in candidateFields) {
        try {
            val field: Field? = try {
                effect::class.java.getDeclaredField(fName)
            } catch (_: NoSuchFieldException) {
                null
            }
            if (field != null) {
                field.isAccessible = true
                val res = field.get(effect)
                if (res is String && res.isNotBlank()) return res
            }
        } catch (_: Exception) { }
    }

    return null
}
