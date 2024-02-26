package com.roman.flappy.game.tools

import com.roman.flappy.game.models.FlappyBatteryStatus

/**
 *
 * Author: romanvysotsky
 * Created: 17.02.24
 */
class FlappyBatteryControllerOne(
    //we could also start with 65600 = 80%
    private val currentStatus: FlappyBatteryStatus
        = FlappyBatteryStatus(10000, 82000)
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

        //lets charge subtract 100wh for now
        currentStatus.currentChargeWh -= 100
        currentStatus.currentChargeWh = currentStatus.currentChargeWh
            .coerceAtLeast(0)
    }


}
