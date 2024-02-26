package com.roman.flappy.game

import androidx.lifecycle.LiveData
import com.roman.flappy.game.models.FlappyGameScore

/**
 *
 * Author: romanvysotsky
 * Created: 14.02.24
 */

interface FlappyGame {
    fun startGame()
    fun stopGame()

    fun getScore(): LiveData<FlappyGameScore>
}
