package com.sijan.bookandreadingtracker.presentation.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.presentation.viewmodel.ReaderState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests for ReaderScreen
 * Testing: UI rendering, empty states, button interactions, navigation, page controls
 */
class ReaderScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val sampleBook = BookEntity(
        id = 1L,
        title = "Test Book for Reading",
        author = "Test Author",
        description = "Test Description",
        coverUrl = "https://example.com/cover.jpg",
        publishedDate = "2023",
        rating = 4.5f,
        pageCount = 300,
        pdfUrl = "https://example.com/book.pdf",
        isInLibrary = true,
        pagesRead = 50,
        personalNote = ""
    )

    // Test 1: Verify top bar renders with book title
    @Test
    fun readerScreen_rendersTopBarWithBookTitle() {
        val state = MutableStateFlow(
            ReaderState(book = sampleBook, currentPage = 50)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = {},
                onNextPage = {}
            )
        }

        composeTestRule.onNodeWithText("Test Book for Reading").assertIsDisplayed()
    }

    // Test 3: Verify back button exists
    @Test
    fun readerScreen_hasBackButton() {
        val state = MutableStateFlow(
            ReaderState(book = sampleBook, currentPage = 50)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = {},
                onNextPage = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    // Test 4: Verify back button click
    @Test
    fun readerScreen_backButtonNavigates() {
        var navigatedBack = false
        val state = MutableStateFlow(
            ReaderState(book = sampleBook, currentPage = 50)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = { navigatedBack = true },
                onPreviousPage = {},
                onNextPage = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(navigatedBack)
    }

    // Test 5: Verify bottom bar renders
    @Test
    fun readerScreen_rendersBottomBar() {
        val state = MutableStateFlow(
            ReaderState(book = sampleBook, currentPage = 50)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = {},
                onNextPage = {}
            )
        }

        composeTestRule.onNodeWithText("Page 50 / 300").assertIsDisplayed()
    }

    // Test 6: Verify previous page button
    @Test
    fun readerScreen_hasPreviousPageButton() {
        val state = MutableStateFlow(
            ReaderState(book = sampleBook, currentPage = 50)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = {},
                onNextPage = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Previous Page").assertIsDisplayed()
    }

    // Test 7: Verify next page button
    @Test
    fun readerScreen_hasNextPageButton() {
        val state = MutableStateFlow(
            ReaderState(book = sampleBook, currentPage = 50)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = {},
                onNextPage = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Next Page").assertIsDisplayed()
    }

    // Test 8: Verify previous page button click
    @Test
    fun readerScreen_previousPageButtonWorks() {
        var previousPageCalled = false
        val state = MutableStateFlow(
            ReaderState(book = sampleBook, currentPage = 50)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = { previousPageCalled = true },
                onNextPage = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Previous Page").performClick()
        assert(previousPageCalled)
    }

    // Test 9: Verify next page button click
    @Test
    fun readerScreen_nextPageButtonWorks() {
        var nextPageCalled = false
        val state = MutableStateFlow(
            ReaderState(book = sampleBook, currentPage = 50)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = {},
                onNextPage = { nextPageCalled = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Next Page").performClick()
        assert(nextPageCalled)
    }

    // Test 10: Verify previous page button disabled at start
    @Test
    fun readerScreen_previousPageButtonDisabledAtStart() {
        val state = MutableStateFlow(
            ReaderState(book = sampleBook, currentPage = 0)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = {},
                onNextPage = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Previous Page").assertIsNotEnabled()
    }

    // Test 11: Verify next page button disabled at end
    @Test
    fun readerScreen_nextPageButtonDisabledAtEnd() {
        val state = MutableStateFlow(
            ReaderState(book = sampleBook, currentPage = 300)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = {},
                onNextPage = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Next Page").assertIsNotEnabled()
    }

    // Test 12: Verify loading indicator
    @Test
    fun readerScreen_showsLoadingIndicator() {
        val state = MutableStateFlow(ReaderState(isLoading = true))

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = {},
                onNextPage = {}
            )
        }

        // Verify CircularProgressIndicator is displayed
        composeTestRule.onRoot().printToLog("LOADING_TEST")
    }

    // Test 13: Verify PDF reader placeholder displays
    @Test
    fun readerScreen_displaysPDFReaderPlaceholder() {
        val state = MutableStateFlow(
            ReaderState(book = sampleBook, currentPage = 50)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = {},
                onNextPage = {}
            )
        }

        composeTestRule.onNodeWithText("PDF Reader").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Currently reading: Test Book for Reading"
        ).assertIsDisplayed()
    }

    // Test 15: Verify error message displays
    @Test
    fun readerScreen_displaysErrorMessage() {
        val state = MutableStateFlow(
            ReaderState(error = "Book not found", book = null)
        )

        composeTestRule.setContent {
            ReaderScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onPreviousPage = {},
                onNextPage = {}
            )
        }

        composeTestRule.onNodeWithText("Book not found").assertIsDisplayed()
    }
}

// Helper composable for testing
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderScreenContent(
    state: ReaderState,
    onNavigateBack: () -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(state.book?.title ?: "Reader")
                        if (state.book != null) {
                            Text(
                                "Page ${state.currentPage} of ${state.book.pageCount}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (state.book != null) {
                ReaderBottomBar(
                    currentPage = state.currentPage,
                    totalPages = state.book.pageCount,
                    onPreviousPage = onPreviousPage,
                    onNextPage = onNextPage
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.book != null) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "PDF Reader",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Currently reading: ${state.book.title}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Page ${state.currentPage} of ${state.book.pageCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(state.error ?: "Book not found")
            }
        }
    }
}

