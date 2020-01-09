package com.example.shoppinglists

import java.io.Serializable

class Item : Serializable{
    var name : String? = null
    var price : Float = 0.00F
    var image : Int = R.drawable.none

    constructor(name: String?, price: Float, image : Int) {
        this.name = name
        this.price = price
        this.image = image
    }

    constructor()
}