package com.example.shoppinglists

import java.io.Serializable

class ShoppingList : Serializable {
    var id : String? = null
    var name : String? = null
    var itemEntries : MutableList<ItemEntry> = ArrayList<ItemEntry>()

    constructor(id : String, name: String?) {
        this.id = id
        this.name = name
    }

    constructor()

    fun cost() : Float {
        var cost : Float = 0.00F

        for (itemEntry in itemEntries) {
            cost += itemEntry.item.price * itemEntry.quantity;
        }

        return cost
    }
}