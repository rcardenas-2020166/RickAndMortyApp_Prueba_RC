package com.rodrigocardenas.rickmortyapp.ui.screens.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigocardenas.rickmortyapp.core.NetworkHelper
import com.rodrigocardenas.rickmortyapp.core.RetrofitHandleCall
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import com.rodrigocardenas.rickmortyapp.R
import com.rodrigocardenas.rickmortyapp.core.Resource
import com.rodrigocardenas.rickmortyapp.data.models.character.CharacterView
import com.rodrigocardenas.rickmortyapp.data.models.episode.Episode
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class CharacterViewViewModel @Inject constructor(
    private val app: Application,
    private val characterViewRepository: CharacterViewRepository,
    private val networkHelper: NetworkHelper
) : AndroidViewModel(app) {

    private val _viewCharacter = MutableStateFlow<Resource<CharacterView>>(Resource.Loading())
    val viewCharacter: StateFlow<Resource<CharacterView>> = _viewCharacter.asStateFlow()

    private val _viewEpisodes = MutableStateFlow<Resource<List<Episode>>>(Resource.Loading())
    val viewEpisodes: StateFlow<Resource<List<Episode>>> = _viewEpisodes.asStateFlow()


    private suspend fun safeGetCharacterCall(idCharacter: Int) {
        _viewCharacter.value = Resource.Loading()
        try {
            if (networkHelper.isNetworkConnected()) {
                val response = characterViewRepository.getCharacter(idCharacter)
                if (response.isSuccessful) {
                    _viewCharacter.value = RetrofitHandleCall.handleCallResponse(response)
                    val episodeUrls = _viewCharacter.value.data?.episode ?: emptyList()
                    val episodeIds = extractEpisodeIdsFromUrls(episodeUrls)
                    val episodeResponse = characterViewRepository.getEpisodesCharacter(episodeIds.toString())
                    if (episodeResponse.isSuccessful) {
                        _viewEpisodes.value = RetrofitHandleCall.handleCallResponse(episodeResponse)
                    } else {
                        _viewEpisodes.value = Resource.Error(
                            "Error ${episodeResponse.code()}: ${episodeResponse.message()}"
                        )
                    }
                } else {
                    _viewCharacter.value = Resource.Error(
                        "Error ${response.code()}: ${response.message()}"
                    )
                }
            } else {
                _viewCharacter.value = Resource.Error(
                    app.getString(R.string.global_message_no_internet)
                )
            }
        } catch (t: Throwable) {
            _viewCharacter.value = when (t) {
                is IOException -> Resource.Error(
                    app.getString(R.string.global_message_internet_failure)
                )
                else -> Resource.Error(t.message.toString())
            }
        }
    }

    fun getAllCharacter(idCharacter: Int) = viewModelScope.launch {
        safeGetCharacterCall(idCharacter)
    }

    private fun extractEpisodeIdsFromUrls(episodeUrls: List<String>): List<Int> {
        return episodeUrls.mapNotNull { url ->
            val regex = """episode/(\d+)""".toRegex()
            regex.find(url)?.groupValues?.get(1)?.toIntOrNull()
        }
    }

}
