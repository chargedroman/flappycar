package com.roman.flappy.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.graphics.contains
import com.roman.flappy.R

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

class CarDrawer(context: Context): Drawer {

    companion object {
        const val MIN_MOVE = -15
        const val MAX_MOVE = 15
    }

    private val carDrawable: Drawable?
            = ContextCompat.getDrawable(context, R.drawable.car2)

    private val carBounds = Rect()
    private val touchPoint = Point()

    private var currentShiftX = 0
    private var currentShiftY = 0

    override fun onDraw(canvas: Canvas) {
        carDrawable?.let {
            val carWidthHalf = it.intrinsicWidth / 2
            val carHeightHalf = it.intrinsicHeight / 2
            val canvasWidthHalf = canvas.width / 2

            carBounds.left = canvasWidthHalf - carWidthHalf + currentShiftX
            carBounds.right = canvasWidthHalf + carWidthHalf + currentShiftX
            carBounds.top = canvas.height - it.intrinsicHeight - carHeightHalf + currentShiftY
            carBounds.bottom = canvas.height - carHeightHalf + currentShiftY

            carBounds.left = carBounds.left
                .coerceAtLeast(0)
                .coerceAtMost(canvas.width - it.intrinsicWidth)

            carBounds.right = carBounds.right
                .coerceAtLeast(it.intrinsicWidth)
                .coerceAtMost(canvas.width)

            carBounds.top = carBounds.top
                .coerceAtLeast(0)
                .coerceAtMost(canvas.height - it.intrinsicHeight)

            carBounds.bottom = carBounds.bottom
                .coerceAtLeast(it.intrinsicHeight)
                .coerceAtMost(canvas.height)

            it.bounds = carBounds
            it.draw(canvas)
        }
    }


    fun onTouch(event: MotionEvent) {
        //init
        val action = event.action and MotionEvent.ACTION_MASK
        val isActionUp = action ==  MotionEvent.ACTION_UP
        if (touchPoint.x == 0 && touchPoint.y == 0 || isActionUp) {
            touchPoint.x = event.x.toInt()
            touchPoint.y = event.y.toInt()
        }

        val dx = event.x - touchPoint.x
        val dy = event.y - touchPoint.y
        touchPoint.x = event.x.toInt()
        touchPoint.y = event.y.toInt()
        
        //shift based on user touch
        currentShiftX += dx.toInt().coerceAtLeast(MIN_MOVE).coerceAtMost(MAX_MOVE)
        currentShiftY += dy.toInt().coerceAtLeast(MIN_MOVE).coerceAtMost(MAX_MOVE)
    }

}
