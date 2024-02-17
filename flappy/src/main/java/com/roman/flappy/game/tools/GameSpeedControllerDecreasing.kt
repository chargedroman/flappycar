package com.roman.flappy.game.tools

/**
 *
 * Author: romanvysotsky
 * Created: 17.02.24
 */
class GameSpeedControllerDecreasing: GameSpeedController {

    override fun onTick(currentTick: Long, currentSpeedKmPerHour: Int): Int {
        return when {
            currentSpeedKmPerHour < 30 -> forTick(60, currentTick)
            currentSpeedKmPerHour < 60 -> forTick(120, currentTick)
            currentSpeedKmPerHour < 90 -> forTick(180, currentTick)
            currentSpeedKmPerHour < 120 -> forTick(240, currentTick)
            currentSpeedKmPerHour < 150 -> forTick(270, currentTick)
            else -> forTick(300, currentTick)
        }
    }

    private fun forTick(each: Int, currentTick: Long): Int {
        return if (currentTick % each == 0L) {
            1
        } else {
            0
        }
    }

}
