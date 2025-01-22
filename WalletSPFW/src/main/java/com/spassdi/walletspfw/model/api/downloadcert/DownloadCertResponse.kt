package com.spassdi.walletspfw.model.api.downloadcert

data class DownloadCertResponse(
    val msg: String,
    val data: DownloadCertData,
    val stateWord: Int
)

data class DownloadCertData(
    val diCert: String
)
