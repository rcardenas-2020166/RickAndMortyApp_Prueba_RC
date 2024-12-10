package com.rodrigocardenas.rickmortyapp.ui.screens.episodes

import com.rodrigocardenas.rickmortyapp.data.remote.RickMortyService
import javax.inject.Inject

class EpisodeRepository @Inject constructor(private val rickMortyService: RickMortyService) {
    suspend fun getAllEpisode(page : Int) = rickMortyService.getEpisodes(page)
}