package com.example.group3_mapd711_project

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.group3_mapd711_project.model.ItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class Home : AppCompatActivity() {
    private lateinit var btnProfile: ImageButton
    private lateinit var btnCart: ImageButton
    private lateinit var btnList: ImageButton
    private lateinit var btnLogout: ImageButton
    private lateinit var homepageHello: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var sharedPreferences: SharedPreferences
    private val firestoreDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()
        // Initialize buttons
        sharedPreferences = getSharedPreferences("projectUserPrefs", MODE_PRIVATE)

        btnProfile = findViewById(R.id.customerProfileButton)
        btnCart = findViewById(R.id.customerCartButton)
        btnList = findViewById(R.id.customerPreviousOrders)
        btnLogout = findViewById(R.id.customerLogoutButton)
        homepageHello = findViewById(R.id.homepageHello)

        recyclerView = findViewById(R.id.recyclerView)

        val profileImageStream = assets.open("user.png")
        val listImageStream = assets.open("shopping-list.png")
        val cartImageStream = assets.open("shopping-cart.png")
        val logoutImageStream = assets.open("logout.png")

        val bitmap = BitmapFactory.decodeStream(profileImageStream)
        btnProfile.setImageBitmap(bitmap)

        val bitmap1 = BitmapFactory.decodeStream(cartImageStream)
        btnCart.setImageBitmap(bitmap1)

        val bitmap2 = BitmapFactory.decodeStream(listImageStream)
        btnList.setImageBitmap(bitmap2)

        val bitmap3 = BitmapFactory.decodeStream(logoutImageStream)
        btnLogout.setImageBitmap(bitmap3)

        // Set click listener for "Profile" button
        btnProfile.setOnClickListener {
            // Redirect to the profile page
             val intent = Intent(this, ProfileActivity::class.java)
             startActivity(intent)
        }

        btnList.setOnClickListener{
            val intent = Intent(this, ViewPreviousOrders::class.java)
            startActivity(intent)
        }
        btnLogout.setOnClickListener{
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
//            val intent = Intent(this,MainActivity::class.java)
//            startActivity(intent)
            finish()
        }

        // Set click listener for "Cart" button
        btnCart.setOnClickListener {
            // Redirect to the cart page
             val intent = Intent(this, CartActivity::class.java)
             startActivity(intent)
        }

        val emailId = sharedPreferences.getString("userName", "")

        val collection1 = firestoreDB.collection("Users").document(emailId.toString()).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val fullName = documentSnapshot.getString("fullName")
                    // Check if firstName is not null and set it to the TextView
                    if (!fullName.isNullOrEmpty()) {
                        homepageHello.text = fullName
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle any errors that occurred while fetching the document
            }

        loadItems()


    }

    private fun initRecyclerview() {
        recyclerView.layoutManager = LinearLayoutManager(this@Home)
    }
    private fun loadItems() {
        CoroutineScope(Dispatchers.IO).launch {
                initRecyclerview()
            try {
                val itemList = mutableListOf<ItemList>()

                // Fetch data from Firestore collection named "products"
                val querySnapshot = firestoreDB.collection("products").get().await()

                // Iterate through each document in the collection
                for (document in querySnapshot.documents) {

                    val documentId = document.id
                    val name = document.getString("name") ?: ""
                    val description = document.getString("description") ?: ""
                    val price = (document.get("price") as? String)?.toInt() ?: 0
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val storage = document.getString("storage") ?: ""
                    val color = document.getString("color") ?: ""


                    Log.e("print","$price")

                    // Create ItemList objects and add them to the list
                    val item = ItemList(documentId,name, description, price,storage, color,imageUrl)
                    itemList.add(item)
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
    private fun updateRecyclerView(itemList: List<ItemList>) {
        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this@Home)

        // Create an adapter and pass the retrieved data
        val adapter = ItemViewAdaptor(itemList)
        recyclerView.adapter = adapter
    }
}
