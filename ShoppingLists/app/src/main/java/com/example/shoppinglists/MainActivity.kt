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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row_shopping_list.*

class MainActivity : AppCompatActivity() {

    var shoppingLists : MutableList<ShoppingList> = ArrayList<ShoppingList>()

    lateinit var ref : DatabaseReference

    var shoppingListsAdapter = ShoppingListsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.title = "Shopping Lists"
        Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show()

        // load all ShoppingLists from Firebase
        ref = FirebaseDatabase.getInstance().getReference("ShoppingLists")
        readingFirebaseData()

        listViewShoppingLists.adapter = shoppingListsAdapter
    }

    inner class ShoppingListsAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parente: ViewGroup?): View {
            val v = layoutInflater.inflate(R.layout.row_shopping_list, parente, false)
            val imageViewShoppingList = v.findViewById<ImageView>(R.id.imageViewShoppingList)
            val textViewShoppingListName = v.findViewById<TextView>(R.id.textViewShoppingListName)
            val textViewShoppingListNumEntries = v.findViewById<TextView>(R.id.textViewShoppingListNumEntries)
            val textViewShoppingListCost = v.findViewById<TextView>(R.id.textViewShoppingListCost)
            val buttonRemoveShoppingList = v.findViewById<TextView>(R.id.buttonRemoveShoppingList)

            var currentShoppingList = getItem(position) as ShoppingList

            imageViewShoppingList.setImageResource(R.drawable.shopping_lists_icon)
            textViewShoppingListName.text = currentShoppingList.name
            textViewShoppingListNumEntries.text = currentShoppingList.itemEntries.size.toString() + " item entries"
            textViewShoppingListCost.text = currentShoppingList.cost().toString() + "€"

            v.setOnClickListener {
                val intent = Intent(this@MainActivity, ShoppingListActivity::class.java)
                intent.putExtra("currentShoppingList", currentShoppingList)

                startActivityForResult(intent, 2)
            }

            buttonRemoveShoppingList.setOnClickListener {
                // remove node from Firebase
                var ref = FirebaseDatabase.getInstance().getReference("ShoppingLists")
                ref.child(shoppingLists[position].id!!).removeValue()

                shoppingLists.removeAt(position)
                shoppingListsAdapter.notifyDataSetChanged()
            }

            return  v
        }

        override fun getItem(position: Int): Any {
            return shoppingLists[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return shoppingLists.size
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                val newShoppingListName = data?.getStringExtra("newShoppingList_name")

                // save ShoppingList on Firebase
                val newShoppingListId = ref.push().key

                var newShoppingList = ShoppingList(newShoppingListId!!, newShoppingListName)

                shoppingLists.add(newShoppingList)

                ref.child(newShoppingListId).setValue(newShoppingList).addOnCompleteListener {
                    Toast.makeText(this, newShoppingListName + " created", Toast.LENGTH_LONG).show()
                }
                shoppingListsAdapter.notifyDataSetChanged()
            }
            if(resultCode == Activity.RESULT_CANCELED){

            }
        }

        if(requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                var changedShoppingList = data?.getSerializableExtra( "savedShoppingList") as ShoppingList

                for(id in 0 until shoppingLists.size)
                    if(shoppingLists[id].id == changedShoppingList.id)
                        shoppingLists[id] = changedShoppingList
                shoppingListsAdapter.notifyDataSetChanged()

                Toast.makeText(this, changedShoppingList.name + " saved", Toast.LENGTH_LONG).show()
            }
            if(resultCode == Activity.RESULT_CANCELED){

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemAdd -> {

                val intent = Intent(this@MainActivity, NewShoppingListNameActivity::class.java)

                startActivityForResult(intent,1)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun readingFirebaseData(){

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){

                    for (h in p0.children){

                        // Bool to check if the node is'nt already stored in the list
                        var exists : Boolean = false

                        // Gets current node Bouquet
                        var shoppingListInCurrentNode = h.getValue(ShoppingList::class.java)


                        // Checks the list for a bouquet with the same id
                        for(sl in shoppingLists){

                            // If it finds one it changes the bool variable to true
                            if(sl.id == shoppingListInCurrentNode!!.id ) {

                                exists = true
                                break

                            }

                        }

                        // If the bouquet in the current node of the firebase is'nt stored in the list it stores it
                        if(exists == false){

                            shoppingLists.add(shoppingListInCurrentNode!!)

                        }


                    }

                    // Updates adaptar
                    shoppingListsAdapter.notifyDataSetChanged()




                }

            }

        })
    }
}