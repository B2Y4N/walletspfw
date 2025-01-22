package com.spassdi.walletspfw.domain

import android.content.Context
import com.spassdi.walletspfw.data.LicenseKeyRepository
import com.spassdi.walletspfw.data.UserInfoRepository
import com.spassdi.walletspfw.model.UserInfo
import com.spassdi.walletspfw.utils.DigitalIdentityManager
import com.spassdi.walletspfw.utils.LicenseKeyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class UserInfoUseCase(context: Context, private val licenseKey: String) {
    private val userInfoRepository = UserInfoRepository(context)
//    private val licenseKeyRepository = LicenseKeyRepository()
    private val digitalIdentityManager = DigitalIdentityManager()
    private val bundleId = context.packageName

//    private var isLicenseKeyValid = false
    private var isLicenseKeyValid = true

//    private val scope = CoroutineScope(Job() + Dispatchers.IO)
//    init {
//        scope.launch {
//            val parsedLicenseKey = LicenseKeyUtil.parseLicenseKey(licenseKey)
//
//            val isValid = parsedLicenseKey.signature.verified && parsedLicenseKey.payload.licenses.any { license ->
//                license.type == "DigitalIdentity"
//                        && license.expiryDate > System.currentTimeMillis()
//                        && license.bundleId == bundleId
//            }
//
//            if (isValid) {
//                val license = parsedLicenseKey.payload.licenses.find { license ->
//                    license.type == "DigitalIdentity"
//                }
//                val response = licenseKeyRepository.validateLicenseKey(license?.licenseKey ?: "", bundleId)
//                if (response.isSuccessful) {
//                    isLicenseKeyValid = true
//                }
//            }
//            scope.cancel()
//        }
//    }

    suspend fun saveUserInfo(
        appUuid: String,
        ldapUuid: String,
        name: String,
        nric: String,
        email: String,
        phone: String,
        addressLine1: String,
        addressLine2: String,
        addressLine3: String,
        addressLine4: String,
        addressLine5: String
    ) {
        if (isLicenseKeyValid) {
            userInfoRepository.batchSaveInfo(
                mapOf(
                    UserInfoRepository.KEY_APP_UUID to appUuid,
                    UserInfoRepository.KEY_LDAP_UUID to ldapUuid,
                    UserInfoRepository.KEY_NAME to name,
                    UserInfoRepository.KEY_NRIC to nric,
                    UserInfoRepository.KEY_EMAIL to email,
                    UserInfoRepository.KEY_PHONE to phone,
                    UserInfoRepository.KEY_ADDRESS_LINE_1 to addressLine1,
                    UserInfoRepository.KEY_ADDRESS_LINE_2 to addressLine2,
                    UserInfoRepository.KEY_ADDRESS_LINE_3 to addressLine3,
                    UserInfoRepository.KEY_ADDRESS_LINE_4 to addressLine4,
                    UserInfoRepository.KEY_ADDRESS_LINE_5 to addressLine5
                )
            )
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getAllInfo(): UserInfo {
        if (isLicenseKeyValid) {
            val infos = userInfoRepository.getAllInfo()
            return UserInfo(
                appUuid = infos[UserInfoRepository.KEY_APP_UUID] ?: "",
                ldapUuid = infos[UserInfoRepository.KEY_LDAP_UUID] ?: "",
                name = infos[UserInfoRepository.KEY_NAME] ?: "",
                nric = infos[UserInfoRepository.KEY_NRIC] ?: "",
                email = infos[UserInfoRepository.KEY_EMAIL] ?: "",
                phone = infos[UserInfoRepository.KEY_PHONE] ?: "",
                addressLine1 = infos[UserInfoRepository.KEY_ADDRESS_LINE_1] ?: "",
                addressLine2 = infos[UserInfoRepository.KEY_ADDRESS_LINE_2] ?: "",
                addressLine3 = infos[UserInfoRepository.KEY_ADDRESS_LINE_3] ?: "",
                addressLine4 = infos[UserInfoRepository.KEY_ADDRESS_LINE_4] ?: "",
                addressLine5 = infos[UserInfoRepository.KEY_ADDRESS_LINE_5] ?: ""
            )
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getAppUuid(): String? {
        if (isLicenseKeyValid) {
            return userInfoRepository.getInfo(UserInfoRepository.KEY_APP_UUID)
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getLdapUuid(): String? {
        if (isLicenseKeyValid) {
            return userInfoRepository.getInfo(UserInfoRepository.KEY_LDAP_UUID)
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getName(): String? {
        if (isLicenseKeyValid) {
            return userInfoRepository.getInfo(UserInfoRepository.KEY_NAME)
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getNric(): String? {
        if (isLicenseKeyValid) {
            return userInfoRepository.getInfo(UserInfoRepository.KEY_NRIC)
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getEmail(): String? {
        if (isLicenseKeyValid) {
            return userInfoRepository.getInfo(UserInfoRepository.KEY_EMAIL)
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getPhoneNumber(): String? {
        if (isLicenseKeyValid) {
            return userInfoRepository.getInfo(UserInfoRepository.KEY_PHONE)
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getAddressLine1(): String? {
        if (isLicenseKeyValid) {
            return userInfoRepository.getInfo(UserInfoRepository.KEY_ADDRESS_LINE_1)
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getAddressLine2(): String? {
        if (isLicenseKeyValid) {
            return userInfoRepository.getInfo(UserInfoRepository.KEY_ADDRESS_LINE_2)
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getAddressLine3(): String? {
        if (isLicenseKeyValid) {
            return userInfoRepository.getInfo(UserInfoRepository.KEY_ADDRESS_LINE_3)
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getAddressLine4(): String? {
        if (isLicenseKeyValid) {
            return userInfoRepository.getInfo(UserInfoRepository.KEY_ADDRESS_LINE_4)
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun getAddressLine5(): String? {
        if (isLicenseKeyValid) {
            return userInfoRepository.getInfo(UserInfoRepository.KEY_ADDRESS_LINE_5)
        } else {
            throw Exception("License key is not valid")
        }
    }

    fun checkCertValidity(): Boolean {
        if (isLicenseKeyValid) {
            return digitalIdentityManager.checkDiCertificateValidity()
        } else {
            throw Exception("License key is not valid")
        }
    }

    suspend fun clearUserInfo() {
        if (isLicenseKeyValid) {
            userInfoRepository.clearUserInfo()
        } else {
            throw Exception("License key is not valid")
        }
    }
}