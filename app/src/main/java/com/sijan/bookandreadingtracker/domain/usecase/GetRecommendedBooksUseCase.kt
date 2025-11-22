package com.sijan.bookandreadingtracker.domain.usecase

import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import javax.inject.Inject

class GetRecommendedBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(): Result<List<BookEntity>> {
        return repository.getRecommendedBooks()
    }
}

