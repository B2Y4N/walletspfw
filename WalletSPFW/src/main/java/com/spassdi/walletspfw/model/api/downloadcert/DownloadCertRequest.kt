package com.spassdi.walletspfw.model.api.downloadcert

data class DownloadCertRequest(
    val diCsr: String,
    val ldapUuid: String,
    val appUuid: String,
    val bundleId: String
)
