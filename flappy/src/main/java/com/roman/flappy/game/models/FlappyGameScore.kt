package com.roman.flappy.game.models

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

data class FlappyGameScore(
    var distanceMeters: Long,
    var currentSpeedKmPerHour: Int,
    var batteryStatus: FlappyBatteryStatus?,
) {

    companion object{
        const val MI_PER_HOUR_FACTOR = 0.621371
        const val FEET_FACTOR = 3.28084
    }

    /**
     * game is over when battery is low and speed is 0;
     * you could theoretically reach the next thing with empty battery and get power again:)
     */
    fun isGameOver(): Boolean {
        return batteryStatus?.isEmpty() == true && currentSpeedKmPerHour == 0
    }


    fun getDistanceText(isMiles: Boolean): String {
        return if (isMiles) {
            val distance = distanceMeters * FEET_FACTOR
            val formatted = String.format("%.0f", distance)
            "$formatted f"
        } else {
            "$distanceMeters m"
        }
    }

    fun getSpeedText(isMiles: Boolean): String {
        return if (isMiles) {
            val distance = currentSpeedKmPerHour * MI_PER_HOUR_FACTOR
            String.format("%.0f", distance) + " mi/h"
        } else {
            "$currentSpeedKmPerHour km/h"
        }
    }


}
