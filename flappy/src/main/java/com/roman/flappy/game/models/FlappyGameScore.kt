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
    /**
     * game is over when battery is low and speed is 0;
     * you could theoretically reach the next thing with empty battery and get power again:)
     */
    fun isGameOver(): Boolean {
        return batteryStatus?.isEmpty() == true && currentSpeedKmPerHour == 0
    }
}
