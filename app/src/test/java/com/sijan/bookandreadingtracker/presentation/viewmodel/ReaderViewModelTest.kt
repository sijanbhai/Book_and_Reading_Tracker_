package com.sijan.bookandreadingtracker.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import com.sijan.bookandreadingtracker.domain.usecase.GetBookByIdUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
class ReaderViewModelTest {

    private lateinit var viewModel: ReaderViewModel
    private lateinit var getBookByIdUseCase: GetBookByIdUseCase
    private lateinit var repository: BookRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()

    private val mockBook = BookEntity(
        id = 1L,
        title = "Test Book",
        author = "Test Author",
        description = "Test Description",
        coverUrl = "test_url",
        publishedDate = "2023",
        rating = 4.5f,
        pageCount = 300,
        isInLibrary = true,
        pagesRead = 150,
        personalNote = ""
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getBookByIdUseCase = mockk()
        repository = mockk()
        savedStateHandle = mockk()

        every { savedStateHandle.get<String>("bookId") } returns "1"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(null)

        // When
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isLoading)

            advanceUntilIdle()
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertNull(finalState.book)
            assertEquals(0, finalState.currentPage)
            assertEquals("Book not found", finalState.error)
        }
    }

    @Test
    fun `loadBook should successfully load book`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)

        // When
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.book)
            assertEquals("Test Book", state.book?.title)
            assertEquals(150, state.currentPage)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `loadBook should handle book not found`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(null)

        // When
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.book)
            assertEquals("Book not found", state.error)
            assertEquals(0, state.currentPage)
        }
    }

    @Test
    fun `onPageChanged should update current page and repository`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)
        coEvery { repository.updateReadingProgress(1L, any()) } returns Unit
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.onPageChanged(200)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(200, state.currentPage)
        }
        coVerify { repository.updateReadingProgress(1L, 200) }
    }

    @Test
    fun `onPageChanged should not exceed page count`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)
        coEvery { repository.updateReadingProgress(1L, any()) } returns Unit
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.onPageChanged(400) // Exceeds page count of 300
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(400, state.currentPage)
        }
        coVerify { repository.updateReadingProgress(1L, 300) } // Should be capped at 300
    }

    @Test
    fun `nextPage should increment page correctly`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)
        coEvery { repository.updateReadingProgress(1L, any()) } returns Unit
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.nextPage()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(151, state.currentPage)
        }
        coVerify { repository.updateReadingProgress(1L, 151) }
    }

    @Test
    fun `nextPage should not exceed page count`() = runTest {
        // Given
        val bookAtLastPage = mockBook.copy(pagesRead = 300)
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(bookAtLastPage)
        coEvery { repository.updateReadingProgress(1L, any()) } returns Unit
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.nextPage()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(300, state.currentPage)
        }
        coVerify { repository.updateReadingProgress(1L, 300) }
    }

    @Test
    fun `previousPage should decrement page correctly`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)
        coEvery { repository.updateReadingProgress(1L, any()) } returns Unit
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.previousPage()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(149, state.currentPage)
        }
        coVerify { repository.updateReadingProgress(1L, 149) }
    }

    @Test
    fun `previousPage should not go below zero`() = runTest {
        // Given
        val bookAtFirstPage = mockBook.copy(pagesRead = 0)
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(bookAtFirstPage)
        coEvery { repository.updateReadingProgress(1L, any()) } returns Unit
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.previousPage()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(0, state.currentPage)
        }
        coVerify { repository.updateReadingProgress(1L, 0) }
    }

    @Test
    fun `nextPage should do nothing when book is null`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(null)
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.nextPage()
        advanceUntilIdle()

        // Then
        // Should not crash, just do nothing
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.book)
        }
    }

    @Test
    fun `state should show loading when data is being fetched`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)

        // When
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
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
    fun `bookId should be extracted from savedStateHandle`() = runTest {
        // Given
        every { savedStateHandle.get<String>("bookId") } returns "42"
        coEvery { getBookByIdUseCase.observe(42L) } returns flowOf(mockBook.copy(id = 42L))

        // When
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        coVerify { getBookByIdUseCase.observe(42L) }
    }

    @Test
    fun `invalid bookId should default to 0`() = runTest {
        // Given
        every { savedStateHandle.get<String>("bookId") } returns "invalid"
        coEvery { getBookByIdUseCase.observe(0L) } returns flowOf(null)

        // When
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        coVerify { getBookByIdUseCase.observe(0L) }
    }

    @Test
    fun `null bookId should default to 0`() = runTest {
        // Given
        every { savedStateHandle.get<String>("bookId") } returns null
        coEvery { getBookByIdUseCase.observe(0L) } returns flowOf(null)

        // When
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        coVerify { getBookByIdUseCase.observe(0L) }
    }

    @Test
    fun `currentPage should be initialized from book pagesRead`() = runTest {
        // Given
        val bookWithProgress = mockBook.copy(pagesRead = 75)
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(bookWithProgress)

        // When
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(75, state.currentPage)
        }
    }

    @Test
    fun `onPageChanged with page 0 should work`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)
        coEvery { repository.updateReadingProgress(1L, 0) } returns Unit
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.onPageChanged(0)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(0, state.currentPage)
        }
        coVerify { repository.updateReadingProgress(1L, 0) }
    }

    @Test
    fun `multiple page changes should work correctly`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)
        coEvery { repository.updateReadingProgress(1L, any()) } returns Unit
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.nextPage()
        advanceUntilIdle()
        viewModel.nextPage()
        advanceUntilIdle()
        viewModel.previousPage()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(151, state.currentPage) // 150 + 1 + 1 - 1 = 151
        }
    }

    @Test
    fun `book with 0 page count should handle page changes`() = runTest {
        // Given
        val bookWithZeroPages = mockBook.copy(pageCount = 0, pagesRead = 0)
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(bookWithZeroPages)
        coEvery { repository.updateReadingProgress(1L, any()) } returns Unit
        viewModel = ReaderViewModel(
            getBookByIdUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.nextPage()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(0, state.currentPage) // Should remain 0
        }
        coVerify { repository.updateReadingProgress(1L, 0) }
    }
}

