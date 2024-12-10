package com.rodrigocardenas.rickmortyapp.ui.screens.character

import com.rodrigocardenas.rickmortyapp.data.remote.RickMortyService
import javax.inject.Inject

class CharacterViewRepository @Inject constructor(private val rickMortyService: RickMortyService) {
    suspend fun getCharacter(idCharacter : Int) = rickMortyService.getCharacter(idCharacter)
    suspend fun getEpisodesCharacter(idEpisodes : String) = rickMortyService.getEpisodesCharacter(idEpisodes)
}