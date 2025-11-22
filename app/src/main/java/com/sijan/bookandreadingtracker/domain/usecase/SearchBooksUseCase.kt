package com.sijan.bookandreadingtracker.domain.usecase

import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(query: String): Result<List<BookEntity>> {
        return repository.searchRemoteBooks(query)
    }
}

