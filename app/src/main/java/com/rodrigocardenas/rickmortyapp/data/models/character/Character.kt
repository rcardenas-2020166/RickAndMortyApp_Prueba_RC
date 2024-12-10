package com.rodrigocardenas.rickmortyapp.data.models.character

data class Character(
    val id: Int,
    val name: String,
    val species : String?,
    val gender : String?,
    val status : String?,
    val image : String?
)
