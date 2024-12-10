package com.rodrigocardenas.rickmortyapp.data.remote

import com.rodrigocardenas.rickmortyapp.data.models.character.CharacterView
import com.rodrigocardenas.rickmortyapp.data.models.character.PaginatedCharacterResponse
import com.rodrigocardenas.rickmortyapp.data.models.episode.Episode
import com.rodrigocardenas.rickmortyapp.data.models.episode.PaginatedEpisodeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickMortyService {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): Response<PaginatedCharacterResponse>

    @GET("episode")
    suspend fun getEpisodes(@Query("page") page: Int): Response<PaginatedEpisodeResponse>

    @GET("character")
    suspend fun searchCharacter(
        @Query("page") page: Int,
        @Query("name") nameCharacter: String
    ): Response<PaginatedCharacterResponse>

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") id: Int
    ): Response<CharacterView>

    @GET("episode/{ids}")
    suspend fun getEpisodesCharacter(@Path("ids") ids: String): Response<List<Episode>>
}
