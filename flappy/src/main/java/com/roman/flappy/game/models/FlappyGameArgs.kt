package com.roman.flappy.game.models

import com.roman.flappy.game.tools.GameSpeedController
import com.roman.flappy.game.tools.GameSpeedControllerLinear

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

data class FlappyGameArgs(
    val gameSpeedInitialKmPerH: Int = 10,
    val gameSpeedMaxKmPerH: Int = 201,
    val gameSpeedController: GameSpeedController = GameSpeedControllerLinear(),
    val gameControl: FlappyGameControl = FlappyGameControl.SENSOR
)
