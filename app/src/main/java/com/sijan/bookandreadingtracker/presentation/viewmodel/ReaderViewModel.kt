package com.sijan.bookandreadingtracker.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import com.sijan.bookandreadingtracker.domain.usecase.GetBookByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReaderState(
    val book: BookEntity? = null,
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val repository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId: Long = savedStateHandle.get<String>("bookId")?.toLongOrNull() ?: 0L

    private val _state = MutableStateFlow(ReaderState())
    val state: StateFlow<ReaderState> = _state.asStateFlow()

    init {
        loadBook()
    }

    private fun loadBook() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getBookByIdUseCase.observe(bookId).collect { book ->
                _state.update {
                    it.copy(
                        book = book,
                        currentPage = book?.pagesRead ?: 0,
                        isLoading = false,
                        error = if (book == null) "Book not found" else null
                    )
                }
            }
        }
    }

    fun onPageChanged(page: Int) {
        _state.update { it.copy(currentPage = page) }

        viewModelScope.launch {
            val maxPages = _state.value.book?.pageCount ?: 0
            val pagesRead = page.coerceAtMost(maxPages)
            repository.updateReadingProgress(bookId, pagesRead)
        }
    }

    fun nextPage() {
        val currentBook = _state.value.book ?: return
        val newPage = (_state.value.currentPage + 1).coerceAtMost(currentBook.pageCount)
        onPageChanged(newPage)
    }

    fun previousPage() {
        val newPage = (_state.value.currentPage - 1).coerceAtLeast(0)
        onPageChanged(newPage)
    }
}

