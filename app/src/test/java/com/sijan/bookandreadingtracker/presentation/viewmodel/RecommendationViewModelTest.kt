package com.sijan.bookandreadingtracker.presentation.viewmodel

import app.cash.turbine.test
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.usecase.AddBookToLibraryUseCase
import com.sijan.bookandreadingtracker.domain.usecase.GetRecommendedBooksUseCase
import com.sijan.bookandreadingtracker.domain.usecase.SearchBooksUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecommendationViewModelTest {

    private lateinit var viewModel: RecommendationViewModel
    private lateinit var getRecommendedBooksUseCase: GetRecommendedBooksUseCase
    private lateinit var searchBooksUseCase: SearchBooksUseCase
    private lateinit var addBookToLibraryUseCase: AddBookToLibraryUseCase
    private val testDispatcher = StandardTestDispatcher()

    private val mockRecommendedBooks = listOf(
        BookEntity(
            id = 1L,
            title = "Recommended Book 1",
            author = "Author 1",
            description = "Description 1",
            coverUrl = "url1",
            publishedDate = "2023",
            rating = 4.5f,
            pageCount = 300,
            isInLibrary = false,
            pagesRead = 0
        ),
        BookEntity(
            id = 2L,
            title = "Recommended Book 2",
            author = "Author 2",
            description = "Description 2",
            coverUrl = "url2",
            publishedDate = "2024",
            rating = 4.3f,
            pageCount = 250,
            isInLibrary = false,
            pagesRead = 0
        )
    )

    private val mockSearchResults = listOf(
        BookEntity(
            id = 3L,
            title = "Search Result Book",
            author = "Search Author",
            description = "Search Description",
            coverUrl = "url3",
            publishedDate = "2022",
            rating = 4.0f,
            pageCount = 400,
            isInLibrary = false,
            pagesRead = 0
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getRecommendedBooksUseCase = mockk()
        searchBooksUseCase = mockk()
        addBookToLibraryUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.success(emptyList())

        // When
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isLoading)

            advanceUntilIdle()
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertTrue(finalState.books.isEmpty())
            assertEquals("", finalState.searchQuery)
            assertNull(finalState.error)
            assertTrue(finalState.isEmpty)
        }
    }

    @Test
    fun `loadRecommendedBooks should successfully load recommendations`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.success(mockRecommendedBooks)

        // When
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.books.size)
            assertFalse(state.isEmpty)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `loadRecommendedBooks should handle error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getRecommendedBooksUseCase() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.books.isEmpty())
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
        }
    }

    @Test
    fun `loadRecommendedBooks should handle generic error message`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.failure(Exception())

        // When
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Failed to load recommendations", state.error)
        }
    }

    @Test
    fun `searchBooks should successfully search for books`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.success(mockRecommendedBooks)
        coEvery { searchBooksUseCase("kotlin") } returns Result.success(mockSearchResults)
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.searchBooks("kotlin")
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.books.size)
            assertEquals("kotlin", state.searchQuery)
            assertEquals("Search Result Book", state.books[0].title)
            assertNull(state.error)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `searchBooks should handle search error`() = runTest {
        // Given
        val errorMessage = "Search failed"
        coEvery { getRecommendedBooksUseCase() } returns Result.success(mockRecommendedBooks)
        coEvery { searchBooksUseCase("kotlin") } returns Result.failure(Exception(errorMessage))
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.searchBooks("kotlin")
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `searchBooks with blank query should load recommendations`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.success(mockRecommendedBooks)
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.searchBooks("")
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.books.size)
            assertEquals("Recommended Book 1", state.books[0].title)
        }
        coVerify(exactly = 2) { getRecommendedBooksUseCase() }
    }

    @Test
    fun `searchBooks with whitespace should load recommendations`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.success(mockRecommendedBooks)
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.searchBooks("   ")
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.books.size)
        }
    }

    @Test
    fun `addToLibrary should call use case`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.success(mockRecommendedBooks)
        coEvery { addBookToLibraryUseCase(any()) } returns Unit
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        val bookToAdd = mockRecommendedBooks[0]

        // When
        viewModel.addToLibrary(bookToAdd)
        advanceUntilIdle()

        // Then
        coVerify { addBookToLibraryUseCase(bookToAdd) }
    }

    @Test
    fun `clearError should set error to null`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.failure(Exception("Error"))
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.error)
        }
    }

    @Test
    fun `isEmpty should be true when no books are loaded`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.success(emptyList())

        // When
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isEmpty)
        }
    }

    @Test
    fun `isEmpty should be false when books are loaded`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.success(mockRecommendedBooks)

        // When
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isEmpty)
        }
    }

    @Test
    fun `state should show loading when data is being fetched`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.success(mockRecommendedBooks)

        // When
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )

        // Then
        viewModel.state.test {
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            advanceUntilIdle()
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
        }
    }

    @Test
    fun `searchBooks should update searchQuery in state`() = runTest {
        // Given
        val searchQuery = "android development"
        coEvery { getRecommendedBooksUseCase() } returns Result.success(mockRecommendedBooks)
        coEvery { searchBooksUseCase(searchQuery) } returns Result.success(mockSearchResults)
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.searchBooks(searchQuery)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(searchQuery, state.searchQuery)
        }
    }

    @Test
    fun `searchBooks with no results should set isEmpty to true`() = runTest {
        // Given
        coEvery { getRecommendedBooksUseCase() } returns Result.success(mockRecommendedBooks)
        coEvery { searchBooksUseCase("nonexistent") } returns Result.success(emptyList())
        viewModel = RecommendationViewModel(
            getRecommendedBooksUseCase,
            searchBooksUseCase,
            addBookToLibraryUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.searchBooks("nonexistent")
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isEmpty)
            assertTrue(state.books.isEmpty())
        }
    }
}

