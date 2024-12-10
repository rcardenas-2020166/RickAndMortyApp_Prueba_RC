@file:Suppress("DEPRECATION")

package com.rodrigocardenas.rickmortyapp.ui.screens.episodes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rodrigocardenas.rickmortyapp.core.Utils
import com.rodrigocardenas.rickmortyapp.ui.components.loading.LoadingScreenDialog
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rodrigocardenas.rickmortyapp.data.models.episode.Episode

sealed class EpisodeViewState {
    object Loading : EpisodeViewState()
    object LoadingNextPage : EpisodeViewState()
    data class Success(val episodes: List<Episode>) : EpisodeViewState()
    data class Error(val message: String) : EpisodeViewState()
}

@Composable
fun EpisodesScreen(modifier: Modifier = Modifier) {
    val episodeViewModel: EpisodeViewModel = hiltViewModel()
    val viewState by episodeViewModel.viewState.collectAsState()

    var allEpisodes by remember { mutableStateOf<List<Episode>>(emptyList()) }
    var isLoadingNextPage by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val shouldLoadNextPage: Boolean by remember {
        derivedStateOf {
            !isLoadingNextPage && allEpisodes.isNotEmpty() && listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == allEpisodes.size - 1
        }
    }

    LaunchedEffect(Unit) {
        if (allEpisodes.isEmpty()) {
            episodeViewModel.getAllEpisode()
        }
    }

    LaunchedEffect(shouldLoadNextPage) {
        if (shouldLoadNextPage) {
            isLoadingNextPage = true
            episodeViewModel.loadNextPage()
        }
    }

    when (val state = viewState) {
        is EpisodeViewState.Success -> {
            allEpisodes = (allEpisodes + state.episodes).distinctBy { it.id }
            isLoadingNextPage = false
        }
        is EpisodeViewState.Error -> {
            Utils().ShowInfoDialog(state.message) {}
        }
        is EpisodeViewState.Loading -> {
            LoadingScreenDialog()
        }
        EpisodeViewState.LoadingNextPage -> {
            isLoadingNextPage = true
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(viewState is EpisodeViewState.Loading),
        onRefresh = {
            episodeViewModel.resetEpisodes()
            allEpisodes = emptyList()
            episodeViewModel.getAllEpisode()
        }
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(allEpisodes) { episode ->
                EpisodeItem(episode = episode)
            }
        }
    }
}

@Composable
fun EpisodeItem(episode: Episode) {
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
                text = episode.episode.toString(),
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
                    text = episode.name.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = episode.air_date.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
    }
}

