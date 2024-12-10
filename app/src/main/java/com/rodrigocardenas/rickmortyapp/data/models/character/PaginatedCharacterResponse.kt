package com.rodrigocardenas.rickmortyapp.data.models.character

data class PaginatedCharacterResponse(
    val info : PageInfoCharacter,
    val results : List<Character>
)
