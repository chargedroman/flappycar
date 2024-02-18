package com.roman.flappy.game.drawers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.roman.flappy.R
import com.roman.flappy.view.FlappyDrawer

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */
class FlappyBackgroundDrawer(context: Context): FlappyDrawer {

    private val backgroundDrawable1: Drawable?
        = ContextCompat.getDrawable(context, R.drawable.street2)

    private val backgroundDrawable2: Drawable?
            = ContextCompat.getDrawable(context, R.drawable.street2)

    private val drawableAspectRatio =
        (backgroundDrawable1?.intrinsicHeight?.toFloat() ?: 0.0f) / (backgroundDrawable1?.intrinsicWidth ?: 1)


    private val bounds1 = Rect()
    private val bounds2 = Rect()

    private var maxShift = 0
    private var currentShift = 0


    /**
     * draw the backgrounds where [bounds2] is on top of [bounds1] and
     * all of that is shifted by [currentShift] to the bottom
     */
    override fun onDraw(canvas: Canvas) {
        val adjustedWidth = canvas.width
        val adjustedHeight = (adjustedWidth * drawableAspectRatio).toInt()

        this.maxShift = adjustedHeight
        val currentShift = currentShift

        bounds1.left = 0
        bounds1.right = adjustedWidth
        bounds2.left = 0
        bounds2.right = adjustedWidth

        bounds1.top = currentShift
        bounds1.bottom = adjustedHeight + currentShift

        bounds2.top = -adjustedHeight + currentShift
        bounds2.bottom = currentShift

        backgroundDrawable1?.bounds = bounds1
        backgroundDrawable2?.bounds = bounds2
        backgroundDrawable1?.draw(canvas)
        backgroundDrawable2?.draw(canvas)
    }

    fun tickTock(currentSpeedKmPerHour: Int) {
        if (maxShift == 0)
            return

        if (currentShift >= (maxShift - currentSpeedKmPerHour))
            currentShift = 0
        else
            currentShift += currentSpeedKmPerHour
    }

}
