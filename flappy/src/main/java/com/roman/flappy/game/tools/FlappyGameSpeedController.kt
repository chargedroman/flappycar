package com.roman.flappy.game.tools

import com.roman.flappy.game.models.FlappyBatteryStatus

/**
 *
 * Author: romanvysotsky
 * Created: 17.02.24
 */
interface FlappyGameSpeedController {
    fun onTick(
        currentTick: Long,
        currentBatteryStatus: FlappyBatteryStatus?
    )
    fun getCurrentSpeedKmPerHour(): Int
    fun getCurrentSpeedPercent(): Double
    fun getCurrentDistanceMeters(): Long

    fun notifyCarOnChargingLane(isOnLane: Boolean)
    fun notifyCarOnCone(isOnCone: Boolean)
    fun notifyCarOnObstruction(isOnObstruction: Boolean)
}
