package com.rodrigocardenas.rickmortyapp.ui.screens.searchcharacter

import com.rodrigocardenas.rickmortyapp.data.remote.RickMortyService
import javax.inject.Inject

class SearchCharacterRepository @Inject constructor(private val rickMortyService: RickMortyService) {
    suspend fun searchCharacter(page : Int, nameCharacter : String) = rickMortyService.searchCharacter(page,nameCharacter)
}