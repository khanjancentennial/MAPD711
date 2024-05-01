package com.example.group3_mapd711_project

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class customerViewProductDetails : AppCompatActivity() {

    private lateinit var productName : TextView
    private lateinit var productDescription : TextView
    private lateinit var productColor : TextView
    private lateinit var productStorage : TextView
    private lateinit var productQty : TextView
    private lateinit var productPrice : TextView
    private lateinit var productTotalPrice : TextView
    private lateinit var productImage : ImageView
    private lateinit var addToCart: Button
    private lateinit var plusButton: Button
    private lateinit var minusButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var sharedPreferences: SharedPreferences


    private var quantity:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_view_product_details)

        supportActionBar?.hide()
        productName = findViewById(R.id.reviewProductName)
        productDescription = findViewById(R.id.reviewProductDescription)
        productColor = findViewById(R.id.reviewProductColor)
        productStorage = findViewById(R.id.reviewProductStorage)
        productQty = findViewById(R.id.qtyTextView)
        productPrice = findViewById(R.id.reviewProductPrice)
        productTotalPrice = findViewById(R.id.reviewTotalPrice)
        productImage = findViewById(R.id.reviewProductImage)
        addToCart = findViewById(R.id.reviewConfirmOrder)
        backButton = findViewById(R.id.reviewBackButton)
        minusButton = findViewById(R.id.buttonMinus)
        plusButton = findViewById(R.id.buttonPlus)

        sharedPreferences = getSharedPreferences("projectUserPrefs", MODE_PRIVATE)

        val priceIntent = intent.getStringExtra("productPrice")

        productName.text = intent.getStringExtra("productName")
        productDescription.text = intent.getStringExtra("productDescription")
        productColor.text = intent.getStringExtra("productColor")
        productStorage.text = intent.getStringExtra("productStorage")
        productPrice.text = "$ ${priceIntent}"


        val assetImagePath = intent.getStringExtra("productImage")
        Glide.with(this)
            .load(assetImagePath)
            .into(productImage)


        val backImageStream = assets.open("left-arrow.png")
        val bitmap = BitmapFactory.decodeStream(backImageStream)
        backButton.setImageBitmap(bitmap)
        backButton.setOnClickListener{
            finish()
        }

        plusButton.setOnClickListener {
                if (quantity < 10) {
                    quantity++
                    productQty.text = quantity.toString()

                    var price : Int = 0
                    var totalPrice : Int = 0
                    price = priceIntent!!.toInt()
                    totalPrice = quantity * price
                    productTotalPrice.text = "$ ${totalPrice.toString()}"

                }
                // Check and set visibility after incrementing the quantity
                if (quantity == 10) {
                    plusButton.isClickable = false
//                    plusButton.setBackgroundColor(Color.RED)
                }
                plusButton.isClickable = true
            }

            // Minus button click listener
            minusButton.setOnClickListener {
                if (quantity > 0) {
                    quantity--
                    productQty.text = quantity.toString()

                    var price : Int = 0
                    var totalPrice : Int = 0
                    price = priceIntent!!.toInt()
                    totalPrice = quantity * price
                    productTotalPrice.text = "$ ${totalPrice.toString()}"

                }
                // Check and set visibility after decrementing the quantity
                if (quantity == 0) {
                    minusButton.isClickable = false
                }
//                plusButton.visibility = View.VISIBLE
                minusButton.isClickable = true
            }


        addToCart.setOnClickListener{
            if(quantity == 0){
                Toast.makeText(this, "Please Select Quantity", Toast.LENGTH_SHORT).show()

            }else{
                // firestoreDB is a Firestore instance
                val firestoreDB = FirebaseFirestore.getInstance()

                // Retrieve the email address from the intent
                val userName = sharedPreferences.getString("userName", "")

                // Access the document with name and address details
                val userDocRef = firestoreDB.collection("Users").document(userName.toString())

                // Create a new subcollection for additional details
                val userDetailsCollectionRef = userDocRef.collection("cartItems")

                val price = productPrice.text.toString()
                val finalProductPrice = price.replace("$ ", "")
                val totalPrice = productTotalPrice.text.toString()
                val finalTotalPrice = totalPrice.replace("$ ", "")

                val dataToAdd = hashMapOf(
                    "productName" to productName.text.toString(),
                    "productDescription" to productDescription.text.toString(),
                    "productColor" to productColor.text.toString(),
                    "productStorage" to productStorage.text.toString(),
                    "productImage" to assetImagePath.toString(),
                    "productPrice" to finalProductPrice,
                    "Qty" to productQty.text.toString(),
                    "totalPrice" to finalTotalPrice.toString(),
                    // ... add other details you want to store
                )

                userDetailsCollectionRef.add(dataToAdd)
                    .addOnSuccessListener {
                        startActivity(Intent(this, CartActivity::class.java))                    }
                    .addOnFailureListener { e ->
                        // Handle any errors that occurred while adding the data
                    }
            }
        }


    }
}