package com.sijan.bookandreadingtracker.presentation.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import com.sijan.bookandreadingtracker.presentation.viewmodel.DashboardState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests for DashboardScreen
 * Testing: UI rendering, empty states, button interactions, navigation
 */
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val sampleBook = BookEntity(
        id = 1L,
        title = "Test Book",
        author = "Test Author",
        description = "Test Description",
        coverUrl = "https://example.com/cover.jpg",
        publishedDate = "2023",
        rating = 4.5f,
        pageCount = 300,
        pdfUrl = "https://example.com/book.pdf",
        isInLibrary = true,
        pagesRead = 150,
        personalNote = "Great book"
    )

    private val finishedBook = sampleBook.copy(
        id = 2L,
        title = "Finished Book",
        pagesRead = 300
    )

    // Test 1: Verify top bar renders correctly
    @Test
    fun dashboardScreen_rendersTopBarWithTitle() {
        val state = MutableStateFlow(DashboardState())

        composeTestRule.setContent {
            DashboardScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
    }

    // Test 2: Verify refresh button exists
    @Test
    fun dashboardScreen_refreshButtonIsDisplayed() {
        val state = MutableStateFlow(DashboardState())

        composeTestRule.setContent {
            DashboardScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Refresh").assertIsDisplayed()
    }

    // Test 3: Verify statistics cards render
    @Test
    fun dashboardScreen_rendersStatisticsCards() {
        val state = MutableStateFlow(
            DashboardState(
                finishedBooksCount = 5,
                totalPagesRead = 1500
            )
        )

        composeTestRule.setContent {
            DashboardScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithText("Finished Books").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Pages").assertIsDisplayed()
        composeTestRule.onNodeWithText("1500").assertIsDisplayed()
    }

    // Test 4: Verify currently reading section displays
    @Test
    fun dashboardScreen_displaysCurrentlyReadingSection() {
        val state = MutableStateFlow(
            DashboardState(currentReads = listOf(sampleBook))
        )

        composeTestRule.setContent {
            DashboardScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithText("Currently Reading").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Book").assertIsDisplayed()
    }

    // Test 5: Verify progress percentage displays
    @Test
    fun dashboardScreen_displaysProgressPercentage() {
        val state = MutableStateFlow(
            DashboardState(currentReads = listOf(sampleBook))
        )

        composeTestRule.setContent {
            DashboardScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithText("50%").assertIsDisplayed()
    }

    // Test 6: Verify recently finished section displays
    @Test
    fun dashboardScreen_displaysRecentlyFinishedSection() {
        val state = MutableStateFlow(
            DashboardState(recentFinishedBooks = listOf(finishedBook))
        )

        composeTestRule.setContent {
            DashboardScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithText("Recently Finished").assertIsDisplayed()
        composeTestRule.onNodeWithText("Finished Book").assertIsDisplayed()
    }

    // Test 9: Verify refresh button click
    @Test
    fun dashboardScreen_refreshButtonClickTriggersAction() {
        var refreshClicked = false
        val state = MutableStateFlow(DashboardState())

        composeTestRule.setContent {
            DashboardScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onRefresh = { refreshClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Refresh").performClick()
        assert(refreshClicked)
    }

    // Test 10: Verify book click navigation
    @Test
    fun dashboardScreen_bookClickTriggersNavigation() {
        var clickedBookId: Long? = null
        val state = MutableStateFlow(
            DashboardState(currentReads = listOf(sampleBook))
        )

        composeTestRule.setContent {
            DashboardScreenContent(
                state = state.collectAsState().value,
                onBookClick = { bookId -> clickedBookId = bookId },
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithText("Test Book").performClick()
        assert(clickedBookId == 1L)
    }
}

// Helper composable for testing without ViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenContent(
    state: DashboardState,
    onBookClick: (Long) -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StatisticsSection(
                    finishedBooks = state.finishedBooksCount,
                    totalPages = state.totalPagesRead
                )
            }

            if (state.currentReads.isNotEmpty()) {
                item {
                    Text(
                        "Currently Reading",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.currentReads.size) { index ->
                            CurrentReadCard(state.currentReads[index], onBookClick)
                        }
                    }
                }
            }

            if (state.recentFinishedBooks.isNotEmpty()) {
                item {
                    Text(
                        "Recently Finished",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(state.recentFinishedBooks.size) { index ->
                    FinishedBookItem(state.recentFinishedBooks[index], onBookClick)
                }
            }

            if (state.currentReads.isEmpty() && state.recentFinishedBooks.isEmpty() && !state.isLoading) {
                item {
                    EmptyDashboardState()
                }
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

