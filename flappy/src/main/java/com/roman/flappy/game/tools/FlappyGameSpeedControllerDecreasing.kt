package com.roman.flappy.game.tools

import com.roman.flappy.game.models.FlappyBatteryStatus
import kotlin.math.round

/**
 *
 * Author: romanvysotsky
 * Created: 17.02.24
 */
class FlappyGameSpeedControllerDecreasing(
    private val gameSpeedInitialKmPerH: Int = 10,
    private val gameSpeedMaxKmPerH: Int = 201
): FlappyGameSpeedController {

    private var currentKmPerH = gameSpeedInitialKmPerH
    private var currentDistanceCm = 0L


    override fun onTick(
        currentTick: Long,
        currentBatteryStatus: FlappyBatteryStatus?
    ) {
        currentDistanceCm += centimetersTravelledInOneTickFor(currentKmPerH)

        if (currentBatteryStatus?.isEmpty() == true) {
            if (currentTick % 30L == 0L) {
                currentKmPerH -= 2
                currentKmPerH = currentKmPerH.coerceAtLeast(0)
            }
            return
        }

        currentKmPerH += when {
            currentKmPerH < 30 -> forTick(60, currentTick)
            currentKmPerH < 60 -> forTick(120, currentTick)
            currentKmPerH < 90 -> forTick(180, currentTick)
            currentKmPerH < 120 -> forTick(240, currentTick)
            currentKmPerH < 150 -> forTick(270, currentTick)
            else -> forTick(300, currentTick)
        }

        if (currentKmPerH < gameSpeedInitialKmPerH)
            currentKmPerH = gameSpeedInitialKmPerH

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
        return currentDistanceCm / 100
    }


    private fun centimetersTravelledInOneTickFor(speedKmPerHour: Int): Long {
        val metersPerSecond = (speedKmPerHour * 1000) / 3600.0
        val centimetersPerTick = (metersPerSecond * 100) / 60.0
        return round(centimetersPerTick).toLong()
    }

    private fun forTick(each: Int, currentTick: Long): Int {
        return if (currentTick % each == 0L) {
            1
        } else {
            0
        }
    }

}
