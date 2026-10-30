package io.koduaxodua.geoswipe.decoder

data class KeySpec(
    val char: Char,
    val x: Float,
    val y: Float,
    val requiresLongPress: Boolean = false,
)
