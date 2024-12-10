package com.rodrigocardenas.rickmortyapp.ui.navigation
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rodrigocardenas.rickmortyapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavHostController
) {
    val navItems = listOf(
        Triple(stringResource(id = R.string.nav_home), R.drawable.ic_menu_home, NavigationRoutes.Home.route),
        Triple(stringResource(id = R.string.nav_characters), R.drawable.ic_menu_character, NavigationRoutes.Characters.route),
        Triple(stringResource(id = R.string.nav_episodes), R.drawable.ic_menu_episode, NavigationRoutes.Episodes.route),
        Triple(stringResource(id = R.string.nav_search), R.drawable.ic_search, NavigationRoutes.Search.route)
    )
    val (selectedItem, setSelectedItem) = remember { mutableStateOf(navItems[0].first) }

    ModalDrawerSheet {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.portada),
                contentDescription = "Portada Application",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            navItems.forEach { (label, icon, route) ->
                NavigationDrawerItem(
                    label = { Text(label) },
                    icon = {
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = label,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(24.dp),
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint)
                        )
                    },
                    selected = label == selectedItem,
                    onClick = {
                        setSelectedItem(label)
                        scope.launch {
                            drawerState.snapTo(DrawerValue.Closed)
                        }
                        navController.navigate(route){
                            popUpTo(route)
                            { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DrawerContentPreview() {
    val navController = rememberNavController()
    DrawerContent(
        scope = rememberCoroutineScope(),
        drawerState = rememberDrawerState(DrawerValue.Closed),
        navController = navController
    )
}
