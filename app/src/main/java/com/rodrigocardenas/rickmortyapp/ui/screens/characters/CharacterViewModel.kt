package com.rodrigocardenas.rickmortyapp.ui.screens.characters

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
class CharacterViewModel @Inject constructor(
    private val app: Application,
    private val characterRepository: CharacterRepository,
    private val networkHelper: NetworkHelper
) : AndroidViewModel(app) {

    private val _viewState = MutableStateFlow<CharacterViewState>(CharacterViewState.Loading)
    val viewState: StateFlow<CharacterViewState> = _viewState

    private var currentPage = 1
    private val allCharacters = mutableListOf<Character>()
    private val addedCharacterIds = mutableSetOf<Int>()

    private suspend fun safeGetAllCharacterCall(page: Int) {
        _viewState.value = CharacterViewState.Loading
        try {
            if (networkHelper.isNetworkConnected()) {
                val response = characterRepository.getAllCharacter(page)

                if (response.isSuccessful) {
                    val result = RetrofitHandleCall.handleCallResponse(response)
                    val newCharacters = result.data?.results ?: emptyList()
                    val uniqueNewCharacters = newCharacters.filter { character ->
                        addedCharacterIds.add(character.id)
                    }
                    allCharacters.addAll(uniqueNewCharacters)
                    _viewState.value = CharacterViewState.Success(allCharacters)
                } else {
                    _viewState.value = CharacterViewState.Error(
                        "Error ${response.code()}: ${response.message()}"
                    )
                }
            } else {
                _viewState.value = CharacterViewState.Error(
                    app.getString(R.string.global_message_no_internet)
                )
            }
        } catch (t: Throwable) {
            _viewState.value = when (t) {
                is IOException -> CharacterViewState.Error(
                    app.getString(R.string.global_message_internet_failure)
                )
                else -> CharacterViewState.Error(t.message.toString())
            }
        }
    }


    fun getAllCharacter(page: Int = currentPage) = viewModelScope.launch {
        safeGetAllCharacterCall(page)
    }

    fun loadNextPage() {
        currentPage += 1
        getAllCharacter(page = currentPage)
    }

    fun resetCharacters() {
        currentPage = 1
        allCharacters.clear()
        addedCharacterIds.clear()
        _viewState.value = CharacterViewState.Loading
        getAllCharacter()
    }
}
