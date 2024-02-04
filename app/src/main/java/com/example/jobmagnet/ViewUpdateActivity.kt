package com.example.jobmagnet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jobmagnet.databinding.ActivityViewUpdateBinding
import com.google.firebase.database.*

class ViewUpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewUpdateBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("data")

        fetchLatestUpdate()

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, FreelancerDashboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchLatestUpdate() {
        databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val update = snapshot.children.first()
                    val appVersion = update.child("input1").getValue(String::class.java) ?: ""
                    val description = update.child("input2").getValue(String::class.java) ?: ""
                    val addedDate = update.child("input3").getValue(String::class.java) ?: ""

                    binding.appVersionTextView.text = "App Version: $appVersion"
                    binding.descriptionTextView.text = "Description: $description"
                    binding.addedDateTextView.text = "Added Date: $addedDate"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
            }
        })
    }
}
