package com.spassdi.walletspfw.data

import com.spassdi.walletspfw.data.api.ApiClient
import com.spassdi.walletspfw.model.api.EmptyData
import com.spassdi.walletspfw.model.api.Response
import com.spassdi.walletspfw.model.api.downloadcert.DownloadCertData
import com.spassdi.walletspfw.model.api.downloadcert.DownloadCertRequest
import com.spassdi.walletspfw.model.api.loginuser.LoginUserData
import com.spassdi.walletspfw.model.api.loginuser.LoginUserRequest
import com.spassdi.walletspfw.model.api.storeuserinfo.AddressInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.AppInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.DeviceInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.EmailInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.IdentityDocumentInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.LdapInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.PhoneNumberInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.StoreUserInfoRequest
import com.spassdi.walletspfw.model.api.storeuserinfo.StoreUserInfoResponse
import com.spassdi.walletspfw.model.api.storeuserinfo.UserInfo
import org.json.JSONObject

internal class DiAuthRepository {
    suspend fun storeUserInfo(
        userInfo: UserInfo,
        addressInfo: AddressInfo,
        phoneNumberInfo: PhoneNumberInfo,
        emailInfo: EmailInfo,
        identityDocumentInfo: IdentityDocumentInfo,
        deviceInfo: DeviceInfo,
        appInfo: AppInfo,
        ldapInfo: LdapInfo
    ): Response<EmptyData> {
        try {
            val storeUserInfoRequest = StoreUserInfoRequest(
                userInfo = userInfo,
                addressInfo = addressInfo,
                phoneNumberInfo = phoneNumberInfo,
                emailInfo = emailInfo,
                identityDocumentInfo = identityDocumentInfo,
                deviceInfo = deviceInfo,
                appInfo = appInfo,
                ldapInfo = ldapInfo
            )

            val response = ApiClient.apiService.storeUserInfo(storeUserInfoRequest)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    return Response(
                        isSuccessful = true,
                        msg = responseBody.msg,
                        stateWord = responseBody.stateWord
                    )
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val errorJson = JSONObject(errorBody)
                    val msg = errorJson.optString("msg")
                    val stateWord = errorJson.optInt("stateWord")

                    return Response(
                        isSuccessful = false,
                        msg = msg,
                        stateWord = stateWord
                    )
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Response(
            isSuccessful = false,
            msg = "Error storing user info",
            stateWord = -1
        )
    }

    suspend fun loginUser(
        idType: String,
        idNumber: String,
        appUuid: String,
        bundleId: String,
        deviceUuid: String,
        deviceInfo: String
    ): Response<LoginUserData> {
        try {
            val loginUserRequest = LoginUserRequest(
                idType = idType,
                idNumber = idNumber,
                appUuid = appUuid,
                bundleId = bundleId,
                deviceUuid = deviceUuid,
                deviceInfo = deviceInfo
            )

            val response = ApiClient.apiService.loginUser(loginUserRequest)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    return Response(
                        isSuccessful = true,
                        msg = responseBody.msg,
                        stateWord = responseBody.stateWord,
                        data = responseBody.data
                    )
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val errorJson = JSONObject(errorBody)
                    val msg = errorJson.optString("message")
//                    val stateWord = errorJson.optInt("stateWord")

                    return Response(
                        isSuccessful = false,
                        msg = msg,
                        stateWord = 404
                    )
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Response(
            isSuccessful = false,
            msg = "Error logging in user",
            stateWord = -1
        )
    }

    suspend fun downloadCert(
        diCsr: String,
        ldapUuid: String,
        appUuid: String,
        bundleId: String
    ): Response<DownloadCertData> {
        try {
            val downloadCertRequest = DownloadCertRequest(
                diCsr = diCsr,
                ldapUuid = ldapUuid,
                appUuid = appUuid,
                bundleId = bundleId
            )

            val response = ApiClient.apiService.downloadCert(downloadCertRequest)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    return Response(
                        isSuccessful = true,
                        msg = responseBody.msg,
                        stateWord = responseBody.stateWord,
                        data = responseBody.data
                    )
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val errorJson = JSONObject(errorBody)
                    val msg = errorJson.optString("msg")
                    val stateWord = errorJson.optInt("stateWord")

                    return Response(
                        isSuccessful = false,
                        msg = msg,
                        stateWord = stateWord
                    )
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Response(
            isSuccessful = false,
            msg = "Error downloading certificate",
            stateWord = -1
        )
    }
}