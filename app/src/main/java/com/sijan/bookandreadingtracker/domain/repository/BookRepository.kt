package com.sijan.bookandreadingtracker.domain.repository

import com.sijan.bookandreadingtracker.data.local.BookEntity
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    // Local database operations
    fun getAllBooks(): Flow<List<BookEntity>>
    fun getLibraryBooks(): Flow<List<BookEntity>>
    fun getCurrentReads(): Flow<List<BookEntity>>
    fun getFinishedBooks(): Flow<List<BookEntity>>
    suspend fun getBookById(id: Long): BookEntity?
    fun observeBookById(id: Long): Flow<BookEntity?>

    suspend fun insertBook(book: BookEntity): Long
    suspend fun updateBook(book: BookEntity)
    suspend fun deleteBook(book: BookEntity)
    suspend fun deleteBookById(id: Long)

    suspend fun updateReadingProgress(bookId: Long, pagesRead: Int)
    suspend fun updatePersonalNote(bookId: Long, note: String)
    suspend fun addToLibrary(bookId: Long)
    suspend fun removeFromLibrary(bookId: Long)

    fun getFinishedBooksCount(): Flow<Int>
    fun getTotalPagesRead(): Flow<Int>

    // Remote API operations
    suspend fun searchRemoteBooks(query: String): Result<List<BookEntity>>
    suspend fun getRecommendedBooks(): Result<List<BookEntity>>
}

