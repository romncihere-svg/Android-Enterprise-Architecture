package com.estrano.starter.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*

class OrbFieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val orbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#14FFFFFF")
        strokeWidth = 1f
    }

    private val anchorsX = FloatArray(ORB_COUNT)
    private val anchorsY = FloatArray(ORB_COUNT)
    private val radius = FloatArray(ORB_COUNT)
    private val colors = intArrayOf(
        Color.parseColor("#663BC9FF"),
        Color.parseColor("#664AF6C3"),
        Color.parseColor("#66FF4D94"),
        Color.parseColor("#66897CFF"),
        Color.parseColor("#66FFE97A"),
        Color.parseColor("#6647D4FF")
    )

    private var phase = 0f
    private var initialized = false

    init {
        startAnimation()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val random = Random(42L)
        for (i in 0 until ORB_COUNT) {
            anchorsX[i] = w * (0.12f + 0.76f * random.nextFloat())
            anchorsY[i] = h * (0.10f + 0.78f * random.nextFloat())
            radius[i] = Math.min(w, h).toFloat() * (0.10f + 0.10f * random.nextFloat())
        }
        initialized = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
        if (!initialized) return

        for (i in 0 until ORB_COUNT) {
            val dx = Math.sin((phase * 6.28318f + i).toDouble()).toFloat() * 26f
            val dy = Math.cos((phase * 4.71238f + i * 0.8f).toDouble()).toFloat() * 34f
            val cx = anchorsX[i] + dx
            val cy = anchorsY[i] + dy
            
            val gradient = RadialGradient(
                cx,
                cy,
                radius[i],
                colors[i],
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            orbPaint.shader = gradient
            canvas.drawCircle(cx, cy, radius[i], orbPaint)
        }
    }

    private fun drawGrid(canvas: Canvas) {
        val width = width
        val height = height
        val gap = 120
        var x = 0
        while (x <= width) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), linePaint)
            x += gap
        }
        var y = 0
        while (y <= height) {
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), linePaint)
            y += gap
        }
    }

    private fun startAnimation() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 9000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                phase = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    companion object {
        private const val ORB_COUNT = 6
    }
}
