package com.example.group3_mapd711_project

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.group3_mapd711_project.model.ItemCartList
import com.example.group3_mapd711_project.model.PreviousOrders
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ViewPreviousOrders : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageButton
    private lateinit var sharedPreferences: SharedPreferences
    private val firestoreDB = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_previous_orders)


        supportActionBar?.hide()
        recyclerView = findViewById(R.id.recyclerViewPreviousOrders)
        backButton = findViewById(R.id.previousOrdersBackButton)

        sharedPreferences = getSharedPreferences("projectUserPrefs", MODE_PRIVATE)

        val backImageStream = assets.open("left-arrow.png")
        val bitmap = BitmapFactory.decodeStream(backImageStream)
        backButton.setImageBitmap(bitmap)

        loadItems()

        backButton.setOnClickListener{
            finish()
        }
    }
    private fun initRecyclerview() {
        recyclerView.layoutManager = LinearLayoutManager(this@ViewPreviousOrders)
    }
    private fun loadItems() {
        CoroutineScope(Dispatchers.IO).launch {
            initRecyclerview()
            try {
                val itemList = mutableListOf<PreviousOrders>()
                val emailId = sharedPreferences.getString("userName", "")

                // Fetch data from Firestore collection named "products"
                val querySnapshot = firestoreDB.collection("allOrders").get().await()

                // Iterate through each document in the collection
                for (document in querySnapshot.documents) {

                    val name = document.getString("productName") ?: ""
                    val description = document.getString("productDescription") ?: ""
                    val storage = document.getString("productStorage") ?: ""
                    val price = (document.get("productPrice") as? String)?.toInt() ?: 0
                    val imageUrl = document.getString("productImageUrl") ?: ""
                    val color = document.getString("productColor") ?: ""
                    val qty = (document.getString("Qty")as?String)?.toInt() ?: 0
                    val totalPrice = (document.getString("totalPrice")as?String)?.toInt() ?: 0
                    val email = document.getString("customerEmail") ?: ""
                    val status = document.getString("orderStatus") ?: ""

                    val docId = document.id

                    if(email == emailId.toString()){
                        // Create ItemList objects and add them to the list
                        val item = PreviousOrders(docId,emailId.toString(),name, description,color,storage, price,qty,totalPrice ,imageUrl,status)
                        itemList.add(item)
                        Log.e("items",itemList.size.toString())
                    }


                }

                // Update the RecyclerView on the main thread
                launch(Dispatchers.Main) {
                    updateRecyclerView(itemList)
                }

            } catch (e: Exception) {
                // Handle exceptions here
                e.printStackTrace()
            }

        }
    }
    private fun updateRecyclerView(itemList: List<PreviousOrders>) {
        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this@ViewPreviousOrders)

        // Create an adapter and pass the retrieved data
        val adapter = PreviousItemViewAdaptor(itemList)
        recyclerView.adapter = adapter
    }
}