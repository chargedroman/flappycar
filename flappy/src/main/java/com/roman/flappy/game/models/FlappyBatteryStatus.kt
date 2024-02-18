package com.roman.flappy.game.models

/**
 *
 * Author: romanvysotsky
 * Created: 17.02.24
 */

data class FlappyBatteryStatus(
    var currentChargeWh: Int,
    val maxChargeWh: Int,
) {
    fun getPercentage(): Int {
        return currentChargeWh / (maxChargeWh / 100)
    }

    fun isEmpty(): Boolean {
        return currentChargeWh <= 0
    }
}
