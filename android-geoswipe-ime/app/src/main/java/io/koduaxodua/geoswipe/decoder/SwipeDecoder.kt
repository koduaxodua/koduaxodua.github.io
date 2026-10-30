package io.koduaxodua.geoswipe.decoder

import kotlin.math.abs
import kotlin.math.hypot

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

        val normalized = normalize(points)
        if (normalized.size < 2) return emptyList()

        val gestureSignature = signatureFromPoints(normalized)

        return dictionary.words
            .asSequence()
            .filter { word -> word.isNotBlank() && word.all { GeoQwertyLayout.keys.containsKey(it) } }
            .map { word -> Candidate(word, scoreWord(word, normalized, gestureSignature)) }
            .sortedBy { it.score }
            .take(topN.coerceAtLeast(1))
            .toList()
    }

    private fun scoreWord(word: String, points: List<SwipePoint>, gestureSignature: String): Double {
        val anchors = word.mapNotNull { GeoQwertyLayout.keys[it] }
        if (anchors.isEmpty()) return Double.MAX_VALUE

        val signatureDistance = levenshteinDistance(gestureSignature, signatureForWord(word)).toDouble()
        val pathScore = anchorPathDistance(anchors, points)
        val endpointPenalty = distance(points.first(), anchors.first()) + distance(points.last(), anchors.last())
        val longPressPenalty = longPressPenalty(word, points)
        val lengthPenalty = abs(word.length - gestureSignature.length) * 0.15

        return (pathScore * 0.55) + (signatureDistance * 1.2) + (endpointPenalty * 0.6) + longPressPenalty + lengthPenalty
    }

    private fun signatureForWord(word: String): String {
        val base = word.map { GeoQwertyLayout.toBaseChar(it) }
        return buildString {
            var prev: Char? = null
            for (char in base) {
                if (char != prev) append(char)
                prev = char
            }
        }
    }

    private fun signatureFromPoints(points: List<SwipePoint>): String {
        val chars = points.mapNotNull { point ->
            GeoQwertyLayout.nearestBaseKey(point.x, point.y)?.char
        }

        return buildString {
            var prev: Char? = null
            for (char in chars) {
                if (char != prev) append(char)
                prev = char
            }
        }
    }

    private fun anchorPathDistance(anchors: List<KeySpec>, points: List<SwipePoint>): Double {
        if (anchors.size == 1) return distance(points.first(), anchors.first())

        var total = 0.0
        anchors.forEachIndexed { index, key ->
            val sampled = points[(index * (points.size - 1)) / (anchors.size - 1)]
            total += distance(sampled, key)
        }
        return total / anchors.size
    }

    private fun longPressPenalty(word: String, points: List<SwipePoint>): Double {
        var penalty = 0.0
        word.forEach { char ->
            val key = GeoQwertyLayout.keys[char] ?: return@forEach
            if (key.requiresLongPress) {
                val hasHold = points.any { point ->
                    point.holdMillis >= longPressThresholdMs && distance(point, key) < 0.9
                }
                if (!hasHold) {
                    penalty += 1.6
                }
            }
        }
        return penalty
    }

    private fun normalize(points: List<SwipePoint>): List<SwipePoint> {
        if (points.isEmpty()) return emptyList()

        val normalized = ArrayList<SwipePoint>(points.size)
        normalized += points.first()

        for (index in 1 until points.size) {
            val prev = normalized.last()
            val current = points[index]
            val movement = hypot((current.x - prev.x).toDouble(), (current.y - prev.y).toDouble())
            val holdJump = current.holdMillis >= longPressThresholdMs && prev.holdMillis < longPressThresholdMs
            if (movement >= 0.09 || holdJump || index == points.lastIndex) {
                normalized += current
            }
        }

        return normalized
    }

    private fun levenshteinDistance(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        val prev = IntArray(b.length + 1) { it }
        val curr = IntArray(b.length + 1)

        for (i in a.indices) {
            curr[0] = i + 1
            for (j in b.indices) {
                val cost = if (a[i] == b[j]) 0 else 1
                curr[j + 1] = minOf(
                    curr[j] + 1,
                    prev[j + 1] + 1,
                    prev[j] + cost,
                )
            }
            for (k in prev.indices) {
                prev[k] = curr[k]
            }
        }

        return prev[b.length]
    }

    private fun distance(point: SwipePoint, key: KeySpec): Double {
        return hypot((point.x - key.x).toDouble(), (point.y - key.y).toDouble())
    }
}
