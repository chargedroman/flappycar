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

    private val list = mutableListOf(
        FlappyObject(
            drawable = ContextCompat.getDrawable(context, R.drawable.cone),
            drawableCollided = ContextCompat.getDrawable(context, R.drawable.cone_smashed),
        ),
        FlappyObject(
            drawable = ContextCompat.getDrawable(context, R.drawable.cone),
            drawableCollided = ContextCompat.getDrawable(context, R.drawable.cone_smashed),
        ),
        FlappyObject(
            drawable = ContextCompat.getDrawable(context, R.drawable.cone),
            drawableCollided = ContextCompat.getDrawable(context, R.drawable.cone_smashed),
        ),
        FlappyObject(
            drawable = ContextCompat.getDrawable(context, R.drawable.cone),
            drawableCollided = ContextCompat.getDrawable(context, R.drawable.cone_smashed),
        ),
        FlappyObject(
            drawable = ContextCompat.getDrawable(context, R.drawable.cone),
            drawableCollided = ContextCompat.getDrawable(context, R.drawable.cone_smashed),
        ),
    )

    override val flappyObjects: List<FlappyObject>
        get() = list

    override val collisionThreshold: Double
        get() = 0.05 //merely touching the cone is enough


    /**
     * have a 1/4 chance that the object spawns on the SAME side of where the car is
     * (make the user move!)
     */
    override fun nextRandomIsLeft(): Boolean {
        val random = randomGenerator.nextInt(0, 6) == 0
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

        val adjustedWidth = flappyObject.getWidthInside(canvasBounds) / 4
        val adjustedHeight = flappyObject.getHeightInside(canvasBounds) / 4

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

}
