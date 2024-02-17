package com.roman.flappy.game.models

import com.roman.flappy.game.tools.FlappyBatteryController
import com.roman.flappy.game.tools.FlappyBatteryControllerOne
import com.roman.flappy.game.tools.FlappyGameSpeedController
import com.roman.flappy.game.tools.FlappyGameSpeedControllerDecreasing

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

data class FlappyGameArgs(
    val gameSpeedController: FlappyGameSpeedController = FlappyGameSpeedControllerDecreasing(),
    val gameBatteryController: FlappyBatteryController = FlappyBatteryControllerOne(),
    val gameControl: FlappyGameControl = FlappyGameControl.SENSOR
)
