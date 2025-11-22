package com.sijan.bookandreadingtracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String,
    val publishedDate: String,
    val rating: Float,
    val pageCount: Int,
    val pdfUrl: String = "",
    val isInLibrary: Boolean = false,
    val pagesRead: Int = 0,
    val personalNote: String = ""
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

