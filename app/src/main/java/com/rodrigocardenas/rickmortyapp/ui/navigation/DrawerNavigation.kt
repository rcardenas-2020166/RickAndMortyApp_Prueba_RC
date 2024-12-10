package com.rodrigocardenas.rickmortyapp.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rodrigocardenas.rickmortyapp.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DrawerNavigation() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed )
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    val currentScreenTitle = when (currentRoute) {
        NavigationRoutes.Home.route -> stringResource(id = R.string.nav_home)
        NavigationRoutes.Characters.route -> stringResource(id = R.string.nav_characters)
        NavigationRoutes.Episodes.route -> stringResource(id = R.string.nav_episodes)
        NavigationRoutes.Search.route -> stringResource(id = R.string.nav_search)
        NavigationRoutes.CharacterDetails.route -> stringResource(id = R.string.nav_details)
        else -> stringResource(id = R.string.app_title)
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(scope, drawerState, navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(scope, drawerState, currentScreenTitle, navController)
            }
        ) {
                paddingValues ->
            AppNavigation(navController = navController, modifier = Modifier.padding(paddingValues))
        }
    }
}
