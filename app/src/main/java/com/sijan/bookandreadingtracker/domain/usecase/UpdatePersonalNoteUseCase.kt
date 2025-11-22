package com.sijan.bookandreadingtracker.domain.usecase

import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import javax.inject.Inject

class UpdatePersonalNoteUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: Long, note: String) {
        repository.updatePersonalNote(bookId, note)
    }
}

