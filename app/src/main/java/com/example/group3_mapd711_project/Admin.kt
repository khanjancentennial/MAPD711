package com.example.group3_mapd711_project

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Admin : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        supportActionBar?.hide()

        sharedPreferences = getSharedPreferences("projectUserPrefs", MODE_PRIVATE)

        // Handle login button click
        val btnLogin = findViewById<Button>(R.id.btnLoginAdmin)
        val etEmail = findViewById<EditText>(R.id.etEmailAdmin)
        val etPassword = findViewById<EditText>(R.id.etPasswordAdmin)


        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (validateInputs(email, password )) {
                // Firebase Authentication - Sign in with email and password
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            getUserType(user?.email.toString())
                            // Navigate to HomeActivity for users

                        } else {
                            // Login failed
                            Toast.makeText(
                                this,
                                "Login failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        // Handle registration text click
        val tvRegister = findViewById<TextView>(R.id.tvRegisterAdmin)
        tvRegister.setOnClickListener {
            startActivity(Intent(this, AdminRegister::class.java))
        }
        val tvCustomerLogin = findViewById<TextView>(R.id.tvBackToCustomerLoginAdmin)
        tvCustomerLogin.setOnClickListener {
            finish()
        }

    }

    fun getUserType(email: String) {
        val firestore = FirebaseFirestore.getInstance()
        val usersCollection = firestore.collection("Users")
        val userDocument = usersCollection.document(email)

        userDocument.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userType = document.getString("userType")
                    if (userType == "Admin") {

                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putBoolean("isAdmin", true)
                        editor.putString("userName", document.id)
                        editor.apply()

                        // Navigate to HomeActivity for users
                        startActivity(Intent(this, AdminHome::class.java))
                    } else {
                        // If userType is not "User", show an error message or handle accordingly
                        Toast.makeText(this, "Invalid user type or unauthorized access", Toast.LENGTH_SHORT).show()
                        // You can choose to sign out the user in this scenario if required
                        FirebaseAuth.getInstance().signOut()
                    }
                } else {
                    // Document doesn't exist, handle the case accordingly (user information not found)
                    Toast.makeText(this, "User information not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Handle errors while retrieving user information from Firestore
                Toast.makeText(this, "Error fetching user information: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateInputs(
        email: String,
        password: String
    ): Boolean {
        // Check for empty fields
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please provide email id and password", Toast.LENGTH_SHORT).show()
            return false
        }

        return true // All validation checks passed
    }
}