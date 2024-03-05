package com.roman.flappy.game.tools

import com.roman.flappy.game.models.FlappyBatteryStatus

/**
 *
 * Author: romanvysotsky
 * Created: 17.02.24
 */
class FlappyBatteryControllerImpl(
    private val currentStatus: FlappyBatteryStatus
): FlappyBatteryController {


    override fun onTick(currentTick: Long, currentSpeedKmPerHour: Int) {
        currentStatus.currentChargeWh -= when {
            currentSpeedKmPerHour < 30 -> 5
            currentSpeedKmPerHour < 60 -> 10
            currentSpeedKmPerHour < 90 -> 15
            currentSpeedKmPerHour < 120 -> 20
            currentSpeedKmPerHour < 150 -> 25
            else -> 30
        }
        currentStatus.currentChargeWh = currentStatus.currentChargeWh
            .coerceAtLeast(0)
    }

    override fun getCurrentBatteryStatus(): FlappyBatteryStatus {
        return currentStatus
    }

    override fun notifyCarOnChargingLane(isOnLane: Boolean) {
        if (isOnLane.not()) return

        //lets charge 100wh for now, which is 100wh per tick as long as you hit the lane on each tick
        currentStatus.currentChargeWh += 100
        currentStatus.currentChargeWh = currentStatus.currentChargeWh
            .coerceAtMost(currentStatus.maxChargeWh)
    }

    override fun notifyCarOnCone(isOnLane: Boolean) {
        if (isOnLane.not()) return

        //lets charge subtract 1% for now
        currentStatus.currentChargeWh -= currentStatus.getOnePercent() * 2
        currentStatus.currentChargeWh = currentStatus.currentChargeWh
            .coerceAtLeast(0)
    }

    override fun notifyCarOnObstruction(isOnObstruction: Boolean) {
        if (isOnObstruction.not()) return

        //lets charge subtract 4% for now
        currentStatus.currentChargeWh -= currentStatus.getOnePercent() * 4
        currentStatus.currentChargeWh = currentStatus.currentChargeWh
            .coerceAtLeast(0)
    }


}
