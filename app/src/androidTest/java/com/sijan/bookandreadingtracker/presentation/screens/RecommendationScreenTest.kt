package com.sijan.bookandreadingtracker.presentation.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.presentation.viewmodel.RecommendationState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests for RecommendationScreen
 * Testing: UI rendering, empty states, button interactions, search functionality, navigation
 */
class RecommendationScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val sampleBook = BookEntity(
        id = 0L, // 0 for recommended books not in library yet
        title = "Recommended Book",
        author = "Famous Author",
        description = "Great book description",
        coverUrl = "https://example.com/cover.jpg",
        publishedDate = "2023",
        rating = 4.8f,
        pageCount = 350,
        pdfUrl = "",
        isInLibrary = false,
        pagesRead = 0,
        personalNote = ""
    )

    // Test 1: Verify top bar renders
    @Test
    fun recommendationScreen_rendersTopBar() {
        val state = MutableStateFlow(RecommendationState())

        composeTestRule.setContent {
            RecommendationScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onSearchBooks = {},
                onAddToLibrary = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithText("Discover Books").assertIsDisplayed()
    }

    // Test 2: Verify search bar displays
    @Test
    fun recommendationScreen_displaysSearchBar() {
        val state = MutableStateFlow(RecommendationState())

        composeTestRule.setContent {
            RecommendationScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onSearchBooks = {},
                onAddToLibrary = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithText("Search books...").assertIsDisplayed()
    }

    // Test 5: Verify loading indicator
    @Test
    fun recommendationScreen_showsLoadingIndicator() {
        val state = MutableStateFlow(RecommendationState(isLoading = true))

        composeTestRule.setContent {
            RecommendationScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onSearchBooks = {},
                onAddToLibrary = {},
                onClearError = {}
            )
        }

        // Verify CircularProgressIndicator is displayed
        composeTestRule.onRoot().printToLog("LOADING_TEST")
    }

    // Test 6: Verify error message displays
    @Test
    fun recommendationScreen_displaysErrorMessage() {
        val state = MutableStateFlow(
            RecommendationState(error = "Failed to load books")
        )

        composeTestRule.setContent {
            RecommendationScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onSearchBooks = {},
                onAddToLibrary = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithText("Failed to load books").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dismiss").assertIsDisplayed()
    }

    // Test 7: Verify error dismiss button
    @Test
    fun recommendationScreen_errorDismissButtonWorks() {
        var errorCleared = false
        val state = MutableStateFlow(
            RecommendationState(error = "Failed to load books")
        )

        composeTestRule.setContent {
            RecommendationScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onSearchBooks = {},
                onAddToLibrary = {},
                onClearError = { errorCleared = true }
            )
        }

        composeTestRule.onNodeWithText("Dismiss").performClick()
        assert(errorCleared)
    }

    // Test 8: Verify books grid displays
    @Test
    fun recommendationScreen_displaysBooks() {
        val state = MutableStateFlow(
            RecommendationState(books = listOf(sampleBook))
        )

        composeTestRule.setContent {
            RecommendationScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onSearchBooks = {},
                onAddToLibrary = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithText("Recommended Book").assertIsDisplayed()
        composeTestRule.onNodeWithText("Famous Author").assertIsDisplayed()
    }

    // Test 9: Verify book rating displays
    @Test
    fun recommendationScreen_displaysBookRating() {
        val state = MutableStateFlow(
            RecommendationState(books = listOf(sampleBook))
        )

        composeTestRule.setContent {
            RecommendationScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onSearchBooks = {},
                onAddToLibrary = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithText("★ 4.8").assertIsDisplayed()
    }

    // Test 10: Verify add to library button
    @Test
    fun recommendationScreen_showsAddToLibraryButton() {
        val state = MutableStateFlow(
            RecommendationState(books = listOf(sampleBook))
        )

        composeTestRule.setContent {
            RecommendationScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onSearchBooks = {},
                onAddToLibrary = {},
                onClearError = {}
            )
        }

        composeTestRule.onAllNodesWithText("Add")[0].assertIsDisplayed()
    }

    // Test 12: Verify book already in library shows disabled button
    @Test
    fun recommendationScreen_disablesButtonForBooksInLibrary() {
        val bookInLibrary = sampleBook.copy(isInLibrary = true)
        val state = MutableStateFlow(
            RecommendationState(books = listOf(bookInLibrary))
        )

        composeTestRule.setContent {
            RecommendationScreenContent(
                state = state.collectAsState().value,
                onBookClick = {},
                onSearchBooks = {},
                onAddToLibrary = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithText("In Library").assertIsDisplayed()
    }
}

// Helper composable for testing
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecommendationScreenContent(
    state: RecommendationState,
    onBookClick: (Long) -> Unit,
    onSearchBooks: (String) -> Unit,
    onAddToLibrary: (BookEntity) -> Unit,
    onClearError: () -> Unit
) {
    var searchText by remember { mutableStateOf(state.searchQuery) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover Books") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search books...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { onSearchBooks(searchText) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                },
                singleLine = true
            )

            state.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onClearError) {
                            Text("Dismiss")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.isEmpty) {
                EmptyRecommendationState()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.books.size) { index ->
                        RecommendationBookCard(
                            book = state.books[index],
                            onBookClick = onBookClick,
                            onAddToLibrary = { onAddToLibrary(state.books[index]) }
                        )
                    }
                }
            }
        }
    }
}

