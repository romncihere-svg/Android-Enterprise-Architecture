package com.estrano.starter.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.sqrt
import kotlin.random.Random

class NeuralNetworkView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val nodes = mutableListOf<Node>()
    private val paintNode = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#00E5FF")
        style = Paint.Style.FILL
    }
    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f
    }
    private val random = Random(System.currentTimeMillis())

    private var w = 0f
    private var h = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w.toFloat()
        this.h = h.toFloat()
        initNodes()
    }

    private fun initNodes() {
        nodes.clear()
        val nodeCount = 60
        for (i in 0 until nodeCount) {
            nodes.add(Node(
                random.nextFloat() * w,
                random.nextFloat() * h,
                (random.nextFloat() - 0.5f) * 2f,
                (random.nextFloat() - 0.5f) * 2f
            ))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#020408"))

        // Update and draw lines
        for (i in nodes.indices) {
            val nodeA = nodes[i]
            nodeA.update(w, h)

            for (j in i + 1 until nodes.size) {
                val nodeB = nodes[j]
                val dist = distance(nodeA, nodeB)
                if (dist < 300f) {
                    val alpha = ((1f - dist / 300f) * 80).toInt()
                    paintLine.setARGB(alpha, 0, 229, 255)
                    canvas.drawLine(nodeA.x, nodeA.y, nodeB.x, nodeB.y, paintLine)
                }
            }
        }

        // Draw nodes
        for (node in nodes) {
            canvas.drawCircle(node.x, node.y, 3f, paintNode)
        }

        invalidate()
    }

    private fun distance(a: Node, b: Node): Float {
        return sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))
    }

    private class Node(var x: Float, var y: Float, var vx: Float, var vy: Float) {
        fun update(w: Float, h: Float) {
            x += vx
            y += vy
            if (x < 0 || x > w) vx *= -1
            if (y < 0 || y > h) vy *= -1
        }
    }
}
