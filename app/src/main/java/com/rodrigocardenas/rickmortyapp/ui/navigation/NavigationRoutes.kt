package com.rodrigocardenas.rickmortyapp.ui.navigation

sealed class NavigationRoutes(
    val route : String
)
{
    object Home : NavigationRoutes("home")
    object Characters : NavigationRoutes("characters")
    object Search : NavigationRoutes("search")
    object Episodes : NavigationRoutes("episodes")
    object CharacterDetails : NavigationRoutes("characterDetails/{id}")
}