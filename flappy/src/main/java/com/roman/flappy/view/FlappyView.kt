package com.roman.flappy.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

@SuppressLint("ClickableViewAccessibility")
class FlappyView: View, FlappyContract.View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        args = FlappyViewArgs(context, attrs)
    }

    private val args: FlappyViewArgs
    private var presenter: FlappyContract.Presenter? = null


    override fun setPresenter(presenter: FlappyContract.Presenter) {
        this.presenter = presenter
        presenter.attachView(this)
        notifyChanged()
    }

    override fun getPresenter(): FlappyContract.Presenter? {
        return presenter
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            save()
            val drawer = presenter?.getMainDrawer()
            drawer?.onDraw(this)
            restore()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        presenter?.onTouch(event)
        return true
    }

    override fun onDetachedFromWindow() {
        presenter?.detachView()
        super.onDetachedFromWindow()
    }

    override fun notifyChanged() {
        postInvalidate()
    }

}
