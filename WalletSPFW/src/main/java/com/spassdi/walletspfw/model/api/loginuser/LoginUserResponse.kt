package com.spassdi.walletspfw.model.api.loginuser

data class LoginUserResponse(
    val msg: String,
    val data: LoginUserData,
    val stateWord: Int
)

data class LoginUserData(
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
