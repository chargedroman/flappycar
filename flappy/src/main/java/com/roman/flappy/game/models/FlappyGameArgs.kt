package com.roman.flappy.game.models

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

data class FlappyGameArgs(
    val gameSpeed: Int = 20,
    val gameControl: FlappyGameControl = FlappyGameControl.SENSOR
)
