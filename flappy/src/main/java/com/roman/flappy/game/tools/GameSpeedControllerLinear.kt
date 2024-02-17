package com.roman.flappy.game.tools

/**
 *
 * Author: romanvysotsky
 * Created: 17.02.24
 */
class GameSpeedControllerLinear: GameSpeedController {

    override fun onTick(currentTick: Long, currentSpeedKmPerHour: Int): Int {
        return if (currentTick % 60L == 0L)
            1
        else
            0
    }

}
