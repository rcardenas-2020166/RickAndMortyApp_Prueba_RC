package com.rodrigocardenas.rickmortyapp.ui.screens.searchcharacter

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
import com.rodrigocardenas.rickmortyapp.data.models.character.Character
import kotlinx.coroutines.launch

@HiltViewModel
class SearchCharacterViewModel @Inject constructor(
    private val app: Application,
    private val searchCharacterRepository: SearchCharacterRepository,
    private val networkHelper: NetworkHelper
) : AndroidViewModel(app) {

    private val _viewState = MutableStateFlow<SearchCharacterViewState>(SearchCharacterViewState.Idle)
    val viewState: StateFlow<SearchCharacterViewState> = _viewState

    private var currentPage = 1
    private var totalPages: Long? = null
    private var currentSearchQuery: String? = null
    private val allCharacters = mutableListOf<Character>()
    private val addedCharacterIds = mutableSetOf<Int>()

    private suspend fun safeGetAllCharacterCall(page: Int, name: String?) {
        _viewState.value = SearchCharacterViewState.Loading
        try {
            if (networkHelper.isNetworkConnected()) {
                val response = searchCharacterRepository.searchCharacter(page, name.orEmpty())

                if (response.isSuccessful) {
                    val result = RetrofitHandleCall.handleCallResponse(response)
                    val newCharacters = result.data?.results ?: emptyList()
                    totalPages = result.data?.info?.pages
                    val uniqueNewCharacters = newCharacters.filter { character ->
                        addedCharacterIds.add(character.id)
                    }
                    allCharacters.addAll(uniqueNewCharacters)
                    _viewState.value = SearchCharacterViewState.Success(allCharacters)
                } else {
                    _viewState.value = SearchCharacterViewState.NotItems
                }
            } else {
                _viewState.value = SearchCharacterViewState.Error(
                    app.getString(R.string.global_message_no_internet)
                )
            }
        } catch (t: Throwable) {
            _viewState.value = when (t) {
                is IOException -> SearchCharacterViewState.Error(
                    app.getString(R.string.global_message_internet_failure)
                )
                else -> SearchCharacterViewState.Error(t.message.toString())
            }
        }
    }

    fun getAllCharacter(page: Int = currentPage, name: String? = currentSearchQuery) = viewModelScope.launch {
        safeGetAllCharacterCall(page, name)
    }

    fun loadNextPage() {
        if (totalPages != null && currentPage >= totalPages!!) {
            return
        }
        currentPage += 1
        getAllCharacter(page = currentPage, name = currentSearchQuery)
    }

    fun searchCharacterByName(name: String) {
        viewModelScope.launch {
            currentSearchQuery = name
            resetCharacters()
            getAllCharacter(page = currentPage, name = currentSearchQuery)
        }
    }

    fun resetCharacters() {
        currentPage = 1
        allCharacters.clear()
        addedCharacterIds.clear()
        _viewState.value = SearchCharacterViewState.Loading
    }
}
