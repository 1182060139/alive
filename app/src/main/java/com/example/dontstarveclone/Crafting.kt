package com.example.dontstarveclone

data class Recipe(val result: String, val ingredients: Map<String, Int>)

object Crafting {
    val recipes = listOf(
        Recipe("斧头", mapOf("树枝" to 2, "石头" to 1)),
        Recipe("营火", mapOf("木头" to 3, "石头" to 2)),
        Recipe("草帽", mapOf("草" to 4))
    )
    
    fun craft(recipe: Recipe, inv: Inventory): Boolean {
        for ((item, amount) in recipe.ingredients) {
            if (!inv.hasItem(item, amount)) return false
        }
        for ((item, amount) in recipe.ingredients) {
            inv.removeItem(item, amount)
        }
        inv.addItem(recipe.result)
        return true
    }
}