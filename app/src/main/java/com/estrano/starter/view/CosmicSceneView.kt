package com.estrano.starter.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*

class CosmicSceneView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.parseColor("#22FFFFFF")
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#12FFFFFF")
        strokeWidth = 1f
    }
    private val hazePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val px = FloatArray(PARTICLE_COUNT)
    private val py = FloatArray(PARTICLE_COUNT)
    private val pr = FloatArray(PARTICLE_COUNT)
    private val speed = FloatArray(PARTICLE_COUNT)
    private val drift = FloatArray(PARTICLE_COUNT)
    
    private var phase = 0f
    private var seeded = false

    init {
        startLoop()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val random = Random(2026L)
        for (i in 0 until PARTICLE_COUNT) {
            px[i] = random.nextFloat() * w
            py[i] = random.nextFloat() * h
            pr[i] = 1.5f + random.nextFloat() * 5f
            speed[i] = 0.2f + random.nextFloat() * 0.9f
            drift[i] = -18f + random.nextFloat() * 36f
        }
        seeded = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width
        val h = height
        if (w == 0 || h == 0) return

        drawAuroraHaze(canvas, w, h)
        drawPerspectiveGrid(canvas, w, h)
        drawNebulaOrbs(canvas, w, h)
        drawRings(canvas, w, h)
        if (seeded) drawParticles(canvas, w, h)
    }

    private fun drawAuroraHaze(canvas: Canvas, w: Int, h: Int) {
        val path = Path().apply {
            moveTo(0f, h * 0.22f)
            cubicTo(w * 0.22f, h * 0.12f, w * 0.46f, h * 0.28f, w * 0.72f, h * 0.18f)
            cubicTo(w * 0.85f, h * 0.14f, w * 0.95f, h * 0.18f, w.toFloat(), h * 0.10f)
            lineTo(w.toFloat(), 0f)
            lineTo(0f, 0f)
            close()
        }
        hazePaint.shader = LinearGradient(
            0f, 0f, w.toFloat(), h * 0.35f,
            intArrayOf(Color.parseColor("#223C7BFF"), Color.parseColor("#1C57E8C7"), Color.TRANSPARENT),
            floatArrayOf(0f, 0.55f, 1f), Shader.TileMode.CLAMP
        )
        canvas.drawPath(path, hazePaint)
    }

    private fun drawPerspectiveGrid(canvas: Canvas, w: Int, h: Int) {
        val horizon = h * 0.68f
        for (i in 0 until 14) {
            val y = horizon + i * ((h - horizon) / 13f)
            canvas.drawLine(0f, y, w.toFloat(), y, linePaint)
        }
        for (i in -6..6) {
            val x = w / 2f + i * (w / 10f)
            canvas.drawLine(x, h.toFloat(), w / 2f, horizon, linePaint)
        }
    }

    private fun drawNebulaOrbs(canvas: Canvas, w: Int, h: Int) {
        val data = arrayOf(
            floatArrayOf(w * 0.18f, h * 0.20f, w * 0.22f, Color.parseColor("#443DA3FF").toFloat()),
            floatArrayOf(w * 0.84f, h * 0.26f, w * 0.18f, Color.parseColor("#4447FFD7").toFloat()),
            floatArrayOf(w * 0.52f, h * 0.14f, w * 0.16f, Color.parseColor("#44FF5EA8").toFloat())
        )
        for (d in data) {
            glowPaint.shader = RadialGradient(d[0], d[1], d[2], d[3].toInt(), Color.TRANSPARENT, Shader.TileMode.CLAMP)
            canvas.drawCircle(d[0], d[1], d[2], glowPaint)
        }
    }

    private fun drawRings(canvas: Canvas, w: Int, h: Int) {
        val cx = w * 0.78f
        val cy = h * 0.24f
        val radius = w * 0.16f
        val rect = RectF(cx - radius, cy - radius * 0.48f, cx + radius, cy + radius * 0.48f)
        canvas.save()
        canvas.rotate(phase * 20f, cx, cy)
        canvas.drawOval(rect, ringPaint)
        rect.inset(radius * 0.18f, radius * 0.08f)
        canvas.drawOval(rect, ringPaint)
        canvas.restore()
    }

    private fun drawParticles(canvas: Canvas, w: Int, h: Int) {
        for (i in 0 until PARTICLE_COUNT) {
            val x = px[i] + Math.sin((phase * speed[i] * 6.28f + i).toDouble()).toFloat() * drift[i]
            val y = py[i] + Math.cos((phase * speed[i] * 4.28f + i).toDouble()).toFloat() * 16f
            val alpha = 80 + (Math.sin((phase * 6.28f + i).toDouble()) + 1.0).toFloat() * 60f
            particlePaint.color = Color.argb(alpha.toInt(), 255, 255, 255)
            canvas.drawCircle(x, y, pr[i], particlePaint)
        }
    }

    private fun startLoop() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 10000L
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animation ->
                phase = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    companion object {
        private const val PARTICLE_COUNT = 42
    }
}
