package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.WorldCupApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WorldCupViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: WorldCupViewModel = viewModel()
      val context = androidx.compose.ui.platform.LocalContext.current
      androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadOnboardingState(context)
      }
      val selectedSkinTeam by viewModel.selectedSkinTeam.collectAsState()
      val customPrimary = selectedSkinTeam?.let {
        try {
          Color(android.graphics.Color.parseColor(it.primaryColor))
        } catch (e: Exception) {
          null
        }
      }
      val customSecondary = selectedSkinTeam?.let {
        try {
          Color(android.graphics.Color.parseColor(it.secondaryColor))
        } catch (e: Exception) {
          null
        }
      }

      MyApplicationTheme(
        customPrimary = customPrimary,
        customSecondary = customSecondary
      ) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          WorldCupApp(viewModel = viewModel)
        }
      }
    }
  }
}

