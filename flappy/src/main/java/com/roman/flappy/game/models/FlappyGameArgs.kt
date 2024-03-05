package com.roman.flappy.game.models

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

data class FlappyGameArgs(
    val gameControl: FlappyGameControl,
    val gameCar: FlappyCustomCar,
    val initialChargePercent: Int,
    val isMiles: Boolean
) {

    fun getChargePercentFactor(): Double {
        val percent = initialChargePercent
            .coerceAtLeast(0)
            .coerceAtMost(100)
        return percent / 100.0
    }

}
