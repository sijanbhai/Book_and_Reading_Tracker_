package com.sijan.bookandreadingtracker.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GoogleBooksResponse(
    @SerializedName("items")
    val items: List<BookItemDto>? = null,
    @SerializedName("totalItems")
    val totalItems: Int = 0
)

data class BookItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfoDto
)

data class VolumeInfoDto(
    @SerializedName("title")
    val title: String?,
    @SerializedName("authors")
    val authors: List<String>?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("imageLinks")
    val imageLinks: ImageLinksDto?,
    @SerializedName("publishedDate")
    val publishedDate: String?,
    @SerializedName("averageRating")
    val averageRating: Float?,
    @SerializedName("pageCount")
    val pageCount: Int?,
    @SerializedName("previewLink")
    val previewLink: String?
)

data class ImageLinksDto(
    @SerializedName("thumbnail")
    val thumbnail: String?,
    @SerializedName("smallThumbnail")
    val smallThumbnail: String?
)

