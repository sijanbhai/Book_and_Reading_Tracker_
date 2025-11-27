package com.sijan.bookandreadingtracker.domain.usecase

import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetLibraryBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    operator fun invoke(): Flow<List<BookEntity>> {
        return repository.getLibraryBooks()
    }
}
