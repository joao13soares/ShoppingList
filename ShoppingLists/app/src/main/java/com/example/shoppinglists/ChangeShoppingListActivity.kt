package com.example.shoppinglists

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_shopping_list.*

class ChangeShoppingListActivity : AppCompatActivity() {

    var availableItems : MutableList<ItemEntry> = ArrayList<ItemEntry>()

    lateinit var shoppingList : ShoppingList

    val changeShoppingListAdapter = ChangeShoppingListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_shopping_list)

        shoppingList = intent.getSerializableExtra("changeShoppingList") as ShoppingList

        addDefaultAvailableItems()
        copyCurrentQuantities()

        supportActionBar!!.title = "Change " + shoppingList.name
        Toast.makeText(this, "Make your changes", Toast.LENGTH_LONG).show()

        listViewListEntries.adapter = changeShoppingListAdapter

        buttonSave.setOnClickListener {
            saveNewQuantitiesToShoppingList()

            // update Firebase node
            var ref = FirebaseDatabase.getInstance().getReference("ShoppingLists")
            ref.child(shoppingList.id!!).setValue(shoppingList)

            var resultIntent = Intent()
            resultIntent.putExtra("changedShoppingList", shoppingList)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    inner class ChangeShoppingListAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parente: ViewGroup?): View {
            val v = layoutInflater.inflate(R.layout.row_change_item_entry, parente, false)
            val imageViewItem = v.findViewById<ImageView>(R.id.imageViewItem)
            val textViewItemName = v.findViewById<TextView>(R.id.textViewItemName)
            val textViewItemQuantity = v.findViewById<TextView>(R.id.textViewItemQuantity)
            val buttonIncreaseItemQuantity = v.findViewById<Button>(R.id.buttonIncreaseItemQuantity)
            val buttonDecreaseItemQuantity = v.findViewById<Button>(R.id.buttonDecreaseItemQuantity)

            var availableItemEntry = getItem(position) as ItemEntry

            imageViewItem.setImageResource(availableItemEntry.item.image)
            textViewItemName.text = availableItemEntry.item.name
            textViewItemQuantity.text = availableItemEntry.quantity.toString()

            v.setOnClickListener {
                val intent = Intent(this@ChangeShoppingListActivity, ItemActivity::class.java)
                intent.putExtra("item_name", availableItemEntry.item.name)
                intent.putExtra("item_price", availableItemEntry.item.price)
                intent.putExtra("item_image", availableItemEntry.item.image)

                startActivity(intent)
            }

            buttonIncreaseItemQuantity.setOnClickListener {
                availableItemEntry.quantity++
                changeShoppingListAdapter.notifyDataSetChanged()
            }

            buttonDecreaseItemQuantity.setOnClickListener {
                if(availableItemEntry.quantity > 0 ) {
                    availableItemEntry.quantity--
                    changeShoppingListAdapter.notifyDataSetChanged()
                }
            }

            return  v
        }

        override fun getItem(position: Int): Any {
            return availableItems[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return availableItems.size
        }

    }

    fun addDefaultAvailableItems() {
        availableItems.add(ItemEntry(Item("Apple", 1.10F, R.drawable.apple), 0))
        availableItems.add(ItemEntry(Item("Pear", 1.30F, R.drawable.pear), 0))
        availableItems.add(ItemEntry(Item("Banana", 1.20F, R.drawable.banana), 0))
        availableItems.add(ItemEntry(Item("Rice", 0.70F, R.drawable.rice), 0))
        availableItems.add(ItemEntry(Item("Pasta", 0.60F, R.drawable.pasta), 0))
        availableItems.add(ItemEntry(Item("Water", 0.40F, R.drawable.water), 0))
    }

    fun copyCurrentQuantities() {
        for(availableItem in availableItems) {
            for(itemEntry in shoppingList.itemEntries) {
                // if item already existed in this shoppingList && his quantity has not been copied to availableItems
                if(availableItem.item.name == "Apple" && itemEntry.item.name == "Apple")
                    availableItem.quantity = itemEntry.quantity
                if(availableItem.item.name == "Pear" && itemEntry.item.name == "Pear")
                    availableItem.quantity = itemEntry.quantity
                if(availableItem.item.name == "Banana" && itemEntry.item.name == "Banana")
                    availableItem.quantity = itemEntry.quantity
                if(availableItem.item.name == "Rice" && itemEntry.item.name == "Rice")
                    availableItem.quantity = itemEntry.quantity
                if(availableItem.item.name == "Pasta" && itemEntry.item.name == "Pasta")
                    availableItem.quantity = itemEntry.quantity
                if(availableItem.item.name == "Water" && itemEntry.item.name == "Water")
                    availableItem.quantity = itemEntry.quantity
            }
        }
    }

    fun saveNewQuantitiesToShoppingList() {
        shoppingList.itemEntries.clear()

        for(availableItem in availableItems) {
            if(availableItem.quantity > 0)
                shoppingList.itemEntries.add(availableItem)
        }
    }
}