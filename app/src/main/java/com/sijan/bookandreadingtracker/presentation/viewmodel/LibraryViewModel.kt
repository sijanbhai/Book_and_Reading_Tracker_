package com.sijan.bookandreadingtracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.usecase.GetLibraryBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryState(
    val books: List<BookEntity> = emptyList(),
    val filteredBooks: List<BookEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getLibraryBooksUseCase: GetLibraryBooksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state.asStateFlow()

    init {
        loadLibraryBooks()
    }

    private fun loadLibraryBooks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getLibraryBooksUseCase().collect { books ->
                _state.update {
                    it.copy(
                        books = books,
                        filteredBooks = filterBooks(books, it.searchQuery),
                        isEmpty = books.isEmpty(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                filteredBooks = filterBooks(it.books, query)
            )
        }
    }

    private fun filterBooks(books: List<BookEntity>, query: String): List<BookEntity> {
        if (query.isBlank()) return books

        val lowerQuery = query.lowercase()
        return books.filter {
            it.title.lowercase().contains(lowerQuery) ||
            it.author.lowercase().contains(lowerQuery)
        }
    }

    fun refreshLibrary() {
        loadLibraryBooks()
    }
}

