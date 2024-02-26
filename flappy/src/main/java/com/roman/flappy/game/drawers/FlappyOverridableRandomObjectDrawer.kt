package com.roman.flappy.game.drawers

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.roman.flappy.view.FlappyDrawer
import kotlin.random.Random

/**
 * Abstract helper to implement more overridable objects
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

abstract class FlappyOverridableRandomObjectDrawer: FlappyDrawer {


    abstract val flappyObjects: List<FlappyObject>

    abstract fun nextRandomIsLeft(): Boolean

    /**
     * how many % of the car surface must be within the an object to detect a collision
     */
    abstract val collisionThreshold: Double



    protected var isCarOnLeftSide = false
    protected val randomGenerator = Random(42)

    private val canvasBounds = Rect()
    private val intersection = Rect()
    private var maxShift = 0
    private var highestObjectHeight: Int? = null


    override fun onDraw(canvas: Canvas) {
        canvasBounds.right = canvas.width
        canvasBounds.bottom = canvas.height
        maxShift = canvasBounds.bottom

        for (obj in flappyObjects) {
            onDrawFlappyObject(canvas, obj)
        }
    }


    private fun onDrawFlappyObject(canvas: Canvas, flappyObject: FlappyObject) {
        val drawable = (if (flappyObject.collidedWithUser)
            flappyObject.drawableCollided ?: flappyObject.drawable
        else
            flappyObject.drawable) ?: return

        setObjectBounds(flappyObject, canvasBounds)

        drawable.bounds = flappyObject.bounds
        drawable.draw(canvas)
    }

    abstract fun setObjectBounds(flappyObject: FlappyObject, canvasBounds: Rect)


    fun isCollidingWith(carPosition: Rect): Boolean {
        this.isCarOnLeftSide = isCarOnLeftSide(carPosition)
        var isColliding = false

        for (flappyObject in flappyObjects) {
            val intersects = intersectsArea(carPosition, flappyObject.bounds, collisionThreshold)
            flappyObject.collidedWithUser = flappyObject.collidedWithUser || intersects
            isColliding = isColliding || intersects
        }

        return isColliding
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
    private fun intersectsArea(rectA: Rect, rectB: Rect, areaIntersectThreshold: Double): Boolean {
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
    

    fun tickTock(currentSpeedKmPerHour: Int) {
        if (canvasBounds.bottom == 0) {
            //wait until everything initializes
            return
        }

        if (mustReShuffle()) {
            for ((i, flappyObject) in flappyObjects.withIndex()) {
                reShuffle(flappyObject, i)
            }
        }

        for (flappyObject in flappyObjects) {
            update(flappyObject, currentSpeedKmPerHour)
        }
    }

    private fun update(
        flappyObject: FlappyObject,
        currentSpeedKmPerHour: Int
    ) {
        flappyObject.currentDistanceShift += currentSpeedKmPerHour

        if (canvasBounds.bottom != 0 && flappyObject.currentDistanceShift > canvasBounds.bottom) {
            flappyObject.seenByUser = true
        }
    }

    private fun reShuffle(flappyObject: FlappyObject, index: Int) {
        flappyObject.seenByUser = false
        flappyObject.collidedWithUser = false
        flappyObject.isLeft = nextRandomIsLeft()
        flappyObject.randomDistance = getNextRandomDistance(index)
        flappyObject.currentDistanceShift = - flappyObject.randomDistance
    }

    private fun mustReShuffle(): Boolean {
        var seenAll = true
        for (flappyObject in flappyObjects) {
            val mustBeInitialized = flappyObject.currentDistanceShift == Int.MIN_VALUE
            seenAll = seenAll && (flappyObject.seenByUser || mustBeInitialized)
        }
        return seenAll
    }

    private fun getNextRandomDistance(index: Int): Int {
        val baseRange = getHighestObjectHeight()
        val increment = getHighestObjectHeight() * 2

        val lowerBound = index * increment + baseRange
        val upperBound = lowerBound + baseRange

        return randomGenerator.nextInt(lowerBound, upperBound)
    }

    private fun getHighestObjectHeight(): Int {
        val height = highestObjectHeight
        if (height != null)
            return height

        highestObjectHeight = flappyObjects.maxOf { it.getHeightInside(canvasBounds) }
        return highestObjectHeight!!
    }


    class FlappyObject(
        val drawable: Drawable?,
        val drawableCollided: Drawable? = null,
        val bounds: Rect = Rect(),
        var isLeft: Boolean = true,
        var currentDistanceShift: Int = Int.MIN_VALUE,
        var randomDistance: Int = 0,
        var seenByUser: Boolean = false,
        var collidedWithUser: Boolean = false,
    ) {
        private val aspectRatio = (drawable?.intrinsicHeight?.toFloat() ?: 0f) / (drawable?.intrinsicWidth?.toFloat() ?: 1f)

        fun getHeightInside(canvasBounds: Rect): Int {
            val adjustedWidth = getWidthInside(canvasBounds)
            return (adjustedWidth * aspectRatio).toInt()
        }

        fun getWidthInside(canvasBounds: Rect): Int {
            return (canvasBounds.right / 2.5).toInt()
        }
    }

}
