package com.sijan.bookandreadingtracker.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import com.sijan.bookandreadingtracker.domain.usecase.GetBookByIdUseCase
import com.sijan.bookandreadingtracker.domain.usecase.UpdatePersonalNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookDetailsState(
    val book: BookEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val updatePersonalNoteUseCase: UpdatePersonalNoteUseCase,
    private val repository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId: Long = savedStateHandle.get<String>("bookId")?.toLongOrNull() ?: 0L

    private val _state = MutableStateFlow(BookDetailsState())
    val state: StateFlow<BookDetailsState> = _state.asStateFlow()

    init {
        loadBookDetails()
    }

    private fun loadBookDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getBookByIdUseCase.observe(bookId).collect { book ->
                _state.update {
                    it.copy(
                        book = book,
                        isLoading = false,
                        error = if (book == null) "Book not found" else null
                    )
                }
            }
        }
    }

    fun updatePersonalNote(note: String) {
        viewModelScope.launch {
            updatePersonalNoteUseCase(bookId, note)
        }
    }

    fun toggleLibraryStatus() {
        val currentBook = _state.value.book ?: return

        viewModelScope.launch {
            if (currentBook.isInLibrary) {
                repository.removeFromLibrary(bookId)
            } else {
                repository.addToLibrary(bookId)
            }
        }
    }

    fun updateReadingProgress(pagesRead: Int) {
        viewModelScope.launch {
            repository.updateReadingProgress(bookId, pagesRead)
        }
    }
}

