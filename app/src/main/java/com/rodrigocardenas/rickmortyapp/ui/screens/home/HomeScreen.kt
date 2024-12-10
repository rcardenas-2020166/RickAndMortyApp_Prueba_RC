package com.rodrigocardenas.rickmortyapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rodrigocardenas.rickmortyapp.R
import com.rodrigocardenas.rickmortyapp.ui.navigation.NavigationRoutes

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        HomeHeader()
        HomedGrid(navController = navController)
    }
}

@Composable
fun HomeHeader() {
    Spacer(modifier = Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.landing_image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun HomedGrid(navController: NavHostController) {
    val items = listOf(
        Triple(stringResource(id = R.string.nav_characters), NavigationRoutes.Characters.route, "👤"),
        Triple(stringResource(id = R.string.nav_episodes), NavigationRoutes.Episodes.route, "🎥"),
        Triple(stringResource(id = R.string.nav_search), NavigationRoutes.Search.route, "🔍"),
        )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items.chunked(2).forEach { rowItems -> // Organizamos en dos tarjetas por fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowItems.forEach { (label, route, icon) -> // Desestructuramos en tres elementos
                    DashboardCard(
                        label = label,
                        icon = icon, // Pasamos el ícono correspondiente
                        route = route, // Pasamos la ruta
                        navController = navController // Pasamos el controlador de navegación
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardCard(label: String, icon: String, route: String, navController: NavHostController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .size(180.dp)
            .padding(20.dp)
            .clickable {
                navController.navigate(route)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = icon,
                fontSize = 35.sp
            )
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
        }
    }
}