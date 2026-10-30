package io.koduaxodua.geoswipe.decoder

data class SwipePoint(
    val x: Float,
    val y: Float,
    val holdMillis: Long = 0L,
)
