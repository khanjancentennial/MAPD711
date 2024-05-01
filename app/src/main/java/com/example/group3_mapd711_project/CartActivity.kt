package com.example.group3_mapd711_project

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.group3_mapd711_project.model.ItemCartList
import com.example.group3_mapd711_project.model.ItemList
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageButton
    private lateinit var totalPriceTextView: TextView
    private lateinit var confirmOrderButton: Button

    private var total:Int = 0

    private val firestoreDB = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sharedPreferences = getSharedPreferences("projectUserPrefs", MODE_PRIVATE)

        supportActionBar?.hide()
        recyclerView = findViewById(R.id.recyclerViewCart)
        backButton = findViewById(R.id.backButton)
        totalPriceTextView = findViewById(R.id.productPriceTextViewCart)
        confirmOrderButton = findViewById(R.id.confirmOrderButton)

        val backImageStream = assets.open("left-arrow.png")
        val bitmap = BitmapFactory.decodeStream(backImageStream)
        backButton.setImageBitmap(bitmap)

        loadItems()

        backButton.setOnClickListener{
            finish()
        }

    }

    private fun initRecyclerview() {
        recyclerView.layoutManager = LinearLayoutManager(this@CartActivity)
    }
    private fun loadItems() {
        CoroutineScope(Dispatchers.IO).launch {
            initRecyclerview()
            try {
                val itemList = mutableListOf<ItemCartList>()
                val emailId = sharedPreferences.getString("userName", "")

                // Fetch data from Firestore collection named "products"
                val querySnapshot = firestoreDB.collection("Users").document(emailId.toString()).collection("cartItems").get().await()

                // Iterate through each document in the collection
                for (document in querySnapshot.documents) {

                    val name = document.getString("productName") ?: ""
                    val description = document.getString("productDescription") ?: ""
                    val storage = document.getString("productStorage") ?: ""
                    val price = (document.get("productPrice") as? String)?.toInt() ?: 0
                    val imageUrl = document.getString("productImage") ?: ""
                    val color = document.getString("productColor") ?: ""
                    val qty = (document.getString("Qty")as?String)?.toInt() ?: 0
                    val totalPrice = (document.getString("totalPrice")as?String)?.toInt() ?: 0

                    val docId = document.id

                    // Set a click listener on your button


                    total += totalPrice
                    totalPriceTextView.text = "$ ${total.toString()}"

                    Log.e("print","$price")

                    // Create ItemList objects and add them to the list
                    val item = ItemCartList(docId,emailId.toString(),name, description,color,storage, price,qty,totalPrice ,imageUrl)
                    itemList.add(item)
                }

                // Update the RecyclerView on the main thread
                launch(Dispatchers.Main) {
                    updateRecyclerView(itemList)
                }
                confirmOrderButton.isEnabled = total>0
                confirmOrderButton.setOnClickListener{
                    confirmOrder(itemList)
                }

            } catch (e: Exception) {
                // Handle exceptions here
                e.printStackTrace()
            }

        }
    }
    private fun updateRecyclerView(itemList: List<ItemCartList>) {
        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this@CartActivity)

        // Create an adapter and pass the retrieved data
        val adapter = ItemCartViewAdaptor(itemList)
        recyclerView.adapter = adapter
    }

    private fun confirmOrder(itemList: List<ItemCartList>) {


        for (item in itemList) {
            val productMap = hashMapOf(
                "productName" to item.title,
                "productDescription" to item.description,
                "productStorage" to item.storage,
                "productColor" to item.color,
                "productPrice" to item.price.toString(),
                "productImageUrl" to item.productImage,
                "customerEmail" to item.email,
                "Qty" to item.qty.toString(),
                "orderStatus" to "In-Process"
            )


            firestoreDB.collection("allOrders")
                .add(productMap)
                .addOnSuccessListener {
                    // Handle success
                    Toast.makeText(this, "Product Added successfully: ", Toast.LENGTH_SHORT).show()
                    val documentToDeleteRef = firestoreDB.collection("Users")
                        .document(item.email.toString())
                        .collection("cartItems")
                        .document(item.id)
                    documentToDeleteRef.delete()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Action Failed: ${e.message} ", Toast.LENGTH_SHORT).show()

                }
        }
    }

}
