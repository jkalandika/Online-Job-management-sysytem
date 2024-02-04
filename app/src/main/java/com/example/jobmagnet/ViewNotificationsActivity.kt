package com.example.jobmagnet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.jobmagnet.databinding.ActivityViewNotificationsBinding

class ViewNotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listeners for the buttons
        binding.companyNotifyBtn.setOnClickListener {
            val intent = Intent(this, CompanyNotify::class.java)
            startActivity(intent)
        }

        binding.appUpdatesBtn.setOnClickListener {
            val intent = Intent(this, ViewUpdateActivity::class.java)
            startActivity(intent)
        }
    }
}


