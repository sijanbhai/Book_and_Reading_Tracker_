package com.sijan.bookandreadingtracker.presentation.viewmodel

import app.cash.turbine.test
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import com.sijan.bookandreadingtracker.domain.usecase.GetCurrentReadsUseCase
import com.sijan.bookandreadingtracker.domain.usecase.GetFinishedBooksUseCase
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
class DashboardViewModelTest {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var getCurrentReadsUseCase: GetCurrentReadsUseCase
    private lateinit var getFinishedBooksUseCase: GetFinishedBooksUseCase
    private lateinit var repository: BookRepository
    private val testDispatcher = StandardTestDispatcher()

    private val mockCurrentReads = listOf(
        BookEntity(
            id = 1L,
            title = "Test Book 1",
            author = "Author 1",
            description = "Description 1",
            coverUrl = "url1",
            publishedDate = "2023",
            rating = 4.5f,
            pageCount = 300,
            isInLibrary = true,
            pagesRead = 150
        ),
        BookEntity(
            id = 2L,
            title = "Test Book 2",
            author = "Author 2",
            description = "Description 2",
            coverUrl = "url2",
            publishedDate = "2024",
            rating = 4.0f,
            pageCount = 400,
            isInLibrary = true,
            pagesRead = 100
        )
    )

    private val mockFinishedBooks = listOf(
        BookEntity(
            id = 3L,
            title = "Finished Book 1",
            author = "Author 3",
            description = "Description 3",
            coverUrl = "url3",
            publishedDate = "2022",
            rating = 5.0f,
            pageCount = 250,
            isInLibrary = true,
            pagesRead = 250
        ),
        BookEntity(
            id = 4L,
            title = "Finished Book 2",
            author = "Author 4",
            description = "Description 4",
            coverUrl = "url4",
            publishedDate = "2021",
            rating = 4.8f,
            pageCount = 350,
            isInLibrary = true,
            pagesRead = 350
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCurrentReadsUseCase = mockk()
        getFinishedBooksUseCase = mockk()
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() = runTest {
        // Given
        coEvery { getCurrentReadsUseCase() } returns flowOf(emptyList())
        coEvery { getFinishedBooksUseCase() } returns flowOf(emptyList())
        coEvery { repository.getFinishedBooksCount() } returns flowOf(0)
        coEvery { repository.getTotalPagesRead() } returns flowOf(0)

        // When
        viewModel = DashboardViewModel(
            getCurrentReadsUseCase,
            getFinishedBooksUseCase,
            repository
        )

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isLoading)

            advanceUntilIdle()
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals(0, finalState.currentReads.size)
            assertEquals(0, finalState.recentFinishedBooks.size)
            assertEquals(0, finalState.finishedBooksCount)
            assertEquals(0, finalState.totalPagesRead)
        }
    }

