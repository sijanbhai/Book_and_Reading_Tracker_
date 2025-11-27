package com.sijan.bookandreadingtracker.presentation.viewmodel

import app.cash.turbine.test
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.usecase.GetLibraryBooksUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private lateinit var viewModel: LibraryViewModel
    private lateinit var getLibraryBooksUseCase: GetLibraryBooksUseCase
    private val testDispatcher = StandardTestDispatcher()

    private val mockBooks = listOf(
        BookEntity(
            id = 1L,
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            description = "A classic novel",
            coverUrl = "url1",
            publishedDate = "1925",
            rating = 4.5f,
            pageCount = 180,
            isInLibrary = true,
            pagesRead = 90
        ),
        BookEntity(
            id = 2L,
            title = "To Kill a Mockingbird",
            author = "Harper Lee",
            description = "Another classic",
            coverUrl = "url2",
            publishedDate = "1960",
            rating = 4.8f,
            pageCount = 324,
            isInLibrary = true,
            pagesRead = 0
        ),
        BookEntity(
            id = 3L,
            title = "1984",
            author = "George Orwell",
            description = "Dystopian novel",
            coverUrl = "url3",
            publishedDate = "1949",
            rating = 4.7f,
            pageCount = 328,
            isInLibrary = true,
            pagesRead = 328
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getLibraryBooksUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadLibraryBooks should successfully load books`() = runTest {
        // Given
        coEvery { getLibraryBooksUseCase() } returns flowOf(mockBooks)

        // When
        viewModel = LibraryViewModel(getLibraryBooksUseCase)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(3, state.books.size)
            assertEquals(3, state.filteredBooks.size)
            assertFalse(state.isEmpty)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `onSearchQueryChange should filter books by title`() = runTest {
        // Given
        coEvery { getLibraryBooksUseCase() } returns flowOf(mockBooks)
        viewModel = LibraryViewModel(getLibraryBooksUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("gatsby")

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("gatsby", state.searchQuery)
            assertEquals(1, state.filteredBooks.size)
            assertEquals("The Great Gatsby", state.filteredBooks[0].title)
            assertEquals(3, state.books.size) // Original list unchanged
        }
    }

    @Test
    fun `onSearchQueryChange should filter books by author`() = runTest {
        // Given
        coEvery { getLibraryBooksUseCase() } returns flowOf(mockBooks)
        viewModel = LibraryViewModel(getLibraryBooksUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("harper lee")

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("harper lee", state.searchQuery)
            assertEquals(1, state.filteredBooks.size)
            assertEquals("Harper Lee", state.filteredBooks[0].author)
        }
    }

    @Test
    fun `onSearchQueryChange with blank query should show all books`() = runTest {
        // Given
        coEvery { getLibraryBooksUseCase() } returns flowOf(mockBooks)
        viewModel = LibraryViewModel(getLibraryBooksUseCase)
        advanceUntilIdle()

        // First filter books
        viewModel.onSearchQueryChange("gatsby")

        // When - clear search
        viewModel.onSearchQueryChange("")

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
            assertEquals(3, state.filteredBooks.size)
            assertEquals(3, state.books.size)
        }
    }

    @Test
    fun `onSearchQueryChange should be case insensitive`() = runTest {
        // Given
        coEvery { getLibraryBooksUseCase() } returns flowOf(mockBooks)
        viewModel = LibraryViewModel(getLibraryBooksUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("ORWELL")

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.filteredBooks.size)
            assertEquals("George Orwell", state.filteredBooks[0].author)
        }
    }

    @Test
    fun `onSearchQueryChange with no matches should return empty list`() = runTest {
        // Given
        coEvery { getLibraryBooksUseCase() } returns flowOf(mockBooks)
        viewModel = LibraryViewModel(getLibraryBooksUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("nonexistent book")

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("nonexistent book", state.searchQuery)
            assertTrue(state.filteredBooks.isEmpty())
            assertEquals(3, state.books.size) // Original list unchanged
        }
    }

    @Test
    fun `refreshLibrary should reload library books`() = runTest {
        // Given
        coEvery { getLibraryBooksUseCase() } returns flowOf(emptyList()) andThen flowOf(mockBooks)
        viewModel = LibraryViewModel(getLibraryBooksUseCase)
        advanceUntilIdle()

        // When
        viewModel.refreshLibrary()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(3, state.books.size)
            assertEquals(3, state.filteredBooks.size)
        }
    }

    @Test
    fun `isEmpty should be true when library is empty`() = runTest {
        // Given
        coEvery { getLibraryBooksUseCase() } returns flowOf(emptyList())

        // When
        viewModel = LibraryViewModel(getLibraryBooksUseCase)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isEmpty)
            assertTrue(state.books.isEmpty())
        }
    }

    @Test
    fun `isEmpty should be false when library has books`() = runTest {
        // Given
        coEvery { getLibraryBooksUseCase() } returns flowOf(mockBooks)

        // When
        viewModel = LibraryViewModel(getLibraryBooksUseCase)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isEmpty)
            assertEquals(3, state.books.size)
        }
    }

    @Test
    fun `search should maintain filtered state after refresh`() = runTest {
        // Given
        coEvery { getLibraryBooksUseCase() } returns flowOf(mockBooks)
        viewModel = LibraryViewModel(getLibraryBooksUseCase)
        advanceUntilIdle()

        // Set search query
        viewModel.onSearchQueryChange("1984")

        // When
        viewModel.refreshLibrary()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("1984", state.searchQuery)
            assertEquals(1, state.filteredBooks.size)
            assertEquals("1984", state.filteredBooks[0].title)
        }
    }

    @Test
    fun `filterBooks should handle partial matches`() = runTest {
        // Given
        coEvery { getLibraryBooksUseCase() } returns flowOf(mockBooks)
        viewModel = LibraryViewModel(getLibraryBooksUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("great")

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.filteredBooks.size)
            assertTrue(state.filteredBooks[0].title.contains("Great", ignoreCase = true))
        }
    }
}

