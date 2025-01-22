package com.spassdi.walletspfw.model.api.licensekey

data class ValidateLicenseKeyRequest(
    val key: String,
    val bundleId: String
)
