package com.sijan.bookandreadingtracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.usecase.AddBookToLibraryUseCase
import com.sijan.bookandreadingtracker.domain.usecase.GetRecommendedBooksUseCase
import com.sijan.bookandreadingtracker.domain.usecase.SearchBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecommendationState(
    val books: List<BookEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false
)

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val getRecommendedBooksUseCase: GetRecommendedBooksUseCase,
    private val searchBooksUseCase: SearchBooksUseCase,
    private val addBookToLibraryUseCase: AddBookToLibraryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RecommendationState())
    val state: StateFlow<RecommendationState> = _state.asStateFlow()

    init {
        loadRecommendedBooks()
    }

    fun loadRecommendedBooks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = getRecommendedBooksUseCase()
            result.fold(
                onSuccess = { books ->
                    _state.update {
                        it.copy(
                            books = books,
                            isEmpty = books.isEmpty(),
                            isLoading = false
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            error = exception.message ?: "Failed to load recommendations",
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    fun searchBooks(query: String) {
        if (query.isBlank()) {
            loadRecommendedBooks()
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, searchQuery = query) }

            val result = searchBooksUseCase(query)
            result.fold(
                onSuccess = { books ->
                    _state.update {
                        it.copy(
                            books = books,
                            isEmpty = books.isEmpty(),
                            isLoading = false
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            error = exception.message ?: "Search failed",
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    fun addToLibrary(book: BookEntity) {
        viewModelScope.launch {
            addBookToLibraryUseCase(book)
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

