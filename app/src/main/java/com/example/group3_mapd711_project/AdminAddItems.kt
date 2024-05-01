package com.example.group3_mapd711_project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class AdminAddItems : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var productName: EditText
    private lateinit var productDescription: EditText
    private lateinit var productStorage: EditText
    private lateinit var productColor: EditText
    private lateinit var productPrice: EditText
    private lateinit var productImageView: ImageView

    private lateinit var productImage: Button
    private lateinit var addToDBButton: Button

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImage: Bitmap? = null

    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_items)
        supportActionBar?.hide()

        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        btnBack = findViewById(R.id.AddItemBackButtonAdmin)
        productName = findViewById(R.id.addProductNameAdmin)
        productDescription = findViewById(R.id.addProductDescriptionAdmin)
        productStorage = findViewById(R.id.addProductStorageAdmin)
        productColor = findViewById(R.id.addProductColorAdmin)
        productPrice = findViewById(R.id.addProductPriceAdmin)

        productImage = findViewById(R.id.chooseImageBtnAdmin)
        addToDBButton = findViewById(R.id.addProductInDbAdmin)
        productImageView = findViewById(R.id.addProductImageViewAdmin)

        val backImageStream = assets.open("left-arrow.png")

        val bitmap = BitmapFactory.decodeStream(backImageStream)
        btnBack.setImageBitmap(bitmap)

        btnBack.setOnClickListener {
            // Redirect to the profile page
            finish()
        }

        productImage.setOnClickListener {
            openGallery()
        }

        addToDBButton.setOnClickListener{

            if (!validateFields()) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!validateImage()) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                uploadImageToFirebaseStorage()

            }
        }

    }

    private fun validateFields(): Boolean {
        val name = productName.text.toString()
        val description = productDescription.text.toString()
        val storage = productStorage.text.toString()
        val color = productColor.text.toString()
        val price = productPrice.text.toString()

        return !(name.isEmpty() || description.isEmpty() || storage.isEmpty() || color.isEmpty() || price.isEmpty())
    }

    private fun validateImage(): Boolean {
        return selectedImage != null
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            // Convert the selected image URI to a Bitmap
            selectedImage = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)

            // Set the selected image to the ImageView
            productImageView.setImageBitmap(selectedImage)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedImage != null) {
            val imageName = UUID.randomUUID().toString()
            val imageRef = storageReference.child("product_images/$imageName.jpg")

            val byteArrayOutputStream = ByteArrayOutputStream()
            selectedImage!!.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageData = byteArrayOutputStream.toByteArray()

            val uploadTask = imageRef.putBytes(imageData)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    print(imageUrl)
                    saveProductToFirestore(imageUrl)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "image not uploaded: ${e.message}" , Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle no image selected
        }
    }

    private fun saveProductToFirestore(imageUrl: String) {
        val name = productName.text.toString()
        val description = productDescription.text.toString()
        val storage = productStorage.text.toString()
        val color = productColor.text.toString()
        val price = productPrice.text.toString()

        val productMap = hashMapOf(
            "name" to name,
            "description" to description,
            "storage" to storage,
            "color" to color,
            "price" to price,
            "imageUrl" to imageUrl
        )

        firestore.collection("products")
            .add(productMap)
            .addOnSuccessListener {
                // Handle success
                Toast.makeText(this, "Product Added successfully: ", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Action Failed: ${e.message} ", Toast.LENGTH_SHORT).show()            }
    }

}