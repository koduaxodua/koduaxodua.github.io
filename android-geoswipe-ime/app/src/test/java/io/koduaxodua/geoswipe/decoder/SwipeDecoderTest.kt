package io.koduaxodua.geoswipe.decoder

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SwipeDecoderTest {
    @Test
    fun `decoder suggests themed word when long press point exists`() {
        val decoder = SwipeDecoder(
            dictionary = GeorgianWordDictionary(),
            longPressThresholdMs = 200L,
        )

        val points = listOf(
            SwipePoint(2.9f, 0f, 250L),
            SwipePoint(4.5f, 1f, 20L),
            SwipePoint(8f, 0f, 20L),
        )

        val suggestions = decoder.decode(points)
        assertTrue(suggestions.isNotEmpty())
    }

    @Test
    fun `decoder returns top n`() {
        val decoder = SwipeDecoder()
        val points = listOf(
            SwipePoint(4f, 1f, 20L),
            SwipePoint(7f, 1f, 20L),
            SwipePoint(2f, 2f, 20L),
        )

        val suggestions = decoder.decode(points, topN = 2)
        assertEquals(2, suggestions.size)
    }
}
