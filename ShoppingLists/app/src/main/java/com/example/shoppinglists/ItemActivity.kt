package com.example.shoppinglists

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_item.*

class ItemActivity : AppCompatActivity() {

    lateinit var item : Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        var name = intent.getStringExtra("item_name")!!
        var price = intent.getFloatExtra("item_price", 0.00F)
        var image = intent.getIntExtra("item_image", R.drawable.none)

        item = Item(name, price, image)

        supportActionBar!!.title = item.name

        textViewItemName.text = item.name
        textViewItemPrice.text = item.price.toString() + "€"
        imageViewItem.setImageResource(item.image)

        imageViewItem.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/" + item.name))
            startActivity(i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_share,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemShare -> {

                val shareIntent: Intent
                shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,  this.item.name)
                shareIntent.putExtra(Intent.EXTRA_TEXT,  this.item.price.toString())
                startActivity(Intent.createChooser(shareIntent, "Share"))

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}