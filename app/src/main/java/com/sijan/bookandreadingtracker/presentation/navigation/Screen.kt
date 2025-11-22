package com.sijan.bookandreadingtracker.presentation.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Library : Screen("library")
    object Recommendation : Screen("recommendation")
    object BookDetails : Screen("details/{bookId}") {
        fun createRoute(bookId: Long) = "details/$bookId"
    }
    object Reader : Screen("reader/{bookId}") {
        fun createRoute(bookId: Long) = "reader/$bookId"
    }
}

