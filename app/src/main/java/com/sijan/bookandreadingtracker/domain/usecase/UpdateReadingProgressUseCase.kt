package com.sijan.bookandreadingtracker.domain.usecase

import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import javax.inject.Inject

class UpdateReadingProgressUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: Long, pagesRead: Int) {
        repository.updateReadingProgress(bookId, pagesRead)
    }
}

