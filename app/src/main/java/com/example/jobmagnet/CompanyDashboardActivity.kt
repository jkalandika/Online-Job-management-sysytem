package com.example.jobmagnet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jobmagnet.databinding.ActivityCompanyDashboardBinding

class CompanyDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompanyDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up View Profile button
        binding.viewProfileButton.setOnClickListener {
            startActivity(Intent(this, FreelancerProfileActivity::class.java))
        }

        // Set up Add Job button
        binding.addJobButton.setOnClickListener {
            startActivity(Intent(this, AddJobActivity::class.java))
        }
    }
}
