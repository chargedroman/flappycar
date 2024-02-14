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

    private val timer = Timer()

    private val tickTask = object : TimerTask() {
        override fun run() {
            tick.invoke()
        }
    }

    fun start() {
        timer.scheduleAtFixedRate(tickTask, 0, INTERVAL)
    }

    fun stop() {
        timer.cancel()
    }

}
