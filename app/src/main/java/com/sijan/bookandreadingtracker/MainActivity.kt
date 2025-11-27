package com.sijan.bookandreadingtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sijan.bookandreadingtracker.presentation.navigation.BookTrackerApp
import com.sijan.bookandreadingtracker.ui.theme.BookAndReadingTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookAndReadingTrackerTheme {
                BookTrackerApp()
            }
        }
    }
}

