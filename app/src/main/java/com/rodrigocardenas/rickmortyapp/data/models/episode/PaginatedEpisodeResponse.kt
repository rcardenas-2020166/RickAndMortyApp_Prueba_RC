package com.rodrigocardenas.rickmortyapp.data.models.episode

data class PaginatedEpisodeResponse(
    val info : PageInfoEpisode,
    val results : List<Episode>
)
