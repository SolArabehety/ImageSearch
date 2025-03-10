package com.solarabehety.imagesearch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solarabehety.core.model.SearchImageError
import com.solarabehety.core.networking.onError
import com.solarabehety.core.networking.onSuccess
import com.solarabehety.core.usecases.SearchImageUseCase
import com.solarabehety.imagesearch.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val stringMapper: StringMapper,
    private val searchImageUseCase: SearchImageUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchActivityUiState>(SearchActivityUiState.Initial)
    val uiState: StateFlow<SearchActivityUiState> get() = _uiState


    /**
     * Initiates the image search based on the provided query.
     * If the query is valid, it updates the UI state to reflect the loading, success, or error states.
     *
     * @param query The search query used to fetch images.
     */
    internal fun searchImages(query: String) {
        if (query.length >= SEARCH_MIN_QUERY_CHARS) {
            _uiState.value = SearchActivityUiState.Loading

            viewModelScope.launch {
                searchImageUseCase.invoke(query).onSuccess { images ->
                    _uiState.value = SearchActivityUiState.Success(images.map { it.url })
                }.onError { error ->
                    val message = stringMapper.mapErrorString(error)
                    _uiState.value = SearchActivityUiState.Error(message)
                }
            }
        } else {
            _uiState.value = SearchActivityUiState.Initial
        }
    }
}


/**
 * UI state for the Search screen
 */
internal sealed interface SearchActivityUiState {
    data object Initial : SearchActivityUiState
    data object Loading : SearchActivityUiState
    data class Error(val message: String) : SearchActivityUiState
    data class Success(val images: List<String>) : SearchActivityUiState
}

/**
 * Minimum number of characters required for a search query.
 */
internal const val SEARCH_MIN_QUERY_CHARS = 3
