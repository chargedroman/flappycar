package com.roman.flappy.game

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.roman.flappy.game.drawers.FlappyBackgroundDrawer
import com.roman.flappy.game.drawers.FlappyCarDrawer
import com.roman.flappy.game.drawers.FlappyChargingLaneDrawer
import com.roman.flappy.game.drawers.FlappyConeDrawer
import com.roman.flappy.game.drawers.FlappyDisplayDrawer
import com.roman.flappy.game.models.FlappyGameArgs
import com.roman.flappy.game.models.FlappyGameControl
import com.roman.flappy.game.models.FlappyGameScore
import com.roman.flappy.game.tools.FlappyBatteryControllerOne
import com.roman.flappy.game.tools.FlappyGameSpeedControllerDecreasing
import com.roman.flappy.view.FlappyDrawer

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyGameImpl(
    applicationContext: Context,
    private val args: FlappyGameArgs,
    private val triggerRedraw: () -> Unit
): FlappyGame, FlappyDrawer {

    //ticks 60 times each second
    private val ticker = FlappyTicker(this::tickTock)
    private var currentTick: Long = 0

    //listens to phone sensor
    private val accelerometer = FlappyTilt(applicationContext, this::onTilt)

    //current score like distance ran
    private val gameScore = MutableLiveData<FlappyGameScore>()


    //define all the drawers and then call them in the right order in [onDraw]
    private val backgroundDrawer = FlappyBackgroundDrawer(applicationContext, args.streetResource)
    private val laneDrawer = FlappyChargingLaneDrawer(applicationContext)
    private val coneDrawer = FlappyConeDrawer(applicationContext)
    private val carDrawer = FlappyCarDrawer(applicationContext, args.gameCar)
    private val displayDrawer = FlappyDisplayDrawer(
        applicationContext,
        FlappyGameSpeedControllerDecreasing(),
        FlappyBatteryControllerOne(),
    )


    override fun startGame() = synchronized(this) {
        ticker.start()
        if (args.gameControl == FlappyGameControl.SENSOR)
            accelerometer.start()
    }

    override fun stopGame() = synchronized(this) {
        ticker.stop()
        accelerometer.stop()
    }

    override fun getScore(): LiveData<FlappyGameScore> {
        return gameScore
    }


    fun onTouch(event: MotionEvent) {
        if (args.gameControl == FlappyGameControl.TOUCH)
            carDrawer.onTouch(event)
    }

    private fun onTilt(x: Float, y: Float) {
        if (args.gameControl == FlappyGameControl.SENSOR) {
            val percent = displayDrawer.getCurrentSpeedPercent()
            carDrawer.onTilt(x, y, percent)
        }
    }


    override fun onDraw(canvas: Canvas) = synchronized(this) {
        backgroundDrawer.onDraw(canvas)
        laneDrawer.onDraw(canvas)
        coneDrawer.onDraw(canvas)
        carDrawer.onDraw(canvas)
        displayDrawer.onDraw(canvas)
    }


    private fun tickTock() = synchronized(this) {
        checkIfGameOver()
        onTickUpdateGameScore()
        triggerRedraw.invoke()
    }

    private fun onTickUpdateGameScore() {
        currentTick++

        val currentKmPerH = displayDrawer.getCurrentSpeed()
        val isCarOnLane = laneDrawer.isCollidingWith(carDrawer.getCarBounds())
        val isCarOnCone = coneDrawer.isCollidingWith(carDrawer.getCarBounds())
        carDrawer.notifyCarOnChargingLane(isCarOnLane)
        carDrawer.notifyCarOnCone(isCarOnCone)

        backgroundDrawer.tickTock(currentKmPerH)
        laneDrawer.tickTock(currentKmPerH)
        coneDrawer.tickTock(currentKmPerH)
        displayDrawer.tickTock(currentTick, currentKmPerH, isCarOnLane, isCarOnCone)

        val score = displayDrawer.getGameScore()
        coneDrawer.updateAmountOfCones(score.distanceMeters)
        laneDrawer.udpateAmountOfLanes(score.distanceMeters)

        gameScore.postValue(score)
    }


    private fun checkIfGameOver() {
        if (displayDrawer.getGameScore().isGameOver())
            stopGame()
    }


}
