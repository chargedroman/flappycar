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
        nextRandomLaneTime(),
        0
    )

    private val laneBig = Lane(
        ContextCompat.getDrawable(context, R.drawable.charging_lane_big),
        Rect(),
        false,
        Int.MIN_VALUE,
        nextRandomLaneTime(),
        0
    )

    private val laneHuge = Lane(
        ContextCompat.getDrawable(context, R.drawable.charging_lane_huge),
        Rect(),
        false,
        Int.MIN_VALUE,
        nextRandomLaneTime(),
        0
    )

    private val laneWarningLeft =
        ContextCompat.getDrawable(context, R.drawable.lane_warning_left)
    private val laneWarningRight =
        ContextCompat.getDrawable(context, R.drawable.lane_warning_right)


    private val canvasBounds = Rect()
    private val intersection = Rect()
    private val tempRect1 = Rect()
    private val tempRect2 = Rect()
    private var maxShift = 0
    private var isCarOnLeftSide = false


    override fun onDraw(canvas: Canvas) {
        canvasBounds.right = canvas.width
        canvasBounds.bottom = canvas.height
        maxShift = canvasBounds.bottom

        onDrawLane(canvas, laneSmall)
        onDrawLane(canvas, laneBig)
        onDrawLane(canvas, laneHuge)

        onDrawWarning(canvas, laneSmall)
        onDrawWarning(canvas, laneBig)
        onDrawWarning(canvas, laneHuge)
    }

    private fun onDrawWarning(canvas: Canvas, lane: Lane) {
        val willComeFromTop = lane.currentShift > Int.MIN_VALUE && lane.currentShift < 0
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

    private fun onDrawLane(canvas: Canvas, lane: Lane) {
        if (lane.lane == null) return

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
        fixOverlappingLanes(laneSmall, laneBig)
        fixOverlappingLanes(laneSmall, laneHuge)
        fixOverlappingLanes(laneBig, laneHuge)
    }

    private fun fixOverlappingLanes(a: Lane, b: Lane) {
        val isOnSameSide = a.isLeft == b.isLeft
        if (isOnSameSide.not())
            return

        tempRect1.set(a.bounds)
        tempRect2.set(b.bounds)

        //when they intersect, just place the upper one further on top of the other
        if (tempRect1.intersect(tempRect2)) {
            val lane = if (a.bounds.top > b.bounds.top) a else b
            lane.currentShift -= a.bounds.height() + b.bounds.height()
        }
    }

    private fun tickTock(lane: Lane, currentTick: Long, currentSpeedKmPerHour: Int) {
        val isLaneNotComing = lane.currentShift == Int.MIN_VALUE
        val isLaneVisible = lane.bounds.intersect(canvasBounds)
        val isTimeToShowLane = currentTick - lane.tickWhenShiftSet == lane.laneTime

        if (isLaneNotComing || (!isLaneVisible && isTimeToShowLane)) {
            lane.tickWhenShiftSet = currentTick
            lane.isLeft = nextRandomIsLeft()
            lane.laneTime = nextRandomLaneTime()
            //to make sure it comes from the top and doesn't "appear" suddenly
            lane.currentShift = - canvasBounds.bottom - (lane.lane?.intrinsicHeight ?: 0) * 10
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
        return randomGenerator.nextLong(240, 960)
    }

    /**
     * have a 1/4 chance that the lane spawns on the OPPOSITE side of where the car is
     * (make the user move!)
     */
    private fun nextRandomIsLeft(): Boolean {
        val random = randomGenerator.nextInt(0, 6) == 0
        return random xor !isCarOnLeftSide
    }


    private class Lane(
        val lane: Drawable?,
        val bounds: Rect,
        var isLeft: Boolean,
        var currentShift: Int,
        var laneTime: Long,
        var tickWhenShiftSet: Long,
    )

}
