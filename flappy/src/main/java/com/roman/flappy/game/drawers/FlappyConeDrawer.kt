package com.roman.flappy.game.drawers

import android.content.Context
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


    /**
     * have a 1/4 chance that the object spawns on the SAME side of where the car is
     * (make the user move!)
     */
    override fun nextRandomIsLeft(): Boolean {
        val random = randomGenerator.nextInt(0, 6) == 0
        return random xor isCarOnLeftSide
    }

}
