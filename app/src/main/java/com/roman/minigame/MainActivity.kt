package com.roman.minigame

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.roman.flappy.game.FlappyPresenter
import com.roman.flappy.view.FlappyView

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class MainActivity: AppCompatActivity() {

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressBar = findViewById<ProgressBar>(R.id.progress)
        progressBar.visibility = View.GONE

        val flappyView = findViewById<FlappyView>(R.id.view_flappy)

        val presenter = FlappyPresenter(applicationContext)
        flappyView.setPresenter(presenter)
        presenter.onStart()
    }


}
