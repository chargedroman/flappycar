package com.roman.flappy.game.tools

import com.roman.flappy.game.models.FlappyBatteryStatus

/**
 *
 * Author: romanvysotsky
 * Created: 17.02.24
 */

interface FlappyBatteryController {
    fun onTick(currentTick: Long, currentSpeedKmPerHour: Int)

    fun getCurrentBatteryStatus(): FlappyBatteryStatus

    fun notifyCarOnChargingLane(isOnLane: Boolean)
    fun notifyCarOnCone(isOnLane: Boolean)
    fun notifyCarOnObstruction(isOnObstruction: Boolean)
}
