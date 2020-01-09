package com.example.shoppinglists

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NewShoppingListNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_shopping_list_name)

        supportActionBar!!.title = "New Shopping List"

        val editTextNewShoppingListName = findViewById<EditText>(R.id.editTextNewShoppingListName)
        val buttonNewShoppingList = findViewById<Button>(R.id.buttonAddNewShoppingList)
        val buttonCancel = findViewById<Button>(R.id.buttonCancel)

        buttonNewShoppingList.setOnClickListener {
            val newShoppingListName = editTextNewShoppingListName.text.toString().trim()

            if(newShoppingListName.isEmpty() || newShoppingListName.isBlank()) {
                editTextNewShoppingListName.error = "Please enter a name"
            }
            else if(newShoppingListName.length > 25) {
                editTextNewShoppingListName.error = "Please enter a shorter name"
            }
            else {
                val resultIntent = Intent()
                resultIntent.putExtra("newShoppingList_name", newShoppingListName)

                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        buttonCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}