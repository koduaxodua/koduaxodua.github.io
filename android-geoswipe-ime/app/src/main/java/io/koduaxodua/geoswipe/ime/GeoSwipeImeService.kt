package io.koduaxodua.geoswipe.ime

import android.inputmethodservice.InputMethodService
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import io.koduaxodua.geoswipe.R
import io.koduaxodua.geoswipe.decoder.GeoQwertyLayout
import io.koduaxodua.geoswipe.decoder.SwipeDecoder
import io.koduaxodua.geoswipe.decoder.SwipePoint
import kotlin.math.hypot

class GeoSwipeImeService : InputMethodService(), View.OnTouchListener {
    private val decoder = SwipeDecoder()
    private val activePoints = mutableListOf<SwipePoint>()

    private lateinit var suggestionButtons: List<Button>
    private lateinit var swipeSurface: View

    private var currentSuggestions: List<String> = emptyList()
    private var lastNormalizedX = 0f
    private var lastNormalizedY = 0f
    private var lastEventTime = 0L
    private var stationaryHoldMillis = 0L

    override fun onCreateInputView(): View {
        val view = LayoutInflater.from(this).inflate(R.layout.input_view, null)

        suggestionButtons = listOf(
            view.findViewById(R.id.suggestion1),
            view.findViewById(R.id.suggestion2),
            view.findViewById(R.id.suggestion3),
        )

        suggestionButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                currentSuggestions.getOrNull(index)?.let { suggestion ->
                    commitWord(suggestion)
                }
            }
        }

        view.findViewById<Button>(R.id.keySpace).setOnClickListener {
            currentInputConnection?.commitText(" ", 1)
        }

        view.findViewById<Button>(R.id.keyBackspace).setOnClickListener {
            currentInputConnection?.deleteSurroundingText(1, 0)
        }

        view.findViewById<Button>(R.id.keyEnter).setOnClickListener {
            currentInputConnection?.commitText("\n", 1)
        }

        swipeSurface = view.findViewById(R.id.swipeSurface)
        swipeSurface.setOnTouchListener(this)
        renderSuggestions(emptyList())

        return view
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (v?.id != R.id.swipeSurface) return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                resetGesture()
                addPoint(event.x, event.y, event.eventTime)
                refreshSuggestions(previewOnly = true)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                addPoint(event.x, event.y, event.eventTime)
                refreshSuggestions(previewOnly = true)
                return true
            }

            MotionEvent.ACTION_UP -> {
                addPoint(event.x, event.y, event.eventTime)
                val best = refreshSuggestions(previewOnly = false)
                if (!best.isNullOrBlank()) {
                    commitWord(best)
                } else {
                    commitFallbackLetter()
                }
                resetGesture()
                return true
            }
        }
        return false
    }

    private fun commitWord(word: String) {
        currentInputConnection?.commitText("$word ", 1)
        renderSuggestions(emptyList())
    }

    private fun commitFallbackLetter() {
        val last = activePoints.lastOrNull() ?: return
        val nearest = GeoQwertyLayout.nearestBaseKey(last.x, last.y) ?: return
        val output = if (last.holdMillis >= LONG_PRESS_THRESHOLD_MS) {
            GeoQwertyLayout.longPressAlternative(nearest.char) ?: nearest.char
        } else {
            nearest.char
        }
        currentInputConnection?.commitText(output.toString(), 1)
    }

    private fun refreshSuggestions(previewOnly: Boolean): String? {
        currentSuggestions = decoder.decode(activePoints, topN = suggestionButtons.size).map { it.word }
        renderSuggestions(currentSuggestions)
        return if (previewOnly) null else currentSuggestions.firstOrNull()
    }

    private fun renderSuggestions(words: List<String>) {
        suggestionButtons.forEachIndexed { index, button ->
            val word = words.getOrNull(index)
            button.text = word ?: getString(R.string.placeholder_suggestion)
            button.isEnabled = !word.isNullOrBlank()
        }
    }

    private fun addPoint(rawX: Float, rawY: Float, eventTime: Long) {
        val width = swipeSurface.width.coerceAtLeast(1)
        val height = swipeSurface.height.coerceAtLeast(1)

        val normalizedX = (rawX / width) * 9f
        val normalizedY = (rawY / height) * 2.2f

        if (activePoints.isEmpty()) {
            stationaryHoldMillis = 0L
        } else {
            val movement = hypot(
                (normalizedX - lastNormalizedX).toDouble(),
                (normalizedY - lastNormalizedY).toDouble(),
            )
            val delta = (eventTime - lastEventTime).coerceAtLeast(0L)
            stationaryHoldMillis = if (movement < 0.14) {
                stationaryHoldMillis + delta
            } else {
                0L
            }
        }

        activePoints.add(
            SwipePoint(
                x = normalizedX,
                y = normalizedY,
                holdMillis = stationaryHoldMillis,
            ),
        )

        lastNormalizedX = normalizedX
        lastNormalizedY = normalizedY
        lastEventTime = eventTime
    }

    private fun resetGesture() {
        activePoints.clear()
        lastNormalizedX = 0f
        lastNormalizedY = 0f
        lastEventTime = 0L
        stationaryHoldMillis = 0L
    }

    private companion object {
        private const val LONG_PRESS_THRESHOLD_MS = 230L
    }
}
