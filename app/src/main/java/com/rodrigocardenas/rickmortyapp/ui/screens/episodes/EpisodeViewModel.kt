package com.rodrigocardenas.rickmortyapp.ui.screens.episodes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigocardenas.rickmortyapp.core.NetworkHelper
import com.rodrigocardenas.rickmortyapp.core.RetrofitHandleCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import javax.inject.Inject
import com.rodrigocardenas.rickmortyapp.R
import com.rodrigocardenas.rickmortyapp.data.models.episode.Episode
import kotlinx.coroutines.launch

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val app: Application,
    private val episodeRepository: EpisodeRepository,
    private val networkHelper: NetworkHelper
) : AndroidViewModel(app) {

    private val _viewState = MutableStateFlow<EpisodeViewState>(EpisodeViewState.Loading)
    val viewState: StateFlow<EpisodeViewState> = _viewState

    private var currentPage = 1
    private var totalPages: Long? = null
    private val allEpisodes = mutableListOf<Episode>()
    private val addedEpisodeIds = mutableSetOf<Int>()

    private suspend fun safeGetAllEpisodeCall(page: Int) {
        _viewState.value = EpisodeViewState.Loading
        try {
            if (networkHelper.isNetworkConnected()) {
                val response = episodeRepository.getAllEpisode(page)

                if (response.isSuccessful) {
                    val result = RetrofitHandleCall.handleCallResponse(response)
                    val newEpisodes = result.data?.results ?: emptyList()
                    totalPages = result.data?.info?.pages
                    val uniqueNewEpisodes = newEpisodes.filter { episode ->
                        addedEpisodeIds.add(episode.id)
                    }
                    allEpisodes.addAll(uniqueNewEpisodes)
                    _viewState.value = EpisodeViewState.Success(allEpisodes)
                } else {
                    _viewState.value = EpisodeViewState.Error(
                        "Error ${response.code()}: ${response.message()}"
                    )
                }
            } else {
                _viewState.value = EpisodeViewState.Error(
                    app.getString(R.string.global_message_no_internet)
                )
            }
        } catch (t: Throwable) {
            _viewState.value = when (t) {
                is IOException -> EpisodeViewState.Error(
                    app.getString(R.string.global_message_internet_failure)
                )
                else -> EpisodeViewState.Error(t.message.toString())
            }
        }
    }

    fun getAllEpisode(page: Int = currentPage) = viewModelScope.launch {
        safeGetAllEpisodeCall(page)
    }

    fun loadNextPage() {
        if (totalPages != null && currentPage >= totalPages!!) {
            return
        }
        currentPage += 1
        getAllEpisode(page = currentPage)
    }

    fun resetEpisodes() {
        currentPage = 1
        totalPages = null
        allEpisodes.clear()
        addedEpisodeIds.clear()
        _viewState.value = EpisodeViewState.Loading
        getAllEpisode()
    }
}
