package com.roman.flappy.game.drawers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.roman.flappy.R
import com.roman.flappy.view.FlappyDrawer

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

class FlappyCarDrawer(context: Context): FlappyDrawer {

    companion object {
        const val MIN_MOVE = -15
        const val MAX_MOVE = 15
        const val MOTION_SENSITIVITY_X = 160
        const val MOTION_SENSITIVITY_Y = 60
        const val MOTION_SENSITIVITY_MIN = 10
    }

    private val carDrawable: Drawable?
            = ContextCompat.getDrawable(context, R.drawable.car2)

    private val chargingBubbleDrawable: Drawable?
            = ContextCompat.getDrawable(context, R.drawable.charge_bubble)

    private val carBounds = Rect()
    private val chargingBubbleBounds = Rect()
    private val touchPoint = Point()

    private var canvasWidth = 0
    private var canvasHeight = 0

    private var currentShiftX = 0
    private var currentShiftY = 0

    private var isCarOnChargingLane: Boolean = false


    override fun onDraw(canvas: Canvas) {
        carDrawable?.let {
            this.canvasWidth = canvas.width
            this.canvasHeight = canvas.height

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

        chargingBubbleDrawable?.let {
            if (isCarOnChargingLane) {
                val halfWidth = it.intrinsicWidth
                val halfHeight = it.intrinsicHeight

                chargingBubbleBounds.top = carBounds.top
                chargingBubbleBounds.left = carBounds.right - halfWidth
                chargingBubbleBounds.bottom = carBounds.top + halfHeight
                chargingBubbleBounds.right = carBounds.right

                it.bounds = chargingBubbleBounds
                it.draw(canvas)
            }
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

        fixShift()
    }


    fun onTilt(motionX: Float, motionY: Float, speedPercent: Double) {
        val motionSensitivityX = (MOTION_SENSITIVITY_X * speedPercent).toInt()
            .coerceAtLeast(MOTION_SENSITIVITY_MIN)
        val motionSensitivityY = (MOTION_SENSITIVITY_Y * speedPercent).toInt()
            .coerceAtLeast(MOTION_SENSITIVITY_MIN)
        val x = (motionSensitivityX * -motionX).toInt()
        val y = (motionSensitivityY * motionY).toInt()

        //shift based on sensor data
        currentShiftX += x.coerceAtLeast(MIN_MOVE).coerceAtMost(MAX_MOVE)
        currentShiftY += y.coerceAtLeast(MIN_MOVE).coerceAtMost(MAX_MOVE)

        fixShift()
    }

    private fun fixShift() {
        val canvasWidthHalf = canvasWidth / 2
        val carWidthHalf = carBounds.width() / 2

        currentShiftX = currentShiftX
            .coerceAtLeast(-canvasWidthHalf + carWidthHalf)
            .coerceAtMost(canvasWidthHalf - carWidthHalf)

        val canvasHeightHalf = canvasHeight / 2
        val carHeightHalf = carBounds.height() / 2

        currentShiftY = currentShiftY
            .coerceAtLeast(-canvasHeightHalf - carHeightHalf)
            .coerceAtMost(carHeightHalf)
    }

    fun getCarBounds(): Rect {
        return carBounds
    }

    fun notifyCarOnChargingLane(isOnLane: Boolean) {
        this.isCarOnChargingLane = isOnLane
    }


}
