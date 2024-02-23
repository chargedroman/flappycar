package com.roman.flappy.game.drawers

import android.content.Context
import android.graphics.Canvas
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

}
