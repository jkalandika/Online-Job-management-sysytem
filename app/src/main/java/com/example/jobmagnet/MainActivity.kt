package com.example.jobmagnet

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val userLoginButton = findViewById<Button>(R.id.userLoginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val adminLoginButton = findViewById<Button>(R.id.adminLoginButton)

        userLoginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        adminLoginButton.setOnClickListener {
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
        }
    }


    private fun saveUserEmail(email: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_email", email)
        editor.apply()
    }


    private fun loginUser(email: String, password: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val storedPassword = userSnapshot.child("password").getValue(String::class.java)

                        if (password == storedPassword) {
                            val accountType = userSnapshot.child("accountType").getValue(String::class.java)
                            saveUserEmail(email)
                            if (accountType.isNullOrEmpty()) {
                                // If accountType is empty or not set, navigate to the account type selection activity
                                val intent = Intent(this@MainActivity, AccountTypeSelectionActivity::class.java)
                                intent.putExtra("userEmail", email)

                                startActivity(intent)
                                finish()
                            } else {

                                // If accountType exists, navigate to the respective user dashboard based on accountType
                                val intent = when (accountType) {
                                    "company" -> Intent(this@MainActivity, CompanyDashboardActivity::class.java)
                                    "freelancer" -> Intent(this@MainActivity, FreelancerDashboardActivity::class.java)
                                    else -> null
                                }
                                if (intent != null) {
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@MainActivity, "Invalid account type", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this@MainActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, "Login failed: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
