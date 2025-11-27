package com.sijan.bookandreadingtracker.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sijan.bookandreadingtracker.data.local.AppDatabase
import com.sijan.bookandreadingtracker.data.local.BookDao
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.data.remote.GoogleBooksApiService
import com.sijan.bookandreadingtracker.data.remote.dto.BookItemDto
import com.sijan.bookandreadingtracker.data.remote.dto.GoogleBooksResponse
import com.sijan.bookandreadingtracker.data.remote.dto.ImageLinksDto
import com.sijan.bookandreadingtracker.data.remote.dto.VolumeInfoDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class BookRepositoryImplIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var bookDao: BookDao
    private lateinit var fakeApiService: FakeGoogleBooksApiService
    private lateinit var repository: BookRepositoryImpl

    private val testBook1 = BookEntity(
        id = 1L,
        title = "The Great Gatsby",
        author = "F. Scott Fitzgerald",
        description = "A classic American novel",
        coverUrl = "https://example.com/gatsby.jpg",
        publishedDate = "1925",
        rating = 4.5f,
        pageCount = 180,
        pdfUrl = "https://example.com/gatsby.pdf",
        isInLibrary = false,
        pagesRead = 0,
        personalNote = ""
    )

    private val testBook2 = BookEntity(
        id = 2L,
        title = "1984",
        author = "George Orwell",
        description = "Dystopian novel",
        coverUrl = "https://example.com/1984.jpg",
        publishedDate = "1949",
        rating = 4.8f,
        pageCount = 328,
        pdfUrl = "https://example.com/1984.pdf",
        isInLibrary = true,
        pagesRead = 164,
        personalNote = "Great book!"
    )

    private val testBook3 = BookEntity(
        id = 3L,
        title = "To Kill a Mockingbird",
        author = "Harper Lee",
        description = "Classic about racial injustice",
        coverUrl = "https://example.com/mockingbird.jpg",
        publishedDate = "1960",
        rating = 4.7f,
        pageCount = 324,
        pdfUrl = "https://example.com/mockingbird.pdf",
        isInLibrary = true,
        pagesRead = 324,
        personalNote = "Finished reading"
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Create in-memory database
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        bookDao = database.bookDao()
        fakeApiService = FakeGoogleBooksApiService()
        repository = BookRepositoryImpl(bookDao, fakeApiService)
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ============= Insert Operations =============

    @Test
    fun insertBook_shouldReturnBookIdAndBeRetrievable() = runTest {
        // When
        val insertedId = repository.insertBook(testBook1)

        // Then
        assertTrue(insertedId > 0)
        val retrievedBook = repository.getBookById(insertedId)
        assertNotNull(retrievedBook)
        assertEquals(testBook1.title, retrievedBook?.title)
        assertEquals(testBook1.author, retrievedBook?.author)

        val allBooks = repository.getAllBooks().first()
        assertEquals(1, allBooks.size)
    }

    // ============= Update Operations =============

    @Test
    fun updateBook_shouldModifyExistingBook() = runTest {
        // Given
        val insertedId = repository.insertBook(testBook1)
        val originalBook = repository.getBookById(insertedId)!!

        // When
        val updatedBook = originalBook.copy(
            title = "Updated Title",
            rating = 5.0f
        )
        repository.updateBook(updatedBook)

        // Then
        val retrievedBook = repository.getBookById(insertedId)
        assertEquals("Updated Title", retrievedBook?.title)
        assertEquals(5.0f, retrievedBook?.rating)
        assertEquals(originalBook.author, retrievedBook?.author)
    }

    @Test
    fun updateReadingProgress_shouldUpdatePagesRead() = runTest {
        // Given
        val bookId = repository.insertBook(testBook1)

        // When - Multiple updates
        repository.updateReadingProgress(bookId, 50)
        assertEquals(50, repository.getBookById(bookId)?.pagesRead)

        repository.updateReadingProgress(bookId, 150)

        // Then - Should keep latest value
        val book = repository.getBookById(bookId)
        assertEquals(150, book?.pagesRead)
    }

    @Test
    fun updatePersonalNote_shouldUpdateNote() = runTest {
        // Given
        val bookId = repository.insertBook(testBook1)

        // When
        val newNote = "This is my personal note about this book"
        repository.updatePersonalNote(bookId, newNote)

        // Then
        val book = repository.getBookById(bookId)
        assertEquals(newNote, book?.personalNote)
    }

    // ============= Delete Operations =============


    @Test
    fun deleteBookById_shouldRemoveFromDatabase() = runTest {
        // Given
        val bookId = repository.insertBook(testBook1)

        // When
        repository.deleteBookById(bookId)

        // Then
        val deletedBook = repository.getBookById(bookId)
        assertNull(deletedBook)
    }

    @Test
    fun deleteBook_shouldNotAffectOtherBooks() = runTest {
        // Given
        val id1 = repository.insertBook(testBook1)
        val id2 = repository.insertBook(testBook2)

        // When
        repository.deleteBookById(id1)

        // Then
        assertNull(repository.getBookById(id1))
        assertNotNull(repository.getBookById(id2))

        val allBooks = repository.getAllBooks().first()
        assertEquals(1, allBooks.size)
    }

    // ============= Library Operations =============

    @Test
    fun libraryOperations_shouldToggleStatus() = runTest {
        // Given
        val bookId = repository.insertBook(testBook1.copy(isInLibrary = false))

        // When - Add to library
        repository.addToLibrary(bookId)

        // Then
        var book = repository.getBookById(bookId)
        assertTrue(book?.isInLibrary == true)

        // When - Remove from library
        repository.removeFromLibrary(bookId)

        // Then
        book = repository.getBookById(bookId)
        assertFalse(book?.isInLibrary == true)
    }

    @Test
    fun getLibraryBooks_shouldReturnOnlyLibraryBooks() = runTest {
        // Given
        repository.insertBook(testBook1.copy(isInLibrary = false))
        repository.insertBook(testBook2.copy(isInLibrary = true))
        repository.insertBook(testBook3.copy(isInLibrary = true))

        // When
        val libraryBooks = repository.getLibraryBooks().first()

        // Then
        assertEquals(2, libraryBooks.size)
        assertTrue(libraryBooks.all { it.isInLibrary })
    }

    // ============= Current Reads Operations =============

    @Test
    fun getCurrentReads_shouldReturnOnlyBooksInProgress() = runTest {
        // Given - Book with 0 < pagesRead < pageCount
        val currentlyReading = testBook1.copy(
            isInLibrary = true,
            pagesRead = 90,
            pageCount = 180
        )
        val notStarted = testBook2.copy(
            isInLibrary = true,
            pagesRead = 0,
            pageCount = 328
        )
        val finished = testBook3.copy(
            isInLibrary = true,
            pagesRead = 324,
            pageCount = 324
        )
        val notInLibrary = testBook1.copy(
            id = 4L,
            isInLibrary = false,
            pagesRead = 90,
            pageCount = 180
        )

        repository.insertBook(currentlyReading)
        repository.insertBook(notStarted)
        repository.insertBook(finished)
        repository.insertBook(notInLibrary)

        // When
        val currentReads = repository.getCurrentReads().first()

        // Then - Should only include books in library with 0 < pages < total
        assertEquals(1, currentReads.size)
        assertEquals(currentlyReading.title, currentReads[0].title)
        assertTrue(currentReads[0].pagesRead > 0)
        assertTrue(currentReads[0].pagesRead < currentReads[0].pageCount)
    }


    // ============= Finished Books Operations =============

    @Test
    fun finishedBooksOperations_shouldFilterAndCountCorrectly() = runTest {
        // Given
        val finished1 = testBook1.copy(
            isInLibrary = true,
            pagesRead = 180,
            pageCount = 180
        )
        val finished2 = testBook3.copy(
            isInLibrary = true,
            pagesRead = 324,
            pageCount = 324
        )
        val inProgress = testBook2.copy(
            isInLibrary = true,
            pagesRead = 100,
            pageCount = 328
        )

        repository.insertBook(finished1)
        repository.insertBook(finished2)
        repository.insertBook(inProgress)

        // When
        val finishedBooks = repository.getFinishedBooks().first()
        val count = repository.getFinishedBooksCount().first()

        // Then
        assertEquals(2, finishedBooks.size)
        assertTrue(finishedBooks.all { it.pagesRead >= it.pageCount })
        assertEquals(2, count)
    }

    @Test
    fun getTotalPagesRead_shouldSumAllPagesReadInLibrary() = runTest {
        // Given
        repository.insertBook(testBook1.copy(
            isInLibrary = true,
            pagesRead = 100
        ))
        repository.insertBook(testBook2.copy(
            isInLibrary = true,
            pagesRead = 200
        ))
        repository.insertBook(testBook3.copy(
            isInLibrary = true,
            pagesRead = 300
        ))

        // When
        val totalPages = repository.getTotalPagesRead().first()

        // Then
        assertEquals(600, totalPages)
    }

    @Test
    fun getTotalPagesRead_shouldExcludeNonLibraryBooks() = runTest {
        // Given
        repository.insertBook(testBook1.copy(
            isInLibrary = true,
            pagesRead = 100
        ))
        repository.insertBook(testBook2.copy(
            isInLibrary = false,
            pagesRead = 500 // Should not be counted
        ))

        // When
        val totalPages = repository.getTotalPagesRead().first()

        // Then
        assertEquals(100, totalPages)
    }

    // ============= Progress Synchronization =============

    @Test
    fun progressSynchronization_shouldUpdateStateTransitionsAndCounts() = runTest {
        // Given - Book starts as current read
        val bookId = repository.insertBook(testBook1.copy(
            isInLibrary = true,
            pagesRead = 90,
            pageCount = 180
        ))

        // Initial state - in current reads
        var currentReads = repository.getCurrentReads().first()
        assertEquals(1, currentReads.size)
        assertEquals(0, repository.getFinishedBooksCount().first())
        assertEquals(90, repository.getTotalPagesRead().first())

        // When - Update progress
        repository.updateReadingProgress(bookId, 150)

        // Then - Still in current reads, total pages updated
        assertEquals(1, repository.getCurrentReads().first().size)
        assertEquals(150, repository.getTotalPagesRead().first())

        // When - Finish reading the book
        repository.updateReadingProgress(bookId, 180)

        // Then - Should move to finished books, counts updated
        currentReads = repository.getCurrentReads().first()
        val finishedBooks = repository.getFinishedBooks().first()

        assertEquals(0, currentReads.size)
        assertEquals(1, finishedBooks.size)
        assertEquals(testBook1.title, finishedBooks[0].title)
        assertEquals(1, repository.getFinishedBooksCount().first())
        assertEquals(180, repository.getTotalPagesRead().first())
    }

    // ============= Remote API Operations =============

    @Test
    fun remoteApiOperations_shouldSearchAndRecommend() = runTest {
        // Given - Setup search results
        fakeApiService.setSearchResults(createMockApiBooks(3))
        fakeApiService.setRecommendedBooks(createMockApiBooks(5))

        // When - Search books
        val searchResult = repository.searchRemoteBooks("kotlin")

        // Then - Search should succeed
        assertTrue(searchResult.isSuccess)
        assertEquals(3, searchResult.getOrNull()?.size)
        assertEquals("Remote Book 0", searchResult.getOrNull()?.get(0)?.title)

        // When - Get recommendations
        val recommendResult = repository.getRecommendedBooks()

        // Then - Recommendations should succeed
        assertTrue(recommendResult.isSuccess)
        assertEquals(5, recommendResult.getOrNull()?.size)
    }

    @Test
    fun remoteApiOperations_shouldHandleErrors() = runTest {
        // Given
        fakeApiService.setShouldThrowError(true)

        // When & Then - Search should fail
        val searchResult = repository.searchRemoteBooks("error")
        assertTrue(searchResult.isFailure)
        assertNotNull(searchResult.exceptionOrNull())

        // When & Then - Recommendations should fail
        val recommendResult = repository.getRecommendedBooks()
        assertTrue(recommendResult.isFailure)
    }

    @Test
    fun addBookFromRemoteApi_shouldBeInsertableToLocalDatabase() = runTest {
        // Given - Get book from API
        fakeApiService.setSearchResults(createMockApiBooks(1))
        val apiResult = repository.searchRemoteBooks("test")
        val remoteBook = apiResult.getOrNull()?.first()!!

        // When - Insert to local database
        val insertedId = repository.insertBook(remoteBook)

        // Then - Should be retrievable
        val localBook = repository.getBookById(insertedId)
        assertNotNull(localBook)
        assertEquals(remoteBook.title, localBook?.title)
        assertEquals(remoteBook.author, localBook?.author)
    }

    @Test
    fun addRemoteBookToLibrary_shouldWorkCorrectly() = runTest {
        // Given - Get and insert book from API
        fakeApiService.setSearchResults(createMockApiBooks(1))
        val apiResult = repository.searchRemoteBooks("test")
        val remoteBook = apiResult.getOrNull()?.first()!!
        val bookId = repository.insertBook(remoteBook)

        // Initially not in library
        assertFalse(repository.getBookById(bookId)?.isInLibrary == true)

        // When - Add to library
        repository.addToLibrary(bookId)

        // Then - Should appear in library
        val libraryBooks = repository.getLibraryBooks().first()
        assertEquals(1, libraryBooks.size)
        assertTrue(libraryBooks[0].isInLibrary)
    }


    // ============= Complex Integration Scenarios =============

    @Test
    fun complexScenario_addSearchReadFinish() = runTest {
        // Scenario: Search for a book, add to library, read it, and finish it

        // 1. Search for book from API
        fakeApiService.setSearchResults(createMockApiBooks(1))
        val searchResult = repository.searchRemoteBooks("kotlin programming")
        assertTrue(searchResult.isSuccess)

        val remoteBook = searchResult.getOrNull()?.first()!!

        // 2. Add to local database
        val bookId = repository.insertBook(remoteBook)

        // 3. Add to library
        repository.addToLibrary(bookId)

        // Verify in library
        val libraryBooks = repository.getLibraryBooks().first()
        assertEquals(1, libraryBooks.size)

        // 4. Start reading
        repository.updateReadingProgress(bookId, 50)

        // Verify in current reads
        var currentReads = repository.getCurrentReads().first()
        assertEquals(1, currentReads.size)

        // 5. Continue reading
        repository.updateReadingProgress(bookId, 150)

        // 6. Finish the book
        val book = repository.getBookById(bookId)!!
        repository.updateReadingProgress(bookId, book.pageCount)

        // 7. Add personal note
        repository.updatePersonalNote(bookId, "Excellent book on Kotlin!")

        // Verify final state
        currentReads = repository.getCurrentReads().first()
        val finishedBooks = repository.getFinishedBooks().first()
        val finishedCount = repository.getFinishedBooksCount().first()
        val totalPages = repository.getTotalPagesRead().first()

        assertEquals(0, currentReads.size)
        assertEquals(1, finishedBooks.size)
        assertEquals(1, finishedCount)
        assertEquals(book.pageCount, totalPages)
        assertEquals("Excellent book on Kotlin!", finishedBooks[0].personalNote)
    }

    @Test
    fun complexScenario_multipleUsersReadingDifferentBooks() = runTest {
        // Scenario: Multiple books at different reading stages

        // Book 1: Not started, in library
        repository.insertBook(testBook1.copy(
            isInLibrary = true,
            pagesRead = 0
        ))

        // Book 2: Currently reading
        repository.insertBook(testBook2.copy(
            isInLibrary = true,
            pagesRead = 150,
            pageCount = 328
        ))

        // Book 3: Finished
        repository.insertBook(testBook3.copy(
            isInLibrary = true,
            pagesRead = 324,
            pageCount = 324
        ))

        // Book 4: Not in library (searched but not added)
        repository.insertBook(testBook1.copy(
            id = 4L,
            title = "Not in Library",
            isInLibrary = false,
            pagesRead = 0
        ))

        // Verify states
        val libraryBooks = repository.getLibraryBooks().first()
        val currentReads = repository.getCurrentReads().first()
        val finishedBooks = repository.getFinishedBooks().first()
        val totalPages = repository.getTotalPagesRead().first()
        val finishedCount = repository.getFinishedBooksCount().first()

        assertEquals(3, libraryBooks.size)
        assertEquals(1, currentReads.size)
        assertEquals(1, finishedBooks.size)
        assertEquals(474, totalPages) // 0 + 150 + 324
        assertEquals(1, finishedCount)
    }

    // ============= Helper Methods =============

    private fun createMockApiBooks(count: Int): List<BookItemDto> {
        return (0 until count).map { index ->
            BookItemDto(
                id = "api_id_$index",
                volumeInfo = VolumeInfoDto(
                    title = "Remote Book $index",
                    authors = listOf("API Author $index"),
                    description = "Description from API $index",
                    imageLinks = ImageLinksDto(
                        thumbnail = "http://example.com/cover_$index.jpg",
                        smallThumbnail = "http://example.com/small_$index.jpg"
                    ),
                    publishedDate = "2023",
                    averageRating = 4.0f + (index * 0.1f),
                    pageCount = 200 + (index * 50),
                    previewLink = "https://example.com/preview_$index"
                )
            )
        }
    }
}

// ============= Fake Google Books API Service =============

class FakeGoogleBooksApiService : GoogleBooksApiService {

    private var searchResults: List<BookItemDto> = emptyList()
    private var recommendedBooks: List<BookItemDto> = emptyList()
    private var shouldThrowError: Boolean = false

    fun setSearchResults(books: List<BookItemDto>) {
        searchResults = books
    }

    fun setRecommendedBooks(books: List<BookItemDto>) {
        recommendedBooks = books
    }

    fun setShouldThrowError(shouldThrow: Boolean) {
        shouldThrowError = shouldThrow
    }

    override suspend fun searchBooks(
        query: String,
        maxResults: Int,
        startIndex: Int
    ): GoogleBooksResponse {
        if (shouldThrowError) {
            throw IOException("Network error")
        }
        return GoogleBooksResponse(
            items = searchResults,
            totalItems = searchResults.size
        )
    }

    override suspend fun getRecommendedBooks(
        category: String,
        orderBy: String,
        maxResults: Int
    ): GoogleBooksResponse {
        if (shouldThrowError) {
            throw IOException("Network error")
        }
        return GoogleBooksResponse(
            items = recommendedBooks,
            totalItems = recommendedBooks.size
        )
    }
}

