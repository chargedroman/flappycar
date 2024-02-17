package com.roman.flappy.game.tools

/**
 *
 * Author: romanvysotsky
 * Created: 17.02.24
 */
interface GameSpeedController {
    fun onTick(currentTick: Long, currentSpeedKmPerHour: Int): Int
}
