package com.roman.flappy.game.drawers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import androidx.core.content.ContextCompat
import com.roman.flappy.R

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

class FlappyObstructionDrawer(context: Context): FlappyOverridableRandomObjectDrawer() {

    companion object {
        const val OBSTRUCTION_SIZE_FACTOR = 5.0
    }

    private val obstruction = ContextCompat.getDrawable(context, R.drawable.obstruction_big)
    private val obstructionCollided = ContextCompat.getDrawable(context, R.drawable.obstruction_big_broken)

    private val warningLeft = ContextCompat.getDrawable(context, R.drawable.ic_obstruction_warning_left)
    private val warningRight = ContextCompat.getDrawable(context, R.drawable.ic_obstruction_warning_right)

    private val list = mutableListOf<FlappyObject>()


    override val flappyObjects: List<FlappyObject>
        get() = list

    override val collisionThreshold: Double
        get() = 0.04

    override val reShuffleDistanceIndexAddon: Int
        get() = reShuffleDistance

    override val reShuffleDistanceFactor: Int
        get() = 1

    private var reShuffleDistance = 40


    /**
     * 50/50 chance
     */
    override fun nextRandomIsLeft(): Boolean {
        val random = randomGenerator.nextInt(0, 2) == 0
        return random xor isCarOnLeftSide
    }


    /**
     * Think of the screen as 2 sections of left/right
     * use the randomDistance of flappyObject to draw the cone
     * somewhere within the left or right section
     */
    override fun setObjectBounds(flappyObject: FlappyObject, canvasBounds: Rect) {
        //wait for init
        if (canvasBounds.bottom == 0 || canvasBounds.right == 0) return

        val adjustedWidthHalf = flappyObject.getWidthInside(canvasBounds) / 2
        val adjustedHeight = flappyObject.getHeightInside(canvasBounds)

        val middle = (canvasBounds.right / 2)
        val halfQuarter = middle / 4
        val currentShift = flappyObject.currentDistanceShift

        if (flappyObject.isLeft) {
            val newMiddle = middle - halfQuarter
            flappyObject.bounds.left = newMiddle - adjustedWidthHalf
            flappyObject.bounds.right = newMiddle + adjustedWidthHalf
        } else {
            val newMiddle = middle + halfQuarter
            flappyObject.bounds.left = newMiddle - adjustedWidthHalf
            flappyObject.bounds.right = newMiddle + adjustedWidthHalf
        }

        flappyObject.bounds.top = currentShift
        flappyObject.bounds.bottom = adjustedHeight + currentShift
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (obj in flappyObjects) {
            onDrawWarning(canvas, obj)
        }
    }

    private fun onDrawWarning(canvas: Canvas, obj: FlappyObject) {
        val willComeFromTop = obj.currentDistanceShift > Int.MIN_VALUE && obj.currentDistanceShift < 0

        if (willComeFromTop) {
            val warning = if (obj.isLeft) warningLeft else warningRight
            val middle = canvas.width / 2
            val halfQuarter = middle / 4
            val height = warning?.intrinsicHeight ?: 0
            val halfHeight = height / 2
            val quarterHeight = height / 4
            val bottom = quarterHeight + height

            val left = if (obj.isLeft) middle - halfQuarter - halfHeight else middle + halfQuarter - halfHeight
            val right = if (obj.isLeft) middle - halfQuarter + halfHeight else middle + halfQuarter + halfHeight

            warning?.let {
                warning.setBounds(left, quarterHeight, right, bottom)
                warning.draw(canvas)
            }
        }
    }


    fun updateAmountOfObstructions(currentDistanceMeters: Long) {
        //as soon as the user reaches 2k, we add these to make it harder
        if (currentDistanceMeters >= 200) {
            if (list.size == 0) {
                list.add(createNewObstruction())
            }

            //also make them appear faster in succession the further the user gets
            reShuffleDistance = when (currentDistanceMeters) {
                in 0 .. 250 -> 100
                in 0 .. 300 -> 80
                in 0 .. 350 -> 60
                else -> 40
            }
        }
    }

    private fun createNewObstruction(): FlappyObject {
        return FlappyObject(
            drawableWidthFactor = OBSTRUCTION_SIZE_FACTOR,
            drawable = obstruction,
            drawableCollided = obstructionCollided,
        )
    }

}
