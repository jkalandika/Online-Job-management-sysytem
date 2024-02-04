package com.example.jobmagnet

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
import java.io.ByteArrayOutputStream
import java.util.*


class CompanyDetailsActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var saveButton: Button
    private lateinit var companyNameEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var contactNumberEditText: EditText
    private lateinit var companyTypeEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var availableJobsEditText: EditText
    private var selectedImageUri: Uri? = null

    private val formSaved = MutableLiveData<Boolean>()
    private val imageSaved = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_details)

        profileImageView = findViewById(R.id.profileImageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        saveButton = findViewById(R.id.saveButton)
        companyNameEditText = findViewById(R.id.companyNameEditText)
        addressEditText = findViewById(R.id.addressEditText)
        contactNumberEditText = findViewById(R.id.contactNumberEditText)
        companyTypeEditText = findViewById(R.id.companyTypeEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        availableJobsEditText = findViewById(R.id.availableJobsEditText)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        saveButton.setOnClickListener {
            val companyName = companyNameEditText.text.toString()
            val address = addressEditText.text.toString()
            val contactNumber = contactNumberEditText.text.toString()
            val companyType = companyTypeEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val availableJobs = availableJobsEditText.text.toString()

            if (companyName.isNotEmpty() && address.isNotEmpty() && contactNumber.isNotEmpty() && companyType.isNotEmpty() && description.isNotEmpty() && availableJobs.isNotEmpty()) {
                saveCompanyDetails(companyName, address, contactNumber, companyType, description, availableJobs.toInt())
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
                startCompanyDashboardActivity()
            }
        }

        imageSaved.observe(this) { imageSuccess ->
            if (imageSuccess && formSaved.value == true) {
                startCompanyDashboardActivity()
            }
        }
    }

    private fun startCompanyDashboardActivity() {
        Toast.makeText(this, "Company details and image saved successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, CompanyProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveCompanyDetails(companyName: String, address: String, contactNumber: String, companyType: String, description: String, availableJobs: Int) {
        val userEmail = intent.getStringExtra("userEmail")
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userId = userSnapshot.key

                        val companyDetails: MutableMap<String, Any> = hashMapOf<String, Any>(
                            "companyName" to companyName,
                            "address" to address,
                            "contactNumber" to contactNumber,
                            "companyType" to companyType,
                            "description" to description,
                            "availableJobs" to availableJobs.toString()
                        )

                        usersRef.child(userId!!).updateChildren(companyDetails)
                            .addOnSuccessListener {
                                formSaved.value = true
                                uploadImageToFirebaseStorage(userId)
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this@CompanyDetailsActivity, "Failed to save company details: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@CompanyDetailsActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this@CompanyDetailsActivity, "Failed to save profile image: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@CompanyDetailsActivity, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}