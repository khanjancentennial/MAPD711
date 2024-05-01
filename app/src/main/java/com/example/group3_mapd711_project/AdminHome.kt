package com.example.group3_mapd711_project

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.group3_mapd711_project.model.PreviousOrders
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminHome : AppCompatActivity() {

    private lateinit var btnProfile: ImageButton
    private lateinit var btnAddItem: ImageButton
    private lateinit var btnLogout: ImageButton
    private lateinit var homepageHelloAdmin: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences


    private val firestoreDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        sharedPreferences = getSharedPreferences("projectUserPrefs", MODE_PRIVATE)

        btnProfile = findViewById(R.id.adminProfileButton)
        btnAddItem = findViewById(R.id.AdminAddProductsButton)
        btnLogout = findViewById(R.id.adminLogoutButton)
        homepageHelloAdmin = findViewById(R.id.homepageHelloAdmin)

        val profileImageStream = assets.open("user.png")
        val addItemImageStream = assets.open("add.png")
        val logoutImageStream = assets.open("logout.png")

        val bitmap = BitmapFactory.decodeStream(profileImageStream)
        btnProfile.setImageBitmap(bitmap)

        val bitmap1 = BitmapFactory.decodeStream(addItemImageStream)
        btnAddItem.setImageBitmap(bitmap1)

        val bitmap3 = BitmapFactory.decodeStream(logoutImageStream)
        btnLogout.setImageBitmap(bitmap3)

        btnLogout.setOnClickListener{
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
//            val intent = Intent(this,MainActivity::class.java)
//            startActivity(intent)
            finish()
        }

        btnProfile.setOnClickListener {
            // Redirect to the profile page
            val intent = Intent(this, AdminProfile::class.java)
            startActivity(intent)
        }

        btnAddItem.setOnClickListener {
            // Redirect to the profile page
            val intent = Intent(this, AdminAddItems::class.java)
            startActivity(intent)
        }

        supportActionBar?.hide()
        recyclerView = findViewById(R.id.adminViewAllOrdersRecyclerView)
        val emailId = sharedPreferences.getString("userName", "")

        val collection1 = firestoreDB.collection("Users").document(emailId.toString()).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val fullName = documentSnapshot.getString("fullName")
                    // Check if firstName is not null and set it to the TextView
                    if (!fullName.isNullOrEmpty()) {
                        homepageHelloAdmin.text = fullName
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle any errors that occurred while fetching the document
            }



        loadItems()
    }

    private fun initRecyclerview() {
        recyclerView.layoutManager = LinearLayoutManager(this@AdminHome)
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

                        // Create ItemList objects and add them to the list
                        val item = PreviousOrders(docId,emailId.toString(),name, description,color,storage, price,qty,totalPrice ,imageUrl,status)
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
    private fun updateRecyclerView(itemList: List<PreviousOrders>) {
        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this@AdminHome)

        // Create an adapter and pass the retrieved data
        val adapter = AdminViewAllOrdersAdaptor(itemList)
        recyclerView.adapter = adapter
    }
}