package com.sijan.bookandreadingtracker.domain.usecase

import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend fun get(id: Long): BookEntity? {
        return repository.getBookById(id)
    }

    fun observe(id: Long): Flow<BookEntity?> {
        return repository.observeBookById(id)
    }
}

