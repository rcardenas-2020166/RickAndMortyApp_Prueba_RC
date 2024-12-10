package com.rodrigocardenas.rickmortyapp.ui.navigation

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.rodrigocardenas.rickmortyapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(scope: CoroutineScope, drawerState: DrawerState, currentScreenTitle: String, navController: NavHostController) {
    val canNavigateBack = navController.previousBackStackEntry != null
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    val allowedBackRoutes = listOf(
        NavigationRoutes.CharacterDetails.route
    )
    val shouldShowBackButton = currentRoute in allowedBackRoutes

    TopAppBar(
        title = { Text(currentScreenTitle) },
        navigationIcon = {
            if (shouldShowBackButton) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            } else {
                IconButton(onClick = {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Open Close Drawer Icon")
                }
            }
        }
    )
}

