package com.example.jobmagnet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class CompanyProfileActivity : AppCompatActivity() {

    private lateinit var companyNameTextView: TextView
    private lateinit var companyTypeTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var contactNumberTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var availableJobsTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_profile)

        // Initialize views
        companyNameTextView = findViewById(R.id.company_name_text_view)
        companyTypeTextView = findViewById(R.id.company_type_text_view)
        descriptionTextView = findViewById(R.id.description_text_view)
        addressTextView = findViewById(R.id.address_text_view)
        contactNumberTextView = findViewById(R.id.contact_number_text_view)
        emailTextView = findViewById(R.id.email_text_view)
        availableJobsTextView = findViewById(R.id.available_jobs_text_view)
        profileImageView = findViewById(R.id.profile_image_view)
        editButton = findViewById(R.id.edit_button)
        deleteButton = findViewById(R.id.delete_button)

        val userEmail = getUserEmail(this)
        userEmail?.let {
            loadCompanyProfile(it)
        } ?: run {
            // No email found in SharedPreferences
        }

        // Handle edit button click
        editButton.setOnClickListener {
            val intent = Intent(this, CompanyDetailsActivity::class.java)
            intent.putExtra("userEmail", userEmail)
            startActivity(intent)
        }

        // Handle delete button click
        deleteButton.setOnClickListener {
            userEmail?.let { email ->
                val companyProfileRef = getCompanyProfileRef(email)
                companyProfileRef?.let { ref ->
                    deleteCompanyProfile(ref)
                } ?: run {
                    Toast.makeText(this, "Failed to delete the company profile", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "Failed to delete the company profile", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getUserEmail(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_email", null)
    }

    private fun loadCompanyProfile(userEmail: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val companyProfile = userSnapshot.getValue(CompanyProfile::class.java)
                        displayCompanyProfile(companyProfile)
                    }
                } else {
                    // No user found with the given email
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("CompanyProfileActivity", "loadCompanyProfile:onCancelled", databaseError.toException())
            }
        })
    }

    private fun getCompanyProfileRef(userEmail: String): Query? {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        return usersRef.orderByChild("email").equalTo(userEmail)
    }

    private fun displayCompanyProfile(companyProfile: CompanyProfile?) {
        companyNameTextView.text = companyProfile?.companyName
        companyTypeTextView.text = companyProfile?.companyType
        descriptionTextView.text = companyProfile?.description
        addressTextView.text = companyProfile?.address
        contactNumberTextView.text = companyProfile?.contactNumber?.toString()
        emailTextView.text = companyProfile?.email
        availableJobsTextView.text = companyProfile?.availableJobs?.toString()
        Picasso.get().load(companyProfile?.profileImage).into(profileImageView)
    }



    private fun deleteCompanyProfile(companyProfileRef: Query) {
        val passwordDialogView = layoutInflater.inflate(R.layout.dialog_password, null)
        val passwordEditText = passwordDialogView.findViewById<EditText>(R.id.password_edit_text)

        val alertDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Enter Password")
            .setView(passwordDialogView)
            .setPositiveButton("Confirm") { _, _ ->
                val password = passwordEditText.text.toString()

                if (password.isNotEmpty()) {
                    // Add your password verification logic here
                    // If the password is correct, proceed with the deletion process
                    companyProfileRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (userSnapshot in dataSnapshot.children) {
                                    userSnapshot.ref.removeValue().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this@CompanyProfileActivity, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this@CompanyProfileActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(this@CompanyProfileActivity, "Failed to delete the company profile", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(this@CompanyProfileActivity, "Failed to delete the company profile", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(this@CompanyProfileActivity, "Failed to delete the company profile", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }
}

