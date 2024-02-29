package com.roman.flappy.game.drawers

import android.content.Context
import android.graphics.Rect
import androidx.core.content.ContextCompat
import com.roman.flappy.R

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

class FlappyConeDrawer(context: Context): FlappyOverridableRandomObjectDrawer() {

    companion object {
        const val MAX_CONES = 40
        const val CONE_SIZE_FACTOR = 10.0
    }

    private val cone = ContextCompat.getDrawable(context, R.drawable.ic_cone)
    private val coneSmashed = ContextCompat.getDrawable(context, R.drawable.ic_cone_smashed)

    private val list = mutableListOf(
        createNewCone()
    )

    override val flappyObjects: List<FlappyObject>
        get() = list

    override val collisionThreshold: Double
        get() = 0.06 //merely touching the cone is enough


    /**
     * have a 1/4 chance that the object spawns on the SAME side of where the car is
     * (make the user move!)
     */
    override fun nextRandomIsLeft(): Boolean {
        val random = randomGenerator.nextInt(0, 4) == 0
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

        val adjustedWidth = flappyObject.getWidthInside(canvasBounds)
        val adjustedHeight = flappyObject.getHeightInside(canvasBounds)

        val middle = (canvasBounds.right / 2)
        val currentShift = flappyObject.currentDistanceShift

        if (flappyObject.isLeft) {
            val randomLeftShift = flappyObject.randomDistance % (middle - adjustedWidth)
            flappyObject.bounds.left = randomLeftShift
            flappyObject.bounds.right = randomLeftShift + adjustedWidth
        } else {
            val randomLeftShift = (flappyObject.randomDistance % (middle - adjustedWidth)) + middle
            flappyObject.bounds.left = randomLeftShift
            flappyObject.bounds.right = randomLeftShift + adjustedWidth
        }

        flappyObject.bounds.top = currentShift
        flappyObject.bounds.bottom = adjustedHeight + currentShift
    }


    fun updateAmountOfCones(currentDistanceMeters: Long) {
        if (list.size >= MAX_CONES) {
            return
        }

        val amountOfCones = (currentDistanceMeters / 200).coerceAtLeast(1)

        if (list.size < amountOfCones) {
            list.add(createNewCone())
        }
    }

    private fun createNewCone(): FlappyObject {
        return FlappyObject(
            drawableWidthFactor = CONE_SIZE_FACTOR,
            drawable = cone,
            drawableCollided = coneSmashed,
        )
    }

}
