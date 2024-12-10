package com.rodrigocardenas.rickmortyapp.ui.screens.characters

import com.rodrigocardenas.rickmortyapp.data.remote.RickMortyService
import javax.inject.Inject

class CharacterRepository @Inject constructor(private val rickMortyService: RickMortyService) {
    suspend fun getAllCharacter(page : Int) = rickMortyService.getCharacters(page)
}