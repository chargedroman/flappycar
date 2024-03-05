package com.roman.flappy.game.tools

import com.roman.flappy.game.models.FlappyBatteryStatus
import kotlin.math.round

/**
 *
 * Author: romanvysotsky
 * Created: 17.02.24
 */
class FlappyGameSpeedControllerImpl(
    private val gameSpeedInitialKmPerH: Int = 10,
    private val gameSpeedMaxKmPerH: Int = 201
): FlappyGameSpeedController {

    private var currentKmPerH = gameSpeedInitialKmPerH
    private var currentDistanceCm: Double = 0.0


    override fun onTick(
        currentTick: Long,
        currentBatteryStatus: FlappyBatteryStatus?
    ) {
        currentDistanceCm += centimetersTravelledInOneTickFor(currentKmPerH)

        if (currentBatteryStatus?.isEmpty() == true) {
            if (currentTick % 30L == 0L) {
                currentKmPerH -= 5
                currentKmPerH = currentKmPerH.coerceAtLeast(0)
            }
            return
        }

        currentKmPerH += when {
            currentKmPerH < 3 -> forTick(5, currentTick)
            currentKmPerH < 7 -> forTick(15, currentTick)
            currentKmPerH < 15 -> forTick(30, currentTick)
            currentKmPerH < 30 -> forTick(60, currentTick)
            currentKmPerH < 60 -> forTick(120, currentTick)
            currentKmPerH < 90 -> forTick(180, currentTick)
            currentKmPerH < 120 -> forTick(240, currentTick)
            currentKmPerH < 150 -> forTick(270, currentTick)
            else -> forTick(300, currentTick)
        }

        if (currentKmPerH > gameSpeedMaxKmPerH)
            currentKmPerH = gameSpeedMaxKmPerH
    }

    override fun getCurrentSpeedKmPerHour(): Int {
        return currentKmPerH
    }

    override fun getCurrentSpeedPercent(): Double {
        return (currentKmPerH / (gameSpeedMaxKmPerH / 100.0)) / 100.0
    }

    override fun getCurrentDistanceMeters(): Long {
        return round(currentDistanceCm).toLong() / 100
    }


    override fun notifyCarOnChargingLane(isOnLane: Boolean) {
        //nop
    }

    override fun notifyCarOnCone(isOnCone: Boolean) {
        if (isOnCone.not() || currentKmPerH >= 30) return

        currentKmPerH -= 1
        currentKmPerH = currentKmPerH.coerceAtLeast(0)
    }

    override fun notifyCarOnObstruction(isOnObstruction: Boolean) {
        if (isOnObstruction.not()) return

        currentKmPerH -= 4
        currentKmPerH = currentKmPerH.coerceAtLeast(0)
    }


    private fun centimetersTravelledInOneTickFor(speedKmPerHour: Int): Double {
        val metersPerSecond = speedKmPerHour / 3.6
        return (metersPerSecond * 100) / 60.0
    }

    private fun forTick(each: Int, currentTick: Long): Int {
        return if (currentTick % each == 0L) {
            1
        } else {
            0
        }
    }

}
