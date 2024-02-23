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

class FlappyChargingLaneDrawer(context: Context): FlappyOverridableRandomObjectDrawer() {

    private val laneWarningLeft =
        ContextCompat.getDrawable(context, R.drawable.lane_warning_left)
    private val laneWarningRight =
        ContextCompat.getDrawable(context, R.drawable.lane_warning_right)

    private val list = mutableListOf(
        FlappyObject(ContextCompat.getDrawable(context, R.drawable.charging_lane_small)),
        FlappyObject(ContextCompat.getDrawable(context, R.drawable.charging_lane_big)),
        FlappyObject(ContextCompat.getDrawable(context, R.drawable.charging_lane_big)),
        FlappyObject(ContextCompat.getDrawable(context, R.drawable.charging_lane_big)),
        FlappyObject(ContextCompat.getDrawable(context, R.drawable.charging_lane_huge))
    )

    override val flappyObjects: List<FlappyObject>
        get() = list

    override val collisionThreshold: Double
        get() = 0.5


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (obj in flappyObjects) {
            onDrawWarning(canvas, obj)
        }
    }


    private fun onDrawWarning(canvas: Canvas, lane: FlappyObject) {
        val willComeFromTop = lane.currentDistanceShift > Int.MIN_VALUE && lane.currentDistanceShift < 0
        if (willComeFromTop) {
            val warning = if (lane.isLeft) laneWarningLeft else laneWarningRight
            val height = warning?.intrinsicHeight ?: 0
            val halfHeight = height / 4
            val left = if (lane.isLeft) halfHeight else canvas.width - halfHeight - height
            val right = if (lane.isLeft) left + height else canvas.width - halfHeight
            val bottom = halfHeight + height

            warning?.let {
                warning.setBounds(left, halfHeight, right, bottom)
                warning.draw(canvas)
            }
        }
    }


    /**
     * have a 1/4 chance that the object spawns on the OPPOSITE side of where the car is
     * (make the user move!)
     */
    override fun nextRandomIsLeft(): Boolean {
        val random = randomGenerator.nextInt(0, 6) == 0
        return random xor !isCarOnLeftSide
    }


    /**
     * think of the screen as right and left and just set the lane bounds to either
     */
    override fun setObjectBounds(flappyObject: FlappyObject, canvasBounds: Rect) {
        val adjustedWidth = flappyObject.getWidthInside(canvasBounds)
        val adjustedHeight = flappyObject.getHeightInside(canvasBounds)

        val halfWidth = adjustedWidth / 2
        val leftMiddle = (canvasBounds.right / 2) / 2
        //the background is asymmetric; change/remove -20 if using another background
        val rightMiddle = (canvasBounds.right / 2) + leftMiddle - 20

        val currentShift = flappyObject.currentDistanceShift

        if (flappyObject.isLeft) {
            flappyObject.bounds.left = leftMiddle - halfWidth
            flappyObject.bounds.right = leftMiddle + halfWidth
        } else {
            flappyObject.bounds.left = rightMiddle - halfWidth
            flappyObject.bounds.right = rightMiddle + halfWidth
        }

        flappyObject.bounds.top = currentShift
        flappyObject.bounds.bottom = adjustedHeight + currentShift
    }

}
