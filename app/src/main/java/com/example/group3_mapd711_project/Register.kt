package com.example.group3_mapd711_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class Register : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var rgUserType: RadioGroup
    private lateinit var rbUser: RadioButton
    private lateinit var rbAdmin: RadioButton
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnAlreadyUser: Button
    private lateinit var etAddress: EditText
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        FirebaseApp.initializeApp(this)

        supportActionBar?.hide()
        // Initialize views
        etFullName = findViewById(R.id.etRegFullName)
        etEmail = findViewById(R.id.etRegEmail)
        etPhoneNumber = findViewById(R.id.etRegPhoneNumber)
        etPassword = findViewById(R.id.etRegPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnAlreadyUser = findViewById(R.id.btnAlreadyUser)
        etAddress = findViewById(R.id.etRegAddress)

        btnRegister.setOnClickListener {
            // Retrieve user inputs
            val fullName = etFullName.text.toString()
            val email = etEmail.text.toString()
            val phoneNumber = etPhoneNumber.text.toString()
            val password = etPassword.text.toString()
            val address = etAddress.text.toString()

            // Firebase Authentication - Create user with email and password

            if (validateInputs(fullName, email, phoneNumber, password, address)) {

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration successful, get the user ID (UID)
                            val user = auth.currentUser
                            val userId = user?.uid ?: ""

                            // Get reference to Firestore collection "Users"
                            val firestore = FirebaseFirestore.getInstance()
                            val usersCollection = firestore.collection("Users")

                            // Create a document with email as its unique identifier
                            val userDocument = usersCollection.document(email)

                            // Create a HashMap to store user data
                            val userMap = HashMap<String, Any>()
                            userMap["fullName"] = fullName
                            userMap["email"] = email
                            userMap["phoneNumber"] = phoneNumber
                            userMap["address"] = address
                            userMap["userType"] = "User"

                            // Set user data in Firestore
                            userDocument.set(userMap)
                                .addOnSuccessListener {
                                    // Finish the activity or perform other actions upon successful registration
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    // Registration failed
                                    Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Registration failed
                            Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

            }

        }


        // Handle "Already a user? Login" button click
        btnAlreadyUser.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun validateInputs(
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String,
        address: String
    ): Boolean {
        // Check for empty fields
        if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate email format
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!email.matches(emailPattern.toRegex())) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate phone number (numeric with 10 digits)
        if (!phoneNumber.matches(Regex("\\d{10}"))) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate password (at least 6 characters, 1 uppercase, and 1 special character)
        val passwordPattern = "^(?=.*[A-Z])(?=.*[@#$%^&+=*])(?=\\S+\$).{6,}\$"
        if (!password.matches(passwordPattern.toRegex())) {
            Toast.makeText(this, "Password should have at least 6 characters with 1 uppercase and 1 special character", Toast.LENGTH_SHORT).show()
            return false
        }

        // You can add more specific validation rules for address or customize as required

        return true // All validation checks passed
    }

}

