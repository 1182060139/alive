package com.example.dontstarveclone

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.*

class GameSurfaceView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var gameThread: Thread? = null
    private var running = false
    private val paint = Paint()
    
    val player = Player()
    val world = World()
    val inventory = Inventory()
    val timeSystem = TimeSystem()
    val ui = GameUI()
    
    var screenW = 0f
    var screenH = 0f
    private var lastUpdateTime = System.currentTimeMillis()
    
    // 触摸状态
    private var touchX = 0f
    private var touchY = 0f
    private var touching = false

    init {
        holder.addCallback(this)
        isFocusable = true
        world.generate(player)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        screenW = width.toFloat()
        screenH = height.toFloat()
        player.x = screenW / 2
        player.y = screenH / 2
        resume()
    }
    override fun surfaceDestroyed(holder: SurfaceHolder) { pause() }

    fun resume() {
        if (running) return
        running = true
        gameThread = Thread {
            while (running) {
                val now = System.currentTimeMillis()
                val delta = (now - lastUpdateTime) / 1000f
                lastUpdateTime = now
                update(delta)
                draw()
                try { Thread.sleep(16) } catch (_: InterruptedException) { break }
            }
        }
        gameThread?.start()
    }

    fun pause() {
        running = false
        gameThread?.join()
        gameThread = null
    }

    private fun update(delta: Float) {
        timeSystem.update(delta)
        player.update(delta, this, touching, touchX, touchY)
        world.update(this)
        inventory.update(delta)
    }

    private fun draw() {
        val canvas: Canvas = holder.lockCanvas() ?: return
        canvas.drawColor(Color.rgb(34, 139, 34)) // 草地背景
        
        // 夜晚暗化
        if (timeSystem.isNight) {
            paint.color = Color.argb(150, 0, 0, 50)
            canvas.drawRect(0f, 0f, screenW, screenH, paint)
        }
        
        world.draw(canvas, paint, this)
        player.draw(canvas, paint)
        ui.draw(canvas, paint, this)
        
        holder.unlockCanvasAndPost(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touching = event.action != MotionEvent.ACTION_UP
        touchX = event.x
        touchY = event.y
        
        // 传递给UI优先处理按钮
        if (ui.onTouch(event, this)) return true
        
        return true
    }
}