package io.koduaxodua.geoswipe.decoder

import kotlin.math.hypot

object GeoQwertyLayout {
    private const val STEP = 1.0f

    private val longPressBaseByChar = mapOf(
        'თ' to 'ტ',
        'ძ' to 'წ',
        'ჭ' to 'ჩ',
        'ჟ' to 'ჯ',
        'ღ' to 'გ',
    )

    private val longPressCharByBase = longPressBaseByChar.entries.associate { (k, v) -> v to k }

    private val baseKeys: Map<Char, KeySpec> = buildMap {
        val row1 = "ქწერტყუიოპ"
        val row2 = "ასდფგჰჯკლ"
        val row3 = "ზხცვბნმ"

        row1.forEachIndexed { i, c -> put(c, KeySpec(c, i * STEP, 0f)) }
        row2.forEachIndexed { i, c -> put(c, KeySpec(c, i * STEP + 0.5f, 1f)) }
        row3.forEachIndexed { i, c -> put(c, KeySpec(c, i * STEP + 1.2f, 2f)) }
    }

    val keys: Map<Char, KeySpec> = buildMap {
        putAll(baseKeys)
        longPressBaseByChar.forEach { (char, baseChar) ->
            baseKeys[baseChar]?.let { base ->
                put(char, KeySpec(char, base.x, base.y, requiresLongPress = true))
            }
        }
    }

    fun toBaseChar(char: Char): Char = longPressBaseByChar[char] ?: char

    fun longPressAlternative(baseChar: Char): Char? = longPressCharByBase[baseChar]

    fun nearestBaseKey(x: Float, y: Float): KeySpec? {
        return baseKeys.values.minByOrNull { key ->
            hypot((x - key.x).toDouble(), (y - key.y).toDouble())
        }
    }
}
