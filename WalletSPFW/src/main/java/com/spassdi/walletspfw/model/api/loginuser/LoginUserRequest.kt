package com.spassdi.walletspfw.model.api.loginuser

data class LoginUserRequest(
    val idType: String,
    val idNumber: String,
    val appUuid: String,
    val bundleId: String,
    val deviceUuid: String,
    val deviceInfo: String
)
