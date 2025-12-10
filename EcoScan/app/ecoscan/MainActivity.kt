package com.ifpe.ecoscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ifpe.ecoscan.screens.EcoScanApp
import com.ifpe.ecoscan.ui.theme.EcoScanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcoScanTheme {
                EcoScanApp()
            }
        }
    }
}
