package com.spassdi.walletspfw.data

import com.spassdi.walletspfw.data.api.ApiClient
import com.spassdi.walletspfw.model.api.EmptyData
import com.spassdi.walletspfw.model.api.Response
import com.spassdi.walletspfw.model.api.licensekey.ValidateLicenseKeyRequest
import org.json.JSONObject

internal class LicenseKeyRepository {
    suspend fun validateLicenseKey(
        licenseKey: String,
        bundleId: String
    ): Response<EmptyData> {
        try {
            val request = ValidateLicenseKeyRequest(
                key = licenseKey,
                bundleId = bundleId
            )

            val response = ApiClient.apiService.validateLicenseKey(
                url = "https://spass-di-tnt.sains.com.my/DI/api/v1/validateLicenseKey",
                body = request
            )

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
                    val msg = errorJson.optString("message")

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
            msg = "Error validating license key",
            stateWord = -1
        )
    }
}