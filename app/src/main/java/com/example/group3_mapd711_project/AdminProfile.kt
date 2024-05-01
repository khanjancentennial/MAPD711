package com.example.group3_mapd711_project

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class AdminProfile : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var btnUpdate: Button
    private lateinit var backButton: ImageButton
    private val firestoreDB = FirebaseFirestore.getInstance()

    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profile)
        supportActionBar?.hide()
        // Initialize views
        etName = findViewById(R.id.etProfileNameAdmin)
        etPhoneNumber = findViewById(R.id.etProfilePhoneNumberAdmin)
        etAddress = findViewById(R.id.etProfileAddressAdmin)
        btnUpdate = findViewById(R.id.btnUpdateProfileAdmin)
        backButton = findViewById(R.id.profileBackButtonAdmin)

        sharedPreferences = getSharedPreferences("projectUserPrefs", MODE_PRIVATE)


        val backImageStream = assets.open("left-arrow.png")
        val bitmap = BitmapFactory.decodeStream(backImageStream)
        backButton.setImageBitmap(bitmap)
        backButton.setOnClickListener{
            finish()
        }

        val emailId = sharedPreferences.getString("userName", "")
        var docRef = firestoreDB.collection("Users").document(emailId.toString()).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val fullName = documentSnapshot.getString("fullName")
                    val address = documentSnapshot.getString("address")
                    val phoneNumber = documentSnapshot.getString("phoneNumber")
                    val fullNameEditable = Editable.Factory.getInstance().newEditable(fullName)
                    etName.text = fullNameEditable

                    val addressEditable = Editable.Factory.getInstance().newEditable(address)
                    etAddress.text = addressEditable

                    val phoneNumberEditable = Editable.Factory.getInstance().newEditable(phoneNumber)
                    etPhoneNumber.text = phoneNumberEditable

                }
            }
            .addOnFailureListener { e ->
                // Handle any errors that occurred while fetching the document
            }


        // Set click listener for "Update" button
        btnUpdate.setOnClickListener {
            val docRef = firestoreDB.collection("Users").document(emailId.toString())

            // Create a map with the fields which will update
            val updates = hashMapOf(
                "fullName" to etName.text.toString(),
                "address" to etAddress.text.toString(),
                "phoneNumber" to etPhoneNumber.text.toString()

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