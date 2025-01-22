package com.spassdi.walletspfw.model.licensekey

internal data class LicenseKey(
    val header: Header,
    val payload: Payload,
    val signature: Signature
)

internal data class Header(
    val value: String,
    val typ: String,
    val alg: String,
    val x5c: List<String>
)

internal data class Payload(
    val licenses: List<License>
)

internal data class License(
    val expiryDate: Long,
    val bundleId: String,
    val licenseKey: String,
    val type: String
)

internal data class Signature(
    val verified: Boolean
)
