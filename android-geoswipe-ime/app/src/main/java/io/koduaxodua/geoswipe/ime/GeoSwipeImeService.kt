package io.koduaxodua.geoswipe.ime

import android.inputmethodservice.InputMethodService
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import io.koduaxodua.geoswipe.R
import io.koduaxodua.geoswipe.decoder.SwipeDecoder
import io.koduaxodua.geoswipe.decoder.SwipePoint

class GeoSwipeImeService : InputMethodService(), View.OnTouchListener {
    private val decoder = SwipeDecoder()
    private val activePoints = mutableListOf<SwipePoint>()
    private lateinit var suggestionText: TextView

    override fun onCreateInputView(): View {
        val view = LayoutInflater.from(this).inflate(R.layout.input_view, null)
        suggestionText = view.findViewById(R.id.suggestionText)
        view.setOnTouchListener(this)
        return view
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                activePoints.clear()
                activePoints.add(SwipePoint(event.x / 120f, event.y / 120f, 0L))
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                activePoints.add(SwipePoint(event.x / 120f, event.y / 120f, 0L))
                refreshSuggestion(previewOnly = true)
                return true
            }

            MotionEvent.ACTION_UP -> {
                activePoints.add(SwipePoint(event.x / 120f, event.y / 120f, event.eventTime - event.downTime))
                val best = refreshSuggestion(previewOnly = false)
                if (!best.isNullOrBlank()) {
                    currentInputConnection?.commitText("$best ", 1)
                }
                activePoints.clear()
                return true
            }
        }
        return false
    }

    private fun refreshSuggestion(previewOnly: Boolean): String? {
        val top = decoder.decode(activePoints).firstOrNull()?.word
        suggestionText.text = top ?: "..."
        return if (previewOnly) null else top
    }
}
