package com.example.dontstarveclone

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.sqrt
import kotlin.random.Random

data class Resource(val type: ResourceType, var x: Float, var y: Float, var quantity: Int = 1)

enum class ResourceType {
    TREE, ROCK, BERRY_BUSH
}

class World {
    val resources = mutableListOf<Resource>()
    
    fun generate(player: Player) {
        resources.clear()
        for (i in 1..15) {
            addRandomResource()
        }
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
    
    fun update(view: GameSurfaceView) {
        // 检测玩家周围采集
        val player = view.player
        val iter = resources.iterator()
        while (iter.hasNext()) {
            val res = iter.next()
            val dx = player.x - res.x
            val dy = player.y - res.y
            if (sqrt(dx * dx + dy * dy) < 40f) {
                // 自动采集？这里改为由按钮触发，所以只标记距离不做自动采集
            }
        }
    }
    
    fun collectNearest(player: Player, view: GameSurfaceView) {
        val near = resources.filter {
            val dx = player.x - it.x
            val dy = player.y - it.y
            sqrt(dx * dx + dy * dy) < 50f
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
            // 重新生成一个资源保持世界丰富
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
            paint.style = Paint.Style.FILL
            when (res.type) {
                ResourceType.TREE -> {
                    paint.color = Color.rgb(139, 69, 19)
                    canvas.drawRect(res.x - 4, res.y - 15, res.x + 4, res.y, paint)
                    paint.color = Color.rgb(34, 139, 34)
                    canvas.drawCircle(res.x, res.y - 20, 12f, paint)
                }
                ResourceType.ROCK -> {
                    paint.color = Color.GRAY
                    canvas.drawCircle(res.x, res.y, 10f, paint)
                }
                ResourceType.BERRY_BUSH -> {
                    paint.color = Color.rgb(0, 100, 0)
                    canvas.drawCircle(res.x, res.y, 8f, paint)
                    paint.color = Color.RED
                    canvas.drawCircle(res.x - 3, res.y - 8, 3f, paint)
                    canvas.drawCircle(res.x + 3, res.y - 6, 3f, paint)
                }
            }
        }
    }
}