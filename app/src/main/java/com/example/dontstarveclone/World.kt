package com.example.dontstarveclone

import android.graphics.*
import kotlin.math.sqrt
import kotlin.random.Random

class World {
    val resources = mutableListOf<Resource>()
    
    fun generate(player: Player) {
        resources.clear()
        repeat(15) { addRandomResource() }
    }
    
    private fun addRandomResource() {
        resources.add(
            Resource(
                type = ResourceType.entries.random(),
                x = Random.nextFloat() * 800 + 50,
                y = Random.nextFloat() * 400 + 100
            )
        )
    }
    
    fun update(view: GameSurfaceView) { }
    
    fun collectNearest(player: Player, view: GameSurfaceView) {
        val near = resources.filter {
            val dx = player.x - it.x
            val dy = player.y - it.y
            sqrt(dx * dx + dy * dy) < 60f
        }.minByOrNull {
            val dx = player.x - it.x
            val dy = player.y - it.y
            sqrt(dx * dx + dy * dy)
        }
        if (near != null) {
            when (near.type) {
                ResourceType.TREE -> view.inventory.addItem("木头", 2)
                ResourceType.ROCK -> view.inventory.addItem("石头", 2)
                ResourceType.BERRY_BUSH -> {
                    view.inventory.addItem("浆果", 1)
                    view.player.hunger = (view.player.hunger + 10).coerceAtMost(100f)
                }
            }
            resources.remove(near)
            resources.add(
                Resource(
                    type = ResourceType.entries.random(),
                    x = Random.nextFloat() * view.screenW,
                    y = Random.nextFloat() * (view.screenH - 150) + 100
                )
            )
        }
    }
    
    fun draw(canvas: Canvas, paint: Paint, view: GameSurfaceView) {
        for (res in resources) {
            val bmp = when (res.type) {
                ResourceType.TREE -> view.treeBitmap
                ResourceType.ROCK -> view.rockBitmap
                ResourceType.BERRY_BUSH -> view.berryBushBitmap
            }
            // 绘制阴影
            paint.setShadowLayer(10f, 4f, 4f, Color.argb(100, 0, 0, 0))
            canvas.drawBitmap(bmp, res.x - bmp.width / 2f, res.y - bmp.height / 2f, paint)
        }
        paint.setShadowLayer(0f, 0f, 0f, 0)
    }
}