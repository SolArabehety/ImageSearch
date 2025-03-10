package com.solarabehety.core.networking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImagesResponse(
    @SerialName("photos")
    val photos: List<Photo>?
)


@Serializable
data class Photo(
    val src: Source,
)

@Serializable
data class Source(
    val original: String,
)