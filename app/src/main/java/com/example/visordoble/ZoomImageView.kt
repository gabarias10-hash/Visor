package com.example.visordoble

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView

class ZoomImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatImageView(context, attrs) {
    private val matrix = Matrix()
    private val scaleDetector: ScaleGestureDetector
    private var scaleFactor = 1f
    private var lastX = 0f
    private var lastY = 0f
    private var posX = 0f
    private var posY = 0f

    init {
        scaleType = ScaleType.MATRIX
        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(0.5f, 5f)
                updateMatrix()
                return true
            }
        })
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener(){
        override fun onDoubleTap(e: MotionEvent): Boolean {
            scaleFactor = 1f; posX = 0f; posY = 0f
            updateMatrix()
            return true
        }
    })

    private fun updateMatrix() {
        matrix.reset()
        matrix.postScale(scaleFactor, scaleFactor, width/2f, height/2f)
        matrix.postTranslate(posX, posY)
        imageMatrix = matrix
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        scaleDetector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { lastX = event.x; lastY = event.y }
            MotionEvent.ACTION_MOVE -> {
                if (!scaleDetector.isInProgress && scaleFactor > 1f) {
                    posX += event.x - lastX
                    posY += event.y - lastY
                    updateMatrix()
                    lastX = event.x; lastY = event.y
                }
            }
        }
        return true
    }
}
