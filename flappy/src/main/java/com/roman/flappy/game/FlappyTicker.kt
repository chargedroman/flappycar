package com.roman.flappy.game

import java.util.Timer
import java.util.TimerTask

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyTicker(val tick: () -> Unit) {

    companion object {
        private const val INTERVAL = 1000 / 60L // 60 times per second
    }

    private var timer: Timer? = null

    fun start() {
        timer = Timer()
        timer?.scheduleAtFixedRate(createTimerTask(), 0, INTERVAL)
    }

    fun stop() {
        timer?.cancel()
        timer = null
    }

    private fun createTimerTask(): TimerTask {
        return object : TimerTask() {
            override fun run() {
                tick.invoke()
            }
        }
    }

}
