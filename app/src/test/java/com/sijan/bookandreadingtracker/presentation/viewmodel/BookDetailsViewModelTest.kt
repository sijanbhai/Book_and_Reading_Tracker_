package com.sijan.bookandreadingtracker.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import com.sijan.bookandreadingtracker.domain.usecase.GetBookByIdUseCase
import com.sijan.bookandreadingtracker.domain.usecase.UpdatePersonalNoteUseCase
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
class BookDetailsViewModelTest {

    private lateinit var viewModel: BookDetailsViewModel
    private lateinit var getBookByIdUseCase: GetBookByIdUseCase
    private lateinit var updatePersonalNoteUseCase: UpdatePersonalNoteUseCase
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
        personalNote = "Great book!"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getBookByIdUseCase = mockk()
        updatePersonalNoteUseCase = mockk()
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
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
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
            assertEquals("Book not found", finalState.error)
        }
    }

    @Test
    fun `loadBookDetails should successfully load book`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)

        // When
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.book)
            assertEquals("Test Book", state.book?.title)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `loadBookDetails should handle book not found`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(null)

        // When
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.book)
            assertEquals("Book not found", state.error)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `updatePersonalNote should call use case with correct parameters`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)
        coEvery { updatePersonalNoteUseCase(1L, any()) } returns Unit
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        val newNote = "Updated note"

        // When
        viewModel.updatePersonalNote(newNote)
        advanceUntilIdle()

        // Then
        coVerify { updatePersonalNoteUseCase(1L, newNote) }
    }

    @Test
    fun `toggleLibraryStatus should add to library when not in library`() = runTest {
        // Given
        val bookNotInLibrary = mockBook.copy(isInLibrary = false)
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(bookNotInLibrary)
        coEvery { repository.addToLibrary(1L) } returns Unit
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.toggleLibraryStatus()
        advanceUntilIdle()

        // Then
        coVerify { repository.addToLibrary(1L) }
        coVerify(exactly = 0) { repository.removeFromLibrary(any()) }
    }

    @Test
    fun `toggleLibraryStatus should remove from library when in library`() = runTest {
        // Given
        val bookInLibrary = mockBook.copy(isInLibrary = true)
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(bookInLibrary)
        coEvery { repository.removeFromLibrary(1L) } returns Unit
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.toggleLibraryStatus()
        advanceUntilIdle()

        // Then
        coVerify { repository.removeFromLibrary(1L) }
        coVerify(exactly = 0) { repository.addToLibrary(any()) }
    }

    @Test
    fun `toggleLibraryStatus should do nothing when book is null`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(null)
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.toggleLibraryStatus()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { repository.addToLibrary(any()) }
        coVerify(exactly = 0) { repository.removeFromLibrary(any()) }
    }

    @Test
    fun `updateReadingProgress should call repository with correct parameters`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)
        coEvery { repository.updateReadingProgress(1L, any()) } returns Unit
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        val pagesRead = 200

        // When
        viewModel.updateReadingProgress(pagesRead)
        advanceUntilIdle()

        // Then
        coVerify { repository.updateReadingProgress(1L, pagesRead) }
    }

    @Test
    fun `state should show loading when data is being fetched`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)

        // When
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
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
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
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
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
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
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        coVerify { getBookByIdUseCase.observe(0L) }
    }

    @Test
    fun `book entity properties should be correctly mapped`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)

        // When
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            val book = state.book
            assertNotNull(book)
            assertEquals(1L, book?.id)
            assertEquals("Test Book", book?.title)
            assertEquals("Test Author", book?.author)
            assertEquals("Test Description", book?.description)
            assertEquals(4.5f, book?.rating)
            assertEquals(300, book?.pageCount)
            assertEquals(150, book?.pagesRead)
            assertEquals("Great book!", book?.personalNote)
        }
    }

    @Test
    fun `updateReadingProgress with 0 pages should work`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)
        coEvery { repository.updateReadingProgress(1L, 0) } returns Unit
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.updateReadingProgress(0)
        advanceUntilIdle()

        // Then
        coVerify { repository.updateReadingProgress(1L, 0) }
    }

    @Test
    fun `updatePersonalNote with empty string should work`() = runTest {
        // Given
        coEvery { getBookByIdUseCase.observe(1L) } returns flowOf(mockBook)
        coEvery { updatePersonalNoteUseCase(1L, "") } returns Unit
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            updatePersonalNoteUseCase,
            repository,
            savedStateHandle
        )
        advanceUntilIdle()

        // When
        viewModel.updatePersonalNote("")
        advanceUntilIdle()

        // Then
        coVerify { updatePersonalNoteUseCase(1L, "") }
    }
}

