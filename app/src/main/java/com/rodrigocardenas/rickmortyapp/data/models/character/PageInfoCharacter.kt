package com.rodrigocardenas.rickmortyapp.data.models.character

data class PageInfoCharacter(
    val count : Long,
    val pages : Long,
    val next: String?,
    val prev: String?
)
