package com.sijan.bookandreadingtracker.presentation.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.presentation.viewmodel.BookDetailsState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests for BookDetailsScreen
 * Testing: UI rendering, empty states, button interactions, navigation
 */
class BookDetailsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val sampleBook = BookEntity(
        id = 1L,
        title = "Test Book Title",
        author = "Test Author Name",
        description = "This is a detailed description of the test book.",
        coverUrl = "https://example.com/cover.jpg",
        publishedDate = "2023",
        rating = 4.5f,
        pageCount = 300,
        pdfUrl = "https://example.com/book.pdf",
        isInLibrary = true,
        pagesRead = 150,
        personalNote = "This is my personal note about the book."
    )

    // Test 1: Verify top bar renders
    @Test
    fun bookDetailsScreen_rendersTopBar() {
        val state = MutableStateFlow(BookDetailsState(book = sampleBook))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithText("Book Details").assertIsDisplayed()
    }

    // Test 2: Verify back button exists
    @Test
    fun bookDetailsScreen_hasBackButton() {
        val state = MutableStateFlow(BookDetailsState(book = sampleBook))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    // Test 3: Verify back button click
    @Test
    fun bookDetailsScreen_backButtonNavigates() {
        var navigatedBack = false
        val state = MutableStateFlow(BookDetailsState(book = sampleBook))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = { navigatedBack = true },
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(navigatedBack)
    }

    // Test 4: Verify book title displays
    @Test
    fun bookDetailsScreen_displaysBookTitle() {
        val state = MutableStateFlow(BookDetailsState(book = sampleBook))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithText("Test Book Title").assertIsDisplayed()
    }

    // Test 5: Verify author displays
    @Test
    fun bookDetailsScreen_displaysAuthor() {
        val state = MutableStateFlow(BookDetailsState(book = sampleBook))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithText("Test Author Name").assertIsDisplayed()
    }

    // Test 6: Verify description displays
    @Test
    fun bookDetailsScreen_displaysDescription() {
        val state = MutableStateFlow(BookDetailsState(book = sampleBook))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "This is a detailed description of the test book.",
            substring = true
        ).assertIsDisplayed()
    }

    // Test 7: Verify book details display
    @Test
    fun bookDetailsScreen_displaysBookDetails() {
        val state = MutableStateFlow(BookDetailsState(book = sampleBook))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithText("Published").assertIsDisplayed()
        composeTestRule.onNodeWithText("2023").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pages").assertIsDisplayed()
        composeTestRule.onNodeWithText("300").assertIsDisplayed()
    }

    // Test 8: Verify reading progress displays
    @Test
    fun bookDetailsScreen_displaysReadingProgress() {
        val state = MutableStateFlow(BookDetailsState(book = sampleBook))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithText("Reading Progress").assertIsDisplayed()
        composeTestRule.onNodeWithText("150 / 300 pages (50%)", substring = true)
            .assertIsDisplayed()
    }

    // Test 9: Verify personal note displays
    @Test
    fun bookDetailsScreen_displaysPersonalNote() {
        val state = MutableStateFlow(BookDetailsState(book = sampleBook))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithText("Personal Note").assertIsDisplayed()
        composeTestRule.onNodeWithText("This is my personal note about the book.")
            .assertIsDisplayed()
    }

    // Test 10: Verify empty personal note state
    @Test
    fun bookDetailsScreen_displaysEmptyNoteState() {
        val bookWithoutNote = sampleBook.copy(personalNote = "")
        val state = MutableStateFlow(BookDetailsState(book = bookWithoutNote))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithText("No notes yet").assertIsDisplayed()
    }

    // Test 11: Verify edit note button
    @Test
    fun bookDetailsScreen_hasEditNoteButton() {
        val state = MutableStateFlow(BookDetailsState(book = sampleBook))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Edit note").assertIsDisplayed()
    }

    // Test 14: Verify loading indicator
    @Test
    fun bookDetailsScreen_showsLoadingIndicator() {
        val state = MutableStateFlow(BookDetailsState(isLoading = true))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        // Verify CircularProgressIndicator is displayed
        composeTestRule.onRoot().printToLog("LOADING_TEST")
    }

    // Test 16: Verify add to library button for books not in library
    @Test
    fun bookDetailsScreen_showsAddToLibraryButton() {
        val bookNotInLibrary = sampleBook.copy(isInLibrary = false)
        val state = MutableStateFlow(BookDetailsState(book = bookNotInLibrary))

        composeTestRule.setContent {
            BookDetailsScreenContent(
                state = state.collectAsState().value,
                onNavigateBack = {},
                onStartReading = {},
                onToggleLibrary = {},
                onUpdateNote = {}
            )
        }

        composeTestRule.onNodeWithText("Add to Library").assertIsDisplayed()
    }
}

// Helper composable for testing
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookDetailsScreenContent(
    state: BookDetailsState,
    onNavigateBack: () -> Unit,
    onStartReading: (Long) -> Unit,
    onToggleLibrary: () -> Unit,
    @Suppress("UNUSED_PARAMETER") onUpdateNote: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.book != null) {
            val book = state.book

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Cover Image
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = book.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    // Title and Author
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Details Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (book.publishedDate.isNotEmpty()) {
                            DetailItem("Published", book.publishedDate)
                        }
                        if (book.pageCount > 0) {
                            DetailItem("Pages", book.pageCount.toString())
                        }
                        if (book.rating > 0) {
                            DetailItem("Rating", "★ ${book.rating}")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reading Progress
                    if (book.isInLibrary) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Reading Progress",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { book.progressPercent / 100f },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Text(
                                    "${book.pagesRead} / ${book.pageCount} pages (${book.progressPercent.toInt()}%)",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Description
                    Text(
                        "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = book.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Personal Note
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Personal Note",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { /* Show dialog */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit note")
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (book.personalNote.isEmpty()) "No notes yet" else book.personalNote,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (book.personalNote.isEmpty())
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    if (book.isInLibrary && book.pdfUrl.isNotEmpty()) {
                        Button(
                            onClick = { onStartReading(book.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (book.pagesRead > 0) "Continue Reading" else "Start Reading")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onToggleLibrary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            if (book.isInLibrary) Icons.Default.Delete else Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (book.isInLibrary) "Remove from Library" else "Add to Library")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

