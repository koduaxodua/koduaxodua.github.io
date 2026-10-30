package io.koduaxodua.geoswipe.decoder

object GeoQwertyLayout {
    private const val STEP = 1.0f

    val keys: Map<Char, KeySpec> = buildMap {
        val row1 = "ქწერტყუიოპ"
        val row2 = "ასდფგჰჯკლ"
        val row3 = "ზხცვბნმ"

        row1.forEachIndexed { i, c -> put(c, KeySpec(c, i * STEP, 0f)) }
        row2.forEachIndexed { i, c -> put(c, KeySpec(c, i * STEP + 0.5f, 1f)) }
        row3.forEachIndexed { i, c -> put(c, KeySpec(c, i * STEP + 1.2f, 2f)) }

        put('თ', KeySpec('თ', keysValue('ტ').x, keysValue('ტ').y, requiresLongPress = true))
        put('ძ', KeySpec('ძ', keysValue('წ').x, keysValue('წ').y, requiresLongPress = true))
        put('ჭ', KeySpec('ჭ', keysValue('ჩ').x, keysValue('ჩ').y, requiresLongPress = true))
        put('ჟ', KeySpec('ჟ', keysValue('ჯ').x, keysValue('ჯ').y, requiresLongPress = true))
        put('ღ', KeySpec('ღ', keysValue('გ').x, keysValue('გ').y, requiresLongPress = true))
    }

    private fun keysValue(char: Char): KeySpec {
        return when (char) {
            'ტ' -> KeySpec('ტ', 3f, 0f)
            'წ' -> KeySpec('წ', 1f, 0f)
            'ჩ' -> KeySpec('ჩ', 2.2f, 2f)
            'ჯ' -> KeySpec('ჯ', 7.5f, 1f)
            'გ' -> KeySpec('გ', 4.5f, 1f)
            else -> error("Unknown base key")
        }
    }
}
