package com.berna8.tfg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.berna8.tfg.ui.NavegacionApp
import com.berna8.tfg.ui.theme.TFGTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TFGTheme {
                NavegacionApp()
            }
        }
    }
}