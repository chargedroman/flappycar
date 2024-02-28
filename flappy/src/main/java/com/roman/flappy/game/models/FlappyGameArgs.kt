package com.roman.flappy.game.models

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

data class FlappyGameArgs(
    val gameControl: FlappyGameControl,
    val gameCar: FlappyCustomCar,
    val streetResource: Int,
    val isMiles: Boolean
)
