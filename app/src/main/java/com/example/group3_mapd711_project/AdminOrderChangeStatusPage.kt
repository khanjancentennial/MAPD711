package com.example.group3_mapd711_project

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class AdminOrderChangeStatusPage : AppCompatActivity() {
    private lateinit var productName: TextView
    private lateinit var qty: TextView
    private lateinit var image: ImageView
    private lateinit var orderDate: TextView
    private lateinit var status: TextView
    private lateinit var changeButton: Button

    private lateinit var backButton: ImageButton

    private val firestoreDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_order_change_status_page)

        supportActionBar?.hide()
        productName = findViewById(R.id.adminChangeStatusProductName)
        qty = findViewById(R.id.adminChangeStatusProductQty)
        image = findViewById(R.id.adminChangeStatusImage)
        status = findViewById(R.id.adminChangeStatusOrderStatus)
        changeButton = findViewById(R.id.adminChangeStatusButton)

        backButton = findViewById(R.id.adminChangeStatusBackButton)

        val backImageStream = assets.open("left-arrow.png")
        val bitmap = BitmapFactory.decodeStream(backImageStream)
        backButton.setImageBitmap(bitmap)

        backButton.setOnClickListener{
            finish()
        }
        productName.text = intent.getStringExtra("productName")
        qty.text = intent.getStringExtra("productQty")
        status.text = intent.getStringExtra("orderStatus")

        val id = intent.getStringExtra("documentId")

        val assetImagePath = intent.getStringExtra("productImage")
        Glide.with(this)
            .load(assetImagePath)
            .into(image)


        changeButton.setOnClickListener{
            val docRef = firestoreDB.collection("allOrders").document(id.toString())

                // Create a map with the fields which will update
            val updates = hashMapOf(
                "orderStatus" to "Delivered",

            )

            docRef.update(updates as Map<String, Any>)
                .addOnSuccessListener {
                    val intent = Intent(this, AdminHome::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                   this.startActivity(intent)

                }
                .addOnFailureListener { e ->
                    // Handle any errors that occurred while deleting the document
                }
        }

    }
}