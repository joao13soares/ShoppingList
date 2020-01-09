package com.example.shoppinglists

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_shopping_list.*

class ShoppingListActivity : AppCompatActivity() {

    lateinit var shoppingList : ShoppingList

    val shoppingListAdapter = ShoppingListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        shoppingList = intent.getSerializableExtra("currentShoppingList") as ShoppingList

        supportActionBar!!.title = shoppingList.name

        listViewListEntries.adapter = shoppingListAdapter

        buttonChange.setOnClickListener {
            val intent = Intent(this@ShoppingListActivity, ChangeShoppingListActivity::class.java)
            intent.putExtra("changeShoppingList", shoppingList)

            startActivityForResult(intent, 1)
        }

        buttonSave.setOnClickListener {
            var resultIntent = Intent()
            resultIntent.putExtra("savedShoppingList", shoppingList)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()

        }
    }

    inner class ShoppingListAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parente: ViewGroup?): View {
            val v = layoutInflater.inflate(R.layout.row_item_entry, parente, false)
            val imageViewItem = v.findViewById<ImageView>(R.id.imageViewItem)
            val textViewItemName = v.findViewById<TextView>(R.id.textViewItemName)
            val textViewItemQuantity = v.findViewById<TextView>(R.id.textViewItemQuantity)

            var itemEntry = getItem(position) as ItemEntry

            imageViewItem.setImageResource(itemEntry.item.image)
            textViewItemName.text = itemEntry.item.name
            textViewItemQuantity.text = itemEntry.quantity.toString()

            v.setOnClickListener {
                val intent = Intent(this@ShoppingListActivity, ItemActivity::class.java)
                intent.putExtra("item_name", itemEntry.item.name)
                intent.putExtra("item_price", itemEntry.item.price)
                intent.putExtra("item_image", itemEntry.item.image)

                startActivity(intent)
            }

            return  v
        }

        override fun getItem(position: Int): Any {
            return shoppingList.itemEntries[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return shoppingList.itemEntries.size
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                this.shoppingList = data?.getSerializableExtra("changedShoppingList") as ShoppingList
                shoppingListAdapter.notifyDataSetChanged()

                Toast.makeText(this, "Click Save to register changes", Toast.LENGTH_LONG).show()
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
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
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,  shoppingList.name)
                shareIntent.putExtra(Intent.EXTRA_TEXT,  shoppingList.cost().toString())
                startActivity(Intent.createChooser(shareIntent, "Share"))

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}