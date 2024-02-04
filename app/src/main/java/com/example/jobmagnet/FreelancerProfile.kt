package com.example.jobmagnet

import com.squareup.picasso.Picasso

data class FreelancerProfile(
    val accountType: String? = null,
    val fullName: String? = null,
    val interestedJobs: String? = null,
    val companyName: String? = null,
    val age: String? = null,
    val contactNumber: Any? = null,
    val about: String? = null,
    val email: String? = null,
    val profileImage: String? = null
) {
    val contactNumberString: String
        get() = contactNumber?.toString() ?: ""
}
