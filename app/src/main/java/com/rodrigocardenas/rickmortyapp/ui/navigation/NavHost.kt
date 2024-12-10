package com.rodrigocardenas.rickmortyapp.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rodrigocardenas.rickmortyapp.ui.screens.character.CharacterViewScreen
import com.rodrigocardenas.rickmortyapp.ui.screens.characters.CharactersScreen
import com.rodrigocardenas.rickmortyapp.ui.screens.episodes.EpisodesScreen
import com.rodrigocardenas.rickmortyapp.ui.screens.home.HomeScreen
import com.rodrigocardenas.rickmortyapp.ui.screens.searchcharacter.SearchCharacterScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Home.route,
        )
    {
        composable(NavigationRoutes.Home.route) {
            HomeScreen(navController, modifier)
        }
        composable(NavigationRoutes.Episodes.route) {
            EpisodesScreen(modifier)
        }
        composable(NavigationRoutes.Search.route) {
            SearchCharacterScreen(navController = navController, modifier)
        }
        composable(NavigationRoutes.Characters.route) {
            CharactersScreen(navController = navController, modifier = modifier)
        }
        composable(NavigationRoutes.CharacterDetails.route) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            if (characterId != null) {
                CharacterViewScreen(modifier = modifier, characterId = characterId)
            }
        }
    }
}