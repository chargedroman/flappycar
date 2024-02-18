package com.roman.flappy.game.drawers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.roman.flappy.R
import com.roman.flappy.view.FlappyDrawer
import kotlin.random.Random

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

class FlappyChargingLaneDrawer(context: Context): FlappyDrawer {

    private val randomGenerator = Random(42)

    private val laneSmall = Lane(
        ContextCompat.getDrawable(context, R.drawable.charging_lane_small),
        Rect(),
        true,
        Int.MIN_VALUE,
        nextRandomLaneTime()
    )

    private val laneBig = Lane(
        ContextCompat.getDrawable(context, R.drawable.charging_lane_big),
        Rect(),
        false,
        Int.MIN_VALUE,
        nextRandomLaneTime()
    )

    private val laneHuge = Lane(
        ContextCompat.getDrawable(context, R.drawable.charging_lane_huge),
        Rect(),
        false,
        Int.MIN_VALUE,
        nextRandomLaneTime()
    )

    private val canvasBounds = Rect()
    private val intersection = Rect()
    private var maxShift = 0
    private var isCarOnLeftSide = false


    override fun onDraw(canvas: Canvas) {
        canvasBounds.right = canvas.width
        canvasBounds.bottom = canvas.height
        maxShift = canvasBounds.bottom

        onDrawLane(canvas, laneSmall)
        onDrawLane(canvas, laneBig)
        onDrawLane(canvas, laneHuge)
    }

    private fun onDrawLane(canvas: Canvas, lane: Lane) {
        if (lane.lane == null) return

        /*
        val aspectRatio = lane.lane.intrinsicHeight / lane.lane.intrinsicWidth
        val adjustedHeight = ((canvas.height / 2.5) * aspectRatio).toInt()
        val adjustedWidth = (canvas.width / 2.5).toInt()
         */

        val aspectRatio = lane.lane.intrinsicHeight.toFloat() / lane.lane.intrinsicWidth
        val adjustedWidth = (canvas.width / 2.5).toInt()
        val adjustedHeight = (adjustedWidth * aspectRatio).toInt()

        val halfWidth = adjustedWidth / 2
        val leftMiddle = (canvas.width / 2) / 2
        //the background is asymmetric; change/remove -20 if using another background
        val rightMiddle = (canvas.width / 2) + leftMiddle - 20

        val currentShift = lane.currentShift

        if (lane.isLeft) {
            lane.bounds.left = leftMiddle - halfWidth
            lane.bounds.right = leftMiddle + halfWidth
        } else {
            lane.bounds.left = rightMiddle - halfWidth
            lane.bounds.right = rightMiddle + halfWidth
        }

        lane.bounds.top = currentShift
        lane.bounds.bottom = adjustedHeight + currentShift

        lane.lane.bounds = lane.bounds
        lane.lane.draw(canvas)
    }

    fun isOnChargingLane(carPosition: Rect): Boolean {
        this.isCarOnLeftSide = isCarOnLeftSide(carPosition)
        return intersectsArea(carPosition, laneSmall.bounds)
                || intersectsArea(carPosition, laneBig.bounds)
                || intersectsArea(carPosition, laneHuge.bounds)
    }

    private fun isCarOnLeftSide(carPosition: Rect): Boolean {
        val carCenter = carPosition.centerX()
        val canvasCenter = canvasBounds.centerX()
        return carCenter <= canvasCenter
    }

    /**
     *  @return true if [rectA] intersects [rectB] in such a way that
     *  [areaIntersectThreshold]% of [rectA] is inside [rectB]
     */
    private fun intersectsArea(rectA: Rect, rectB: Rect, areaIntersectThreshold: Double = 0.5): Boolean {
        if (rectB.intersect(rectA)) {
            intersection.left = rectA.left.coerceAtLeast(rectB.left)
            intersection.top = rectA.top.coerceAtLeast(rectB.top)
            intersection.right = rectA.right.coerceAtMost(rectB.right)
            intersection.bottom = rectA.bottom.coerceAtMost(rectB.bottom)
            val areaRectA = rectA.width() * rectA.height().toFloat()
            val areaIntersection = intersection.width() * intersection.height().toFloat()
            val threshold = areaIntersectThreshold * areaRectA
            return areaIntersection >= threshold
        }

        return false
    }
    

    fun tickTock(currentTick: Long, currentSpeedKmPerHour: Int) {
        tickTock(laneSmall, currentTick, currentSpeedKmPerHour)
        tickTock(laneBig, currentTick, currentSpeedKmPerHour)
        tickTock(laneHuge, currentTick, currentSpeedKmPerHour)
    }

    private fun tickTock(lane: Lane, currentTick: Long, currentSpeedKmPerHour: Int) {
        val isLaneVisible = lane.bounds.intersect(canvasBounds)
        val isTimeToShowLane = (currentTick + lane.laneTime) % lane.laneTime == 0L

        if (!isLaneVisible && isTimeToShowLane) {
            lane.isLeft = nextRandomIsLeft()
            lane.laneTime = nextRandomLaneTime()
            //to make sure it comes from the top and doesn't "appear" suddenly
            lane.currentShift = - canvasBounds.bottom - (lane.lane?.intrinsicHeight ?: 0) * 4
        }

        tickTock(lane, currentSpeedKmPerHour)
    }

    private fun tickTock(lane: Lane, currentSpeedKmPerHour: Int) {
        if (maxShift == 0)
            return

        if (lane.currentShift >= (maxShift - currentSpeedKmPerHour))
            lane.currentShift = Int.MIN_VALUE
        else
            lane.currentShift += currentSpeedKmPerHour
    }


    private fun nextRandomLaneTime(): Long {
        return randomGenerator.nextLong(1, 700)
    }

    /**
     * have a 1/4 chance that the lane spawns on the OPPOSITE side of where the car is
     * (make the user move!)
     */
    private fun nextRandomIsLeft(): Boolean {
        val random = randomGenerator.nextInt(0, 4) == 0
        return random xor isCarOnLeftSide
    }


    private class Lane(
        val lane: Drawable?,
        val bounds: Rect,
        var isLeft: Boolean,
        var currentShift: Int,
        var laneTime: Long,
    )

}
