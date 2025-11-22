package com.sijan.bookandreadingtracker.domain.usecase

import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import javax.inject.Inject

class AddBookToLibraryUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(book: BookEntity): Long {
        val updatedBook = book.copy(isInLibrary = true)
        return repository.insertBook(updatedBook)
    }
}

