package com.sijan.bookandreadingtracker.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sijan.bookandreadingtracker.presentation.viewmodel.ReaderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(state.book?.title ?: "Reader")
                        if (state.book != null) {
                            Text(
                                "Page ${state.currentPage} of ${state.book?.pageCount}",
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
                    totalPages = state.book?.pageCount ?: 0,
                    onPreviousPage = { viewModel.previousPage() },
                    onNextPage = { viewModel.nextPage() }
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
                // PDF Reader Placeholder
                // In a real app, you would integrate a PDF rendering library here
                // such as AndroidPdfViewer or PdfRenderer
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
                        "Currently reading: ${state.book?.title}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Page ${state.currentPage} of ${state.book?.pageCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "📖 PDF Reader Feature",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "In a production app, this screen would display the actual PDF content using libraries like:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Android PdfRenderer", style = MaterialTheme.typography.bodySmall)
                            Text("• AndroidPdfViewer library", style = MaterialTheme.typography.bodySmall)
                            Text("• PSPDFKit", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Your reading progress is being tracked automatically!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {
                Text(state.error ?: "Book not found")
            }
        }
    }
}

@Composable
fun ReaderBottomBar(
    currentPage: Int,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Surface(
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousPage,
                enabled = currentPage > 0
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Page")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Page $currentPage / $totalPages",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                val progress = if (totalPages > 0) currentPage.toFloat() / totalPages else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.width(200.dp).padding(top = 4.dp)
                )
            }

            IconButton(
                onClick = onNextPage,
                enabled = currentPage < totalPages
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Page")
            }
        }
    }
}

