package com.sijan.bookandreadingtracker.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isInLibrary = 1")
    fun getLibraryBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isInLibrary = 1 AND pagesRead > 0 AND pagesRead < pageCount")
    fun getCurrentReads(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isInLibrary = 1 AND pagesRead >= pageCount AND pageCount > 0")
    fun getFinishedBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Long): BookEntity?

    @Query("SELECT * FROM books WHERE id = :id")
    fun observeBookById(id: Long): Flow<BookEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: Long)

    @Query("UPDATE books SET pagesRead = :pagesRead WHERE id = :bookId")
    suspend fun updateReadingProgress(bookId: Long, pagesRead: Int)

    @Query("UPDATE books SET personalNote = :note WHERE id = :bookId")
    suspend fun updatePersonalNote(bookId: Long, note: String)

    @Query("UPDATE books SET isInLibrary = :isInLibrary WHERE id = :bookId")
    suspend fun updateLibraryStatus(bookId: Long, isInLibrary: Boolean)

    @Query("SELECT COUNT(*) FROM books WHERE isInLibrary = 1 AND pagesRead >= pageCount AND pageCount > 0")
    fun getFinishedBooksCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(pagesRead), 0) FROM books WHERE isInLibrary = 1")
    fun getTotalPagesRead(): Flow<Int>
}

