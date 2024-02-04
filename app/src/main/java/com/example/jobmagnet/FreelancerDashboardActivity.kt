package com.example.jobmagnet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jobmagnet.databinding.ActivityFreelancerDashboardBinding

class FreelancerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFreelancerDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFreelancerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up View Profile button
        binding.viewProfileButton.setOnClickListener {
            startActivity(Intent(this, FreelancerProfileActivity::class.java))
        }

        // Set up View Notifications button
        binding.viewNotificationsButton.setOnClickListener {
            startActivity(Intent(this, ViewNotificationsActivity::class.java))
        }

        // Set up Search button
        binding.searchButton.setOnClickListener {
            startActivity(Intent(this, SearchJobActivity::class.java))
        }

        // Set up View Registered Jobs button
        binding.viewRegisteredJobsButton.setOnClickListener {
            startActivity(Intent(this, ViewRegisteredJobsActivity::class.java))
        }
    }
}
