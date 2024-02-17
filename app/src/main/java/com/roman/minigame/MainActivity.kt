package com.roman.minigame

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.roman.flappy.game.FlappyPresenter
import com.roman.flappy.game.models.FlappyGameArgs
import com.roman.flappy.game.models.FlappyGameControl
import com.roman.flappy.game.tools.FlappyGameSpeedControllerDecreasing
import com.roman.flappy.view.FlappyView

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class MainActivity: AppCompatActivity() {

    private var presenter: FlappyPresenter? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressBar = findViewById<ProgressBar>(R.id.progress)
        progressBar.visibility = View.GONE

        val flappyView = findViewById<FlappyView>(R.id.view_flappy)

        //define game parameters
        val speedController = FlappyGameSpeedControllerDecreasing()
        val gameArgs = FlappyGameArgs(
            gameControl = FlappyGameControl.SENSOR,
            gameSpeedController = speedController
        )

        //bind presenter (which holds the game instance)
        val presenter = FlappyPresenter(applicationContext)
        val game = presenter.getGame()
        game.initGame(gameArgs)
        flappyView.setPresenter(presenter)

        //keep reference for starting/stopping game
        this.presenter = presenter

        //update score to show to the user somehow
        //as this can be called 60 times per second,
        //make sure to not do heavy stuff like allocating objects
        //(for now I'll still allocate String to print it)
        game.getScore().observe(this) {
            println("okhttp currentSpeed=${it.currentSpeedKmPerHour} travelled=${it.distanceMeters}m battery=${it.batteryStatus}")
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.getGame()?.startGame()
    }

    override fun onPause() {
        super.onPause()
        presenter?.getGame()?.stopGame()
    }

}
