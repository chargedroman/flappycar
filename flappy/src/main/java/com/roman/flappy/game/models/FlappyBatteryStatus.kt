package com.roman.flappy.game.models

/**
 *
 * Author: romanvysotsky
 * Created: 17.02.24
 */

data class FlappyBatteryStatus(
    var currentChargeWh: Int,
    var totalChargeWh: Int,
) {
    fun getPercentage(): Int {
        return currentChargeWh / (totalChargeWh / 100)
    }

    fun isEmpty(): Boolean {
        return currentChargeWh <= 0
    }
}
