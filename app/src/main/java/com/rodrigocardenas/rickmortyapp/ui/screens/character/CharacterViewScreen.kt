package com.rodrigocardenas.rickmortyapp.ui.screens.character

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImage
import com.rodrigocardenas.rickmortyapp.R
import com.rodrigocardenas.rickmortyapp.core.Resource
import com.rodrigocardenas.rickmortyapp.core.Utils
import com.rodrigocardenas.rickmortyapp.data.models.character.CharacterView
import com.rodrigocardenas.rickmortyapp.data.models.episode.Episode
import com.rodrigocardenas.rickmortyapp.ui.components.loading.LoadingScreenDialog

@Composable
fun CharacterViewScreen(
    modifier: Modifier = Modifier,
    characterId: Int
) {
    val characterViewViewModel: CharacterViewViewModel = hiltViewModel()
    val characterState by characterViewViewModel.viewCharacter.collectAsState()
    val episodeState by characterViewViewModel.viewEpisodes.collectAsState()

    LaunchedEffect(characterId) {
        characterViewViewModel.getAllCharacter(characterId)
    }

    when (characterState) {
        is Resource.Loading -> {
            LoadingScreenDialog()
        }
        is Resource.Success -> {
            val character = (characterState as Resource.Success<CharacterView>).data
            character?.let {
                CharacterViewContent(it, modifier, episodeState)
            } ?: run {
                Utils().ShowInfoDialog(stringResource(id = R.string.search_character_message_not_items)) {}
            }
        }
        is Resource.Error -> {
            Utils().ShowInfoDialog(characterState.message.toString()) {}
        }
        is Resource.Nothing -> {}
    }
}

@Composable
fun CharacterViewContent(characterView: CharacterView, modifier: Modifier, episodeState: Resource<List<Episode>>) {
    val statusAlive = stringResource(id = R.string.character_message_status_alive)
    val statusDead = stringResource(id = R.string.character_message_status_dead)
    val statusUnknown = stringResource(id = R.string.character_message_status_unknow)

    val statusText = when (characterView.status.toString()) {
        "Alive" -> statusAlive
        "Dead" -> statusDead
        "unknown" -> statusUnknown
        else -> characterView.status
    }

    val genderIcon = when (characterView.gender) {
        "Male" -> R.drawable.ic_gender_male
        "Female" -> R.drawable.ic_gender_female
        else -> R.drawable.ic_gender
    }

    val iconTint = when (statusText) {
        stringResource(id = R.string.character_message_status_alive) -> Color.Green
        stringResource(id = R.string.character_message_status_dead) -> Color.Red
        stringResource(id = R.string.character_message_status_unknow) -> Color.Gray
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_circle_background),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = characterView.name.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Text(
                    text = statusText.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 32.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = genderIcon),
                contentDescription = "Gender Icon",
                modifier = Modifier.size(30.dp)
            )
        }
        AsyncImage(
            model = characterView.image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(id = R.string.nav_episodes),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        when (episodeState) {
            is Resource.Loading -> {
                LoadingScreenDialog()
            }
            is Resource.Success -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    val episodes = (episodeState as Resource.Success<List<Episode>>).data
                    episodes?.forEach { episode ->
                        EpisodeItem(
                            episodeNumber = episode.episode.toString(),
                            title = episode.episode.toString(),
                            date = episode.air_date.toString()
                        )
                    }
                }
            }
            is Resource.Error -> {
                Utils().ShowInfoDialog(episodeState.message.toString()) {}
            }
            is Resource.Nothing -> {}
        }
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(label = stringResource(id = R.string.character_message_specie).replace(":", ""), value = characterView.species.toString())
        InfoRow(label = stringResource(id = R.string.character_message_gender).replace(":", ""), value = characterView.gender.toString())
        InfoRow(label = stringResource(id = R.string.character_message_location).replace(":", ""), value = characterView.location?.name.toString())
    }
}


@Composable
fun EpisodeItem(episodeNumber: String, title: String, date: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = episodeNumber,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp)
            )

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(IntrinsicSize.Max)
                    .background(Color.Gray)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
