@file:Suppress("DEPRECATION")

package com.rodrigocardenas.rickmortyapp.ui.screens.searchcharacter

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rodrigocardenas.rickmortyapp.R
import com.rodrigocardenas.rickmortyapp.core.Utils
import com.rodrigocardenas.rickmortyapp.data.models.character.Character
import com.rodrigocardenas.rickmortyapp.ui.components.loading.LoadingScreenDialog
import com.rodrigocardenas.rickmortyapp.ui.navigation.NavigationRoutes

sealed class SearchCharacterViewState {
    object Idle : SearchCharacterViewState()
    object Loading : SearchCharacterViewState()
    object LoadingNextPage : SearchCharacterViewState()
    object NotItems : SearchCharacterViewState()
    data class Success(val characters: List<Character>) : SearchCharacterViewState()
    data class Error(val message: String) : SearchCharacterViewState()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCharacterScreen(navController: NavHostController,  modifier: Modifier = Modifier) {
    val searchViewModel: SearchCharacterViewModel = hiltViewModel()
    val viewState by searchViewModel.viewState.collectAsState()

    var query by remember { mutableStateOf("") }
    var allCharacters by remember { mutableStateOf<List<Character>>(emptyList()) }
    var isLoadingNextPage by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val shouldLoadNextPage: Boolean by remember {
        derivedStateOf {
            !isLoadingNextPage && allCharacters.isNotEmpty() &&
                    listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == allCharacters.size - 1
        }
    }

    LaunchedEffect(shouldLoadNextPage) {
        if (shouldLoadNextPage) {
            isLoadingNextPage = true
            searchViewModel.loadNextPage()
        }
    }

    when (val state = viewState) {
        is SearchCharacterViewState.Idle -> {
            allCharacters = emptyList()
        }
        is SearchCharacterViewState.Success -> {
            allCharacters = (allCharacters + state.characters).distinctBy { it.id }
            isLoadingNextPage = false
        }
        is SearchCharacterViewState.Error -> {
            Utils().ShowInfoDialog(state.message) {}
        }
        is SearchCharacterViewState.Loading -> {
            LoadingScreenDialog()
        }
        SearchCharacterViewState.LoadingNextPage -> {
            isLoadingNextPage = true
        }

        SearchCharacterViewState.NotItems -> {
            isLoadingNextPage = false
            NoItemsScreen(message = stringResource(id = R.string.search_character_message_not_items))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(stringResource(id = R.string.search_character_message_search_bar )) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search_character_message_button),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            androidx.compose.material3.Button(
                onClick = {
                    allCharacters = emptyList()
                    searchViewModel.resetCharacters()
                    searchViewModel.searchCharacterByName(query)
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier.height(56.dp)
            ) {
                Text(text = stringResource(id = R.string.search_character_message_button))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SwipeRefresh(
            state = rememberSwipeRefreshState(viewState is SearchCharacterViewState.Loading),
            onRefresh = {
                allCharacters = emptyList()
                searchViewModel.resetCharacters()
                searchViewModel.searchCharacterByName(query)
            }
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(allCharacters) { character ->
                    CharacterItem(character = character, navController = navController)
                }
            }
        }
    }
}

@Composable
fun CharacterItem(character: Character, navController: NavHostController) {
    val statusAlive = stringResource(id = R.string.character_message_status_alive)
    val statusDead = stringResource(id = R.string.character_message_status_dead)
    val statusUnknown = stringResource(id = R.string.character_message_status_unknow)

    val statusText = when (character.status.toString()) {
        "Alive" -> statusAlive
        "Dead" -> statusDead
        "unknown" -> statusUnknown
        else -> character.status
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = character.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    CharacterInfo(title = "", value = statusText.toString(), iconRes = R.drawable.ic_circle_background, isStatusIcon = true)
                    CharacterInfo(title = stringResource(id = R.string.character_message_specie), value = character.species.toString(), iconRes = R.drawable.ic_egg)
                    CharacterInfo(title = stringResource(id = R.string.character_message_gender), value = character.gender.toString(), iconRes = R.drawable.ic_gender)
                }
            }

            IconButton(
                onClick = {
                    navController.navigate(NavigationRoutes.CharacterDetails.route.replace("{id}", character.id.toString()))
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eye),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}


@Composable
fun CharacterInfo(
    title: String,
    value: String,
    @DrawableRes iconRes: Int,
    isStatusIcon: Boolean = false
) {
    val iconTintColor = if (isStatusIcon) {
        when (value) {
            stringResource(id = R.string.character_message_status_alive) -> Color.Green
            stringResource(id = R.string.character_message_status_dead) -> Color.Red
            stringResource(id = R.string.character_message_status_unknow) -> Color.Gray
            else -> MaterialTheme.colorScheme.onSurface
        }
    } else {
        MaterialTheme.colorScheme.primary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = iconTintColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun NoItemsScreen(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_menu_character),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp),
            tint = Color.LightGray
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.LightGray,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
