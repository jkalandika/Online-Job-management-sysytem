package com.example.jobmagnet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobmagnet.databinding.ActivityAdminLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.adminLoginButton.setOnClickListener {
            val email = binding.adminEmailEditText.text.toString()
            val password = binding.adminPasswordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                adminLogin(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.userLoginButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun adminLogin(email: String, password: String) {
        val database = FirebaseDatabase.getInstance()
        val adminsRef = database.getReference("admins")

        adminsRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (adminSnapshot in dataSnapshot.children) {
                        val storedPassword = adminSnapshot.child("password").getValue(String::class.java)

                        if (password == storedPassword) {
                            Toast.makeText(this@AdminLoginActivity, "Admin login successful", Toast.LENGTH_SHORT).show()
                            // Navigate to the Admin Dashboard Activity or any desired activity
                            val intent = Intent(this@AdminLoginActivity, AdminDashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@AdminLoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@AdminLoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@AdminLoginActivity, "Admin login failed: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
