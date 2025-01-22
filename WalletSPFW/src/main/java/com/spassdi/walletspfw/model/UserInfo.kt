package com.spassdi.walletspfw.model

data class UserInfo(
    val appUuid: String,
    val ldapUuid: String,
    val name: String,
    val nric: String,
    val email: String,
    val phone: String,
    val addressLine1: String,
    val addressLine2: String,
    val addressLine3: String,
    val addressLine4: String,
    val addressLine5: String
)