    @Test
    fun `loadDashboardData should successfully load all data`() = runTest {
        // Given
        coEvery { getCurrentReadsUseCase() } returns flowOf(mockCurrentReads)
        coEvery { getFinishedBooksUseCase() } returns flowOf(mockFinishedBooks)
        coEvery { repository.getFinishedBooksCount() } returns flowOf(2)
        coEvery { repository.getTotalPagesRead() } returns flowOf(1000)

        // When
        viewModel = DashboardViewModel(
            getCurrentReadsUseCase,
            getFinishedBooksUseCase,
            repository
        )

        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.currentReads.size)
            assertEquals(2, state.recentFinishedBooks.size)
            assertEquals(2, state.finishedBooksCount)
            assertEquals(1000, state.totalPagesRead)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `loadDashboardData should limit recent finished books to 5`() = runTest {
        // Given
        val manyFinishedBooks = (1..10).map { index ->
            BookEntity(
                id = index.toLong(),
                title = "Book $index",
                author = "Author $index",
                description = "Description $index",
                coverUrl = "url$index",
                publishedDate = "2023",
                rating = 4.0f,
                pageCount = 300,
                isInLibrary = true,
                pagesRead = 300
            )
        }

        coEvery { getCurrentReadsUseCase() } returns flowOf(emptyList())
        coEvery { getFinishedBooksUseCase() } returns flowOf(manyFinishedBooks)
        coEvery { repository.getFinishedBooksCount() } returns flowOf(10)
        coEvery { repository.getTotalPagesRead() } returns flowOf(3000)

        // When
        viewModel = DashboardViewModel(
            getCurrentReadsUseCase,
            getFinishedBooksUseCase,
            repository
        )

        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(5, state.recentFinishedBooks.size)
            assertEquals(10, state.finishedBooksCount)
        }
    }

    @Test
    fun `refreshData should reload all dashboard data`() = runTest {
        // Given
        coEvery { getCurrentReadsUseCase() } returns flowOf(emptyList()) andThen flowOf(mockCurrentReads)
        coEvery { getFinishedBooksUseCase() } returns flowOf(emptyList()) andThen flowOf(mockFinishedBooks)
        coEvery { repository.getFinishedBooksCount() } returns flowOf(0) andThen flowOf(2)
        coEvery { repository.getTotalPagesRead() } returns flowOf(0) andThen flowOf(1000)

        viewModel = DashboardViewModel(
            getCurrentReadsUseCase,
            getFinishedBooksUseCase,
            repository
        )

        advanceUntilIdle()

        // When
        viewModel.refreshData()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.currentReads.size)
            assertEquals(2, state.recentFinishedBooks.size)
            assertEquals(2, state.finishedBooksCount)
            assertEquals(1000, state.totalPagesRead)
        }
    }

    @Test
    fun `state should show loading when data is being fetched`() = runTest {
        // Given
        coEvery { getCurrentReadsUseCase() } returns flowOf(mockCurrentReads)
        coEvery { getFinishedBooksUseCase() } returns flowOf(mockFinishedBooks)
        coEvery { repository.getFinishedBooksCount() } returns flowOf(2)
        coEvery { repository.getTotalPagesRead() } returns flowOf(1000)

        // When
        viewModel = DashboardViewModel(
            getCurrentReadsUseCase,
            getFinishedBooksUseCase,
            repository
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
    fun `dashboard should calculate total pages read correctly`() = runTest {
        // Given
        val totalPages = 1500
        coEvery { getCurrentReadsUseCase() } returns flowOf(mockCurrentReads)
        coEvery { getFinishedBooksUseCase() } returns flowOf(mockFinishedBooks)
        coEvery { repository.getFinishedBooksCount() } returns flowOf(2)
        coEvery { repository.getTotalPagesRead() } returns flowOf(totalPages)

        // When
        viewModel = DashboardViewModel(
            getCurrentReadsUseCase,
            getFinishedBooksUseCase,
            repository
        )

        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(totalPages, state.totalPagesRead)
        }
    }

    @Test
    fun `empty current reads should be handled correctly`() = runTest {
        // Given
        coEvery { getCurrentReadsUseCase() } returns flowOf(emptyList())
        coEvery { getFinishedBooksUseCase() } returns flowOf(mockFinishedBooks)
        coEvery { repository.getFinishedBooksCount() } returns flowOf(2)
        coEvery { repository.getTotalPagesRead() } returns flowOf(600)

        // When
        viewModel = DashboardViewModel(
            getCurrentReadsUseCase,
            getFinishedBooksUseCase,
            repository
        )

        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.currentReads.isEmpty())
            assertEquals(2, state.recentFinishedBooks.size)
        }
    }

    @Test
    fun `empty finished books should be handled correctly`() = runTest {
        // Given
        coEvery { getCurrentReadsUseCase() } returns flowOf(mockCurrentReads)
        coEvery { getFinishedBooksUseCase() } returns flowOf(emptyList())
        coEvery { repository.getFinishedBooksCount() } returns flowOf(0)
        coEvery { repository.getTotalPagesRead() } returns flowOf(250)

        // When
        viewModel = DashboardViewModel(
            getCurrentReadsUseCase,
            getFinishedBooksUseCase,
            repository
        )

        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.currentReads.size)
            assertTrue(state.recentFinishedBooks.isEmpty())
            assertEquals(0, state.finishedBooksCount)
        }
    }
}

