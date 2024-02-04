package com.example.jobmagnet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class FreelancerDetailsActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var saveButton: Button
    private lateinit var fullNameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var contactNumberEditText: EditText
    private lateinit var aboutEditText: EditText
    private lateinit var interestedJobsEditText: EditText
    private var selectedImageUri: Uri? = null

    private val formSaved = MutableLiveData<Boolean>()
    private val imageSaved = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_freelancer_details)

        profileImageView = findViewById(R.id.profileImageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        saveButton = findViewById(R.id.saveButton)
        fullNameEditText = findViewById(R.id.fullNameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        contactNumberEditText = findViewById(R.id.contactNumberEditText)
        aboutEditText = findViewById(R.id.aboutEditText)
        interestedJobsEditText = findViewById(R.id.interestedJobsEditText)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        saveButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString()
            val age = ageEditText.text.toString()
            val contactNumber = contactNumberEditText.text.toString()
            val about = aboutEditText.text.toString()
            val interestedJobs = interestedJobsEditText.text.toString()

            if (fullName.isNotEmpty() && age != null && contactNumber.isNotEmpty() && about.isNotEmpty() && interestedJobs.isNotEmpty())  {
                saveFreelancerDetails(fullName, age, contactNumber, about, interestedJobs)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        observeDataSaveStatus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            profileImageView.setImageBitmap(bitmap)
            selectImageButton.alpha = 0f
        }
    }

    private fun observeDataSaveStatus() {
        formSaved.observe(this) { formSuccess ->
            if (formSuccess && imageSaved.value == true) {
                startFreelancerDashboardActivity()
            }
        }

        imageSaved.observe(this) { imageSuccess ->
            if (imageSuccess && formSaved.value == true) {
                startFreelancerDashboardActivity()
            }
        }
    }

    private fun startFreelancerDashboardActivity() {
        val userEmail = intent.getStringExtra("userEmail")
        Toast.makeText(this, "User details and image saved successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, FreelancerDashboardActivity::class.java)
        intent.putExtra("userEmail", userEmail)
        startActivity(intent)
        finish()
    }

    private fun saveFreelancerDetails(fullName: String, age: String, contactNumber: String, about: String, interestedJobs: String) {
        val userEmail = intent.getStringExtra("userEmail")
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userId = userSnapshot.key

                        val freelancerDetails: MutableMap<String, Any> = hashMapOf<String, Any>(
                            "fullName" to fullName,
                            "age" to age,
                            "contactNumber" to contactNumber,
                            "about" to about,
                            "interestedJobs" to interestedJobs
                        )

                        usersRef.child(userId!!).updateChildren(freelancerDetails)
                            .addOnSuccessListener {
                                formSaved.value = true
                                uploadImageToFirebaseStorage(userId)
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this@FreelancerDetailsActivity, "Failed to save User details: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@FreelancerDetailsActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadImageToFirebaseStorage(userId: String) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val userProfileImage = hashMapOf("profileImage" to uri.toString()) as MutableMap<String, Any>
                    FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)
                        .updateChildren(userProfileImage)
                        .addOnSuccessListener {
                            imageSaved.value = true
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this@FreelancerDetailsActivity, "Failed to save profile image: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@FreelancerDetailsActivity, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}