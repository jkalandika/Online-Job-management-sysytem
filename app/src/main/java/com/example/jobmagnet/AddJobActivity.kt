package com.example.jobmagnet


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.jobmagnet.databinding.ActivityAddJobBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddJobActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityAddJobBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("jobs")

        binding.addButton.setOnClickListener {
            val jobTitle = binding.jobTitleInput.text.toString().trim()
            val jobDescription = binding.jobDescriptionInput.text.toString().trim()
            val jobPrice = "Rs. " + binding.jobPriceInput.text.toString().trim()
            val jobTopic = binding.jobTopicInput.text.toString().trim()

            val job = Job(jobTitle, jobDescription, jobPrice, jobTopic)
            val jobRef = database.push()
            jobRef.setValue(job)
            val jobId = jobRef.key

            startActivity(Intent(this, PaymentGatewayActivity::class.java))
            finish()
        }
    }
}

