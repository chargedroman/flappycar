package com.roman.flappy.game

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyBoard(
    val width: Int = DEFAULT_WIDTH,
    val height: Int = DEFAULT_HEIGHT,
) {

    companion object {
        const val DEFAULT_WIDTH = 400
        const val DEFAULT_HEIGHT = 1200
    }

    /*
    The board is a bit like this and then is drawn (could be cut off at the top) relative to the screen size
    _ _ _ _ _
    _ _ _ _ _
    _ _ _ _ _
    _ _ _ _ _
    _ _ _ _ _
    _ _ _ _ _
    _ _ _ _ _
    _ _ _ _ _
    */

}
