package com.example.jobmagnet

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobmagnet.databinding.ActivityPutUpdateBinding
import com.google.firebase.database.FirebaseDatabase

class PutUpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPutUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPutUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
            val input1 = binding.input1EditText.text.toString()
            val input2 = binding.input2EditText.text.toString()
            val input3 = binding.input3EditText.text.toString()
            if (input1.isNotEmpty() && input2.isNotEmpty() && input3.isNotEmpty()) {
                saveData(input1, input2, input3)
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveData(input1: String, input2: String, input3: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("data")

        val data = hashMapOf(
            "input1" to input1,
            "input2" to input2,
            "input3" to input3
        )

        myRef.push().setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data save failed", Toast.LENGTH_SHORT).show()
            }
    }

}
