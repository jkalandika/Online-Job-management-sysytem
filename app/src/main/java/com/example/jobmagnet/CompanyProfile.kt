package com.example.jobmagnet

data class CompanyProfile(
    val accountType: String? = null,
    val address: String? = null,
    val availableJobs: String? = null,
    val companyName: String? = null,
    val companyType: String? = null,
    val contactNumber: Any? = null,
    val description: String? = null,
    val email: String? = null,
    val profileImage: String? = null
) {
    val contactNumberString: String
        get() = contactNumber?.toString() ?: ""
}
