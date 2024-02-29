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

    companion object {
        const val MAX_LANES = 6
        const val LANE_SIZE_FACTOR = 2.5
    }

    private val laneWarningLeft =
        ContextCompat.getDrawable(context, R.drawable.ic_lane_warning_left)
    private val laneWarningRight =
        ContextCompat.getDrawable(context, R.drawable.ic_lane_warning_right)

    private val laneSmall =
        ContextCompat.getDrawable(context, R.drawable.charging_lane_small)
    private val laneBig =
        ContextCompat.getDrawable(context, R.drawable.charging_lane_big)
    private val laneHuge =
        ContextCompat.getDrawable(context, R.drawable.charging_lane_huge)


    private val list = mutableListOf(
        FlappyObject(LANE_SIZE_FACTOR, laneSmall),
        FlappyObject(LANE_SIZE_FACTOR, laneHuge),
        FlappyObject(LANE_SIZE_FACTOR, laneSmall),
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
        //this is to make the charging lanes look more centered; due to the uneven street texture
        val balancing = adjustedWidth / 20

        val halfWidth = adjustedWidth / 2
        val leftMiddle = (canvasBounds.right / 2) / 2 + balancing
        val rightMiddle = (canvasBounds.right / 2) + leftMiddle - (balancing * 2)

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

    fun udpateAmountOfLanes(currentDistanceMeters: Long) {
        if (list.size >= MAX_LANES) {
            return
        }

        val amountOfLanes = 1 + (currentDistanceMeters / 200).coerceAtLeast(1)

        if (list.size < amountOfLanes) {
            if (currentDistanceMeters <= 600)
                list.add(FlappyObject(LANE_SIZE_FACTOR, laneBig))
            else
                list.add(FlappyObject(LANE_SIZE_FACTOR, laneHuge))
        }
    }

}
