package com.rodrigocardenas.rickmortyapp.data.models.episode

data class PageInfoEpisode(
    val count : Long,
    val pages : Long,
    val next: String?,
    val prev: String?
)
