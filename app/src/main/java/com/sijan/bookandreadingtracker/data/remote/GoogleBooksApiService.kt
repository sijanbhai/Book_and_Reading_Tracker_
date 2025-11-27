package com.sijan.bookandreadingtracker.data.remote

import com.sijan.bookandreadingtracker.data.remote.dto.GoogleBooksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("startIndex") startIndex: Int = 0
    ): GoogleBooksResponse
    
    @GET("volumes")
    suspend fun getRecommendedBooks(
        @Query("q") category: String = "fiction",
        @Query("orderBy") orderBy: String = "relevance",
        @Query("maxResults") maxResults: Int = 20
    ): GoogleBooksResponse
    
    companion object {
        const val BASE_URL = "https://www.googleapis.com/books/v1/"
    }
}

