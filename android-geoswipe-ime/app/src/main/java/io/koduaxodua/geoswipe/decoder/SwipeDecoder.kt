package io.koduaxodua.geoswipe.decoder

import kotlin.math.sqrt

class SwipeDecoder(
    private val dictionary: GeorgianWordDictionary = GeorgianWordDictionary(),
    private val longPressThresholdMs: Long = 230L,
) {
    data class Candidate(
        val word: String,
        val score: Double,
    )

    fun decode(points: List<SwipePoint>, topN: Int = 3): List<Candidate> {
        if (points.size < 2) return emptyList()

        return dictionary.words
            .asSequence()
            .filter { candidate -> candidate.all { GeoQwertyLayout.keys.containsKey(it) } }
            .map { word -> Candidate(word, scoreWord(word, points)) }
            .sortedBy { it.score }
            .take(topN)
            .toList()
    }

    private fun scoreWord(word: String, points: List<SwipePoint>): Double {
        if (word.isEmpty()) return Double.MAX_VALUE

        val anchors = word.mapNotNull { GeoQwertyLayout.keys[it] }
        if (anchors.isEmpty()) return Double.MAX_VALUE

        var pathScore = 0.0
        anchors.forEachIndexed { index, keySpec ->
            val sampledPoint = points[(index * (points.size - 1)) / (anchors.size - 1).coerceAtLeast(1)]
            pathScore += distance(sampledPoint, keySpec)
            if (keySpec.requiresLongPress && sampledPoint.holdMillis < longPressThresholdMs) {
                pathScore += 1.5
            }
        }

        val continuityPenalty = if (word.length > points.size + 2) 1.0 else 0.0
        return pathScore + continuityPenalty
    }

    private fun distance(point: SwipePoint, key: KeySpec): Double {
        val dx = (point.x - key.x).toDouble()
        val dy = (point.y - key.y).toDouble()
        return sqrt(dx * dx + dy * dy)
    }
}
