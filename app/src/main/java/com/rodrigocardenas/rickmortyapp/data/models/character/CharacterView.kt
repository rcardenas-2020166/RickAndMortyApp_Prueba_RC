package com.rodrigocardenas.rickmortyapp.data.models.character

import com.rodrigocardenas.rickmortyapp.data.models.episode.Episode

data class CharacterView(
    val id: Int,
    val name: String?,
    val species : String?,
    val gender : String?,
    val status : String?,
    val episode : List<String>?,
    val location: Location?,
    val image : String?
)

data class Location(
    val name: String?,
    val url: String?
)


