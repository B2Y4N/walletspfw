package com.spassdi.walletspfw.data.api

import com.spassdi.walletspfw.model.api.downloadcert.DownloadCertRequest
import com.spassdi.walletspfw.model.api.downloadcert.DownloadCertResponse
import com.spassdi.walletspfw.model.api.licensekey.ValidateLicenseKeyRequest
import com.spassdi.walletspfw.model.api.licensekey.ValidateLicenseKeyResponse
import com.spassdi.walletspfw.model.api.loginuser.LoginUserRequest
import com.spassdi.walletspfw.model.api.loginuser.LoginUserResponse
import com.spassdi.walletspfw.model.api.storeuserinfo.StoreUserInfoRequest
import com.spassdi.walletspfw.model.api.storeuserinfo.StoreUserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

internal interface ApiService {
    @POST("storeUserInfo")
    suspend fun storeUserInfo(@Body body: StoreUserInfoRequest): Response<StoreUserInfoResponse>

    @POST("loginUser")
    suspend fun loginUser(@Body body: LoginUserRequest): Response<LoginUserResponse>

    @POST("downloadCert")
    suspend fun downloadCert(@Body body: DownloadCertRequest): Response<DownloadCertResponse>

    @POST
    suspend fun validateLicenseKey(
        @Url url: String,
        @Body body: ValidateLicenseKeyRequest
    ): Response<ValidateLicenseKeyResponse>
}