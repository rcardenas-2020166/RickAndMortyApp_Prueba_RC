package com.rodrigocardenas.rickmortyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rodrigocardenas.rickmortyapp.ui.navigation.DrawerNavigation
import com.rodrigocardenas.rickmortyapp.ui.theme.RickMortyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickMortyAppTheme {
                DrawerNavigation()
            }
        }
    }
}
