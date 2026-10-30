package io.koduaxodua.geoswipe.decoder

import org.junit.Assert.assertEquals
import org.junit.Test

class SwipeDecoderTest {
    @Test
    fun `decoder prefers long-press word when hold evidence exists`() {
        val decoder = SwipeDecoder(
            dictionary = GeorgianWordDictionary(words = listOf("ტემა", "თემა")),
            longPressThresholdMs = 200L,
        )

        val suggestions = decoder.decode(
            points = pointsForWord(
                word = "თემა",
                holdIndices = setOf(0),
            ),
            topN = 1,
        )

        assertEquals("თემა", suggestions.first().word)
    }

    @Test
    fun `decoder prefers base word when no long-press evidence exists`() {
        val decoder = SwipeDecoder(
            dictionary = GeorgianWordDictionary(words = listOf("ტემა", "თემა")),
            longPressThresholdMs = 220L,
        )

        val suggestions = decoder.decode(
            points = pointsForWord(
                word = "ტემა",
                holdIndices = emptySet(),
            ),
            topN = 1,
        )

        assertEquals("ტემა", suggestions.first().word)
    }

    @Test
    fun `decoder honors top n size`() {
        val decoder = SwipeDecoder(dictionary = GeorgianWordDictionary(words = listOf("ტესტი", "თემა", "დღეს")))

        val suggestions = decoder.decode(
            points = pointsForWord(word = "ტესტი", holdIndices = emptySet()),
            topN = 2,
        )

        assertEquals(2, suggestions.size)
    }

    private fun pointsForWord(word: String, holdIndices: Set<Int>): List<SwipePoint> {
        return word.mapIndexedNotNull { index, char ->
            GeoQwertyLayout.keys[char]?.let { key ->
                SwipePoint(
                    x = key.x,
                    y = key.y,
                    holdMillis = if (index in holdIndices) 280L else 30L,
                )
            }
        }
    }
}
