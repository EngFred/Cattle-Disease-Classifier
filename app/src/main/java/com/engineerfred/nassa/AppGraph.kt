package com.engineerfred.nassa

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppGraph(
    viewModel: DiagnosticViewModel,
    onSaveFirstLaunch: () -> Unit,
    isFirstLaunch: Boolean,
    navController: NavHostController = rememberNavController(),
    darkTheme: Boolean
) {

    val startDestination = if ( isFirstLaunch ) "welcome" else "diagonalise"

    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = startDestination
    ){

        composable(
            route = "welcome",
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(500)
                )
            },
        ) {
            WelcomeScreen(
                onNext = {
                    onSaveFirstLaunch()
                    navController.navigate("diagonalise") {
                        launchSingleTop = true
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                )
            },

            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(500)
                )
            },
            route = "qa",
        ){
            QAScreen( onBack = { navController.navigateUp() }, darkTheme = darkTheme)
        }
        composable("diagonalise"){
            DiagnosticScreen(
                onAskClicked = {
                    navController.navigate("qa") {
                        launchSingleTop = true
                    }
                },
                viewModel = viewModel,
                darkTheme = darkTheme
            )
        }
    }
}