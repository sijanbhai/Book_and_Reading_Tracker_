package com.sijan.bookandreadingtracker.domain.model

data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String,
    val publishedDate: String,
    val rating: Float,
    val pageCount: Int,
    val pdfUrl: String,
    val isInLibrary: Boolean,
    val pagesRead: Int,
    val personalNote: String
) {
    val progressPercent: Float
        get() = if (pageCount > 0) {
            (pagesRead.toFloat() / pageCount.toFloat()) * 100f
        } else 0f

    val isCurrentlyReading: Boolean
        get() = isInLibrary && pagesRead > 0 && pagesRead < pageCount

    val isFinished: Boolean
        get() = isInLibrary && pagesRead >= pageCount && pageCount > 0
}

