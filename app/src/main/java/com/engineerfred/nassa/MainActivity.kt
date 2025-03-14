package com.engineerfred.nassa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.engineerfred.nassa.ui.theme.NassaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefsManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val faqList = loadFAQData(applicationContext)

        setContent {

            val diagnosticViewModel: DiagnosticViewModel = hiltViewModel()
            val selectedTheme = diagnosticViewModel.uiState.collectAsState().value.selectedTheme

            val darkTheme = when (selectedTheme) {
                ThemeOption.System -> isSystemInDarkTheme()
                ThemeOption.Dark -> true
                ThemeOption.Light -> false
            }

            NassaTheme( darkTheme = darkTheme ) {
                AppGraph(
                    viewModel = diagnosticViewModel,
                    onSaveFirstLaunch = {
                        prefsManager.setFirstLaunchFalse()
                    },
                    isFirstLaunch = prefsManager.isFirstLaunch(),
                    faqList = faqList
                )
            }
        }
    }
}