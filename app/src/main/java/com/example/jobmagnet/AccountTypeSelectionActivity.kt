package com.example.jobmagnet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobmagnet.databinding.ActivityAccountTypeSelectionBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class AccountTypeSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountTypeSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountTypeSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userEmail = intent.getStringExtra("userEmail")

        if (userEmail == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.companyButton.setOnClickListener {
            updateAccountType(userEmail, "company")
            val intent = Intent(this, CompanyDetailsActivity::class.java)
            intent.putExtra("userEmail", userEmail)
            startActivity(intent)

            finish()
        }

        binding.freelancerButton.setOnClickListener {
            updateAccountType(userEmail, "freelancer")
            val intent = Intent(this, FreelancerDetailsActivity::class.java)
            intent.putExtra("userEmail", userEmail)
            startActivity(intent)

            finish()
        }
    }

    private fun updateAccountType(email: String, accountType: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        userSnapshot.ref.child("accountType").setValue(accountType)
                        Toast.makeText(this@AccountTypeSelectionActivity, "Details added to database", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AccountTypeSelectionActivity, "User not exists", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@AccountTypeSelectionActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
