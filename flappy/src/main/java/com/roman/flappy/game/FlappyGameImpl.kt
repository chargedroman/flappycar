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
import com.roman.flappy.game.drawers.FlappyObstructionDrawer
import com.roman.flappy.game.models.FlappyBatteryStatus
import com.roman.flappy.game.models.FlappyGameArgs
import com.roman.flappy.game.models.FlappyGameControl
import com.roman.flappy.game.models.FlappyGameScore
import com.roman.flappy.game.tools.FlappyBatteryControllerImpl
import com.roman.flappy.game.tools.FlappyGameSpeedControllerImpl
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

    companion object {
        const val BATTERY_MAX = 82000
    }


    //ticks 60 times each second
    private val ticker = FlappyTicker(this::tickTock)
    private var currentTick: Long = 0
    private var isGameStarted: Boolean = false

    //listens to phone sensor
    private val accelerometer = FlappyTilt(applicationContext, this::onTilt)

    //current score like distance ran
    private val gameScore = MutableLiveData<FlappyGameScore>()


    //define all the drawers and then call them in the right order in [onDraw]
    private val backgroundDrawer = FlappyBackgroundDrawer(applicationContext)
    private val laneDrawer = FlappyChargingLaneDrawer(applicationContext)
    private val coneDrawer = FlappyConeDrawer(applicationContext)
    private val obstructionDrawer = FlappyObstructionDrawer(applicationContext)
    private val carDrawer = FlappyCarDrawer(applicationContext, args.gameCar)
    private val displayDrawer = FlappyDisplayDrawer(
        applicationContext,
        args.isMiles,
        FlappyGameSpeedControllerImpl(),
        FlappyBatteryControllerImpl(FlappyBatteryStatus(
            (BATTERY_MAX * args.getChargePercentFactor()).toInt(),
            BATTERY_MAX)
        ),
    )


    override fun startGame() = synchronized(this) {
        if (isGameStarted)
            return@synchronized

        isGameStarted = true
        ticker.start()

        if (args.gameControl == FlappyGameControl.SENSOR)
            accelerometer.start()
    }

    override fun stopGame() = synchronized(this) {
        if (isGameStarted.not())
            return@synchronized

        isGameStarted = false
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
            carDrawer.onTilt(x, y)
        }
    }


    override fun onDraw(canvas: Canvas) = synchronized(this) {
        backgroundDrawer.onDraw(canvas)
        laneDrawer.onDraw(canvas)
        coneDrawer.onDraw(canvas)
        obstructionDrawer.onDraw(canvas)
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
        val isCarOnObstruction = obstructionDrawer.isCollidingWith(carDrawer.getCarBounds())
        carDrawer.notifyCarOnChargingLane(isCarOnLane)
        carDrawer.notifyCarOnCone(isCarOnCone)
        carDrawer.notifyCarOnObstruction(isCarOnObstruction)

        backgroundDrawer.tickTock(currentKmPerH)
        laneDrawer.tickTock(currentKmPerH)
        coneDrawer.tickTock(currentKmPerH)
        obstructionDrawer.tickTock(currentKmPerH)
        displayDrawer.tickTock(currentTick, currentKmPerH, isCarOnLane, isCarOnCone, isCarOnObstruction)

        val score = displayDrawer.getGameScore()
        coneDrawer.updateAmountOfCones(score.distanceMeters)
        obstructionDrawer.updateAmountOfObstructions(score.distanceMeters)
        laneDrawer.udpateAmountOfLanes(score.currentSpeedKmPerHour)

        gameScore.postValue(score)
    }


    private fun checkIfGameOver() {
        if (displayDrawer.getGameScore().isGameOver())
            stopGame()
    }


}
