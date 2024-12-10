@file:Suppress("DEPRECATION")

package com.rodrigocardenas.rickmortyapp.ui.screens.characters

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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.rodrigocardenas.rickmortyapp.data.models.character.Character
import com.rodrigocardenas.rickmortyapp.R
import com.rodrigocardenas.rickmortyapp.core.Utils
import com.rodrigocardenas.rickmortyapp.ui.components.loading.LoadingScreenDialog
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rodrigocardenas.rickmortyapp.ui.navigation.NavigationRoutes

sealed class CharacterViewState {
    object Loading : CharacterViewState()
    object LoadingNextPage : CharacterViewState()
    data class Success(val characters: List<Character>) : CharacterViewState()
    data class Error(val message: String) : CharacterViewState()
}

@Composable
fun CharactersScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val characterViewModel: CharacterViewModel = hiltViewModel()
    val viewState by characterViewModel.viewState.collectAsState()

    var allCharacters by remember { mutableStateOf<List<Character>>(emptyList()) }
    var isLoadingNextPage by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val shouldLoadNextPage: Boolean by remember {
        derivedStateOf {
            !isLoadingNextPage && allCharacters.isNotEmpty() && listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == allCharacters.size - 1
        }
    }

    LaunchedEffect(Unit) {
        if (allCharacters.isEmpty()) {
            characterViewModel.getAllCharacter()
        }
    }

    LaunchedEffect(shouldLoadNextPage) {
        if (shouldLoadNextPage) {
            isLoadingNextPage = true
            characterViewModel.loadNextPage()
        }
    }

    when (val state = viewState) {
        is CharacterViewState.Success -> {
            allCharacters = (allCharacters + state.characters).distinctBy { it.id }
            isLoadingNextPage = false
        }
        is CharacterViewState.Error -> {
            Utils().ShowInfoDialog(state.message) {}
        }
        is CharacterViewState.Loading -> {
                LoadingScreenDialog()
        }
        CharacterViewState.LoadingNextPage -> {
            isLoadingNextPage = true
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(viewState is CharacterViewState.Loading),
        onRefresh = {
            characterViewModel.resetCharacters()
            allCharacters = emptyList()
            characterViewModel.getAllCharacter()
        }
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(allCharacters) { character ->
                CharacterItem(character = character, navController)
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
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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
                    CharacterInfo(
                        title = "",
                        value = statusText.toString(),
                        iconRes = R.drawable.ic_circle_background,
                        isStatusIcon = true
                    )
                    CharacterInfo(
                        title = stringResource(id = R.string.character_message_specie),
                        value = character.species.toString(),
                        iconRes = R.drawable.ic_egg
                    )
                    CharacterInfo(
                        title = stringResource(id = R.string.character_message_gender),
                        value = character.gender.toString(),
                        iconRes = R.drawable.ic_gender
                    )
                }
            }

            IconButton(
                onClick = {
                    navController.navigate(NavigationRoutes.CharacterDetails.route.replace("{id}", character.id.toString()))
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
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
            text = "$title",
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