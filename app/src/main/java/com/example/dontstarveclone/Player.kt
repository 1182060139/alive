package com.example.dontstarveclone

import android.graphics.*
import kotlin.math.*

class Player {
    var x = 0f
    var y = 0f
    var targetX = -1f
    var targetY = -1f
    val speed = 120f

    var health = 100f
    var hunger = 100f
    var sanity = 100f

    // 动画状态
    private var state = State.IDLE
    private var animTimer = 0f
    private var currentFrame = 0
    private var frameWidth = 0
    private var frameHeight = 0
    
    // ----- 根据你的精灵表修改这些参数 -----
    private val totalFramesPerRow = 4   // 每行总帧数（站立+走路）
    private val idleFrames = 1          // 站立帧数（通常只有第一帧）
    private val walkFrames = 3          // 走路帧数
    private val frameDuration = 0.15f   // 每帧时间（秒）
    
    // 方向映射：根据你的图片行顺序
    // 假设第0行=下, 第1行=左, 第2行=右, 第3行=上
    private val dirToRow = mapOf(
        Direction.DOWN to 0,
        Direction.LEFT to 1,
        Direction.RIGHT to 2,
        Direction.UP to 3
    )
    // -----------------------------------

    private var currentDir = Direction.DOWN

    enum class State { IDLE, WALK }
    enum class Direction { DOWN, LEFT, RIGHT, UP }

    fun update(delta: Float, view: GameSurfaceView, touching: Boolean, touchX: Float, touchY: Float) {
        if (view.screenW <= 0f || view.screenH <= 0f) return

        if (touching && !view.ui.isOverControl(touchX, touchY, view)) {
            targetX = touchX
            targetY = touchY
        }

        val isMoving = (targetX >= 0 && targetY >= 0)

        if (isMoving) {
            val dx = targetX - x
            val dy = targetY - y
            val dist = sqrt(dx * dx + dy * dy)
            if (dist < 8f) {
                targetX = -1f
                targetY = -1f
            } else {
                x += dx / dist * speed * delta
                y += dy / dist * speed * delta

                // 根据移动方向更新方向
                currentDir = when {
                    abs(dx) > abs(dy) -> if (dx > 0) Direction.RIGHT else Direction.LEFT
                    else -> if (dy > 0) Direction.DOWN else Direction.UP
                }
            }
        }

        x = x.coerceIn(20f, view.screenW - 20f)
        y = y.coerceIn(80f, view.screenH - 20f)

        // 动画状态更新
        state = if (isMoving) State.WALK else State.IDLE
        animTimer += delta
        val maxFrames = if (state == State.WALK) walkFrames else idleFrames
        val frameTime = if (state == State.WALK) frameDuration else 0.3f
        if (animTimer >= frameTime) {
            animTimer -= frameTime
            currentFrame = (currentFrame + 1) % maxFrames
        }

        hunger -= 2f * delta
        if (hunger <= 0) {
            hunger = 0f
            health -= 5f * delta
        }
        if (health <= 0) health = 0f
    }

    fun draw(canvas: Canvas, paint: Paint, bitmap: Bitmap) {
        if (frameWidth == 0) {
            frameWidth = bitmap.width / totalFramesPerRow
            frameHeight = bitmap.height / 4  // 4个方向行
        }

        // 根据方向和状态选择帧
        val frameIndex = if (state == State.WALK) idleFrames + currentFrame else 0
        val row = dirToRow[currentDir] ?: 0

        val srcX = frameIndex * frameWidth
        val srcY = row * frameHeight
        val srcRect = Rect(srcX, srcY, srcX + frameWidth, srcY + frameHeight)

        // 绘制位置（脚底对齐y坐标）
        val destX = x - frameWidth / 2f
        val destY = y - frameHeight + 10f

        // 阴影
        paint.setShadowLayer(6f, 3f, 3f, Color.argb(120, 0, 0, 0))
        canvas.drawBitmap(bitmap, srcRect,
            RectF(destX, destY, destX + frameWidth, destY + frameHeight), paint)
        paint.setShadowLayer(0f, 0f, 0f, 0)
    }
}