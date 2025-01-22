package com.spassdi.walletspfw.domain

import android.content.Context
import com.spassdi.walletspfw.data.DiAuthRepository
import com.spassdi.walletspfw.data.LicenseKeyRepository
import com.spassdi.walletspfw.model.api.EmptyData
import com.spassdi.walletspfw.model.api.Response
import com.spassdi.walletspfw.model.api.loginuser.LoginUserData
import com.spassdi.walletspfw.model.api.storeuserinfo.AddressInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.AppInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.DeviceInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.EmailInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.IdentityDocumentInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.LdapInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.PhoneNumberInfo
import com.spassdi.walletspfw.model.api.storeuserinfo.UserInfo
import com.spassdi.walletspfw.utils.DigitalIdentityManager
import com.spassdi.walletspfw.utils.LicenseKeyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.security.PrivateKey
import java.util.UUID

class DiAuthUseCase(context: Context, private val licenseKey: String) {
    private val diAuthRepository = DiAuthRepository()
//    private val licenseKeyRepository = LicenseKeyRepository()
    private val userInfoUseCase = UserInfoUseCase(context, licenseKey)
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

    private fun generateKeyPairAndCsr(name: String): Pair<PrivateKey, String> {
        val keyPair = digitalIdentityManager.createDiKey()

        val csr = digitalIdentityManager.generateDiCsr(
            commonName = name,
            privateKey = keyPair.private,
            publicKey = keyPair.public
        )
        val formattedCsr = csr.replace("-----BEGIN CERTIFICATE REQUEST-----\n", "")
            .replace("\n-----END CERTIFICATE REQUEST-----\n","")
            .replace("\n","")

        return Pair(keyPair.private, formattedCsr)
    }

    private suspend fun clearUserData() {
        digitalIdentityManager.deleteDiKeyPair()
        userInfoUseCase.clearUserInfo()
    }

    suspend fun login(
        idNumber: String,
        deviceUuid: String,
        deviceInfo: String
    ): Response<LoginUserData> {
        if (isLicenseKeyValid) {
            try {
                val appUuid = UUID.randomUUID().toString()

                val loginUser = diAuthRepository.loginUser(
                    idType = "IC",
                    idNumber = idNumber,
                    appUuid = appUuid,
                    bundleId = bundleId,
                    deviceUuid = deviceUuid,
                    deviceInfo = deviceInfo
                )

                if (loginUser.isSuccessful) {
                    val data = loginUser.data!!
                    val name = data.name
                    val ldapUuid = data.ldapUuid
                    val (diPrivateKey, diCsr) = generateKeyPairAndCsr(name)

                    val downloadCert = diAuthRepository.downloadCert(
                        diCsr = diCsr,
                        ldapUuid = ldapUuid,
                        appUuid = appUuid,
                        bundleId = bundleId
                    )

                    if (downloadCert.isSuccessful) {
                        clearUserData()

                        val certData = downloadCert.data!!
                        digitalIdentityManager.saveDiCertificate(diPrivateKey, certData.diCert)

                        val nric = data.nric
                        val email = data.email
                        val phone = data.phone
                        val addressLine1 = data.addressLine1
                        val addressLine2 = data.addressLine2
                        val addressLine3 = data.addressLine3
                        val addressLine4 = data.addressLine4
                        val addressLine5 = data.addressLine5

                        userInfoUseCase.saveUserInfo(
                            appUuid = appUuid,
                            ldapUuid = ldapUuid,
                            name = name,
                            nric = nric,
                            email = email,
                            phone = phone,
                            addressLine1 = addressLine1,
                            addressLine2 = addressLine2,
                            addressLine3 = addressLine3,
                            addressLine4 = addressLine4,
                            addressLine5 = addressLine5
                        )

                        return loginUser
                    } else {
                        return Response(
                            isSuccessful = false,
                            msg = downloadCert.msg,
                            stateWord = downloadCert.stateWord
                        )
                    }
                } else {
                    return Response(
                        isSuccessful = false,
                        msg = loginUser.msg,
                        stateWord = loginUser.stateWord
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return Response(
                isSuccessful = false,
                msg = "Error logging in",
                stateWord = -1
            )
        } else {
            return Response(
                isSuccessful = false,
                msg = "License key is not valid",
                stateWord = -2
            )
        }
    }

    suspend fun onboard(
        firstName: String,
        lastName: String,
        email: String,
        idNumber: String,
        phoneNumber: String
    ): Response<EmptyData> {
        if (isLicenseKeyValid) {
            try {
                val appUuid = UUID.randomUUID().toString()
                val fullName = "$firstName $lastName"
                val addressLine1 = "123MainSt"
                val addressLine2 = "Apt4B"
                val addressLine3 = "LandmarkBuilding"
                val addressLine4 = ""
                val addressLine5 = ""

                val userInfo = UserInfo(
                    name = fullName,
                    firstName = firstName,
                    lastName = lastName,
                    status = "Active"
                )

                val addressInfo = AddressInfo(
                    countryId = 9574,
                    postalCode = 123456,
                    stateId = 9775,
                    cityId = 10321,
                    divisionId = 56,
                    districtId = 74,
                    subdistrictId = 112,
                    line1 = addressLine1,
                    line2 = addressLine2,
                    line3 = addressLine3,
                    line4 = addressLine4,
                    line5 = addressLine5,
                    area = "Central",
                    street = "MainStreet",
                    buildingName = "CentralPlaza",
                    isPrimary = 1,
                    latitude = "40.7128",
                    longitude = "-74.0060"
                )

                val phoneNumberInfo = PhoneNumberInfo(
                    phoneNumber = phoneNumber,
                    isVerified = 1,
                    isPrimary = 1
                )

                val emailInfo = EmailInfo(
                    emailAddress = email,
                    isVerified = 1,
                    isPrimary = 1
                )

                val identityDocumentInfo = IdentityDocumentInfo(
                    idType = "IC",
                    idNumber = idNumber,
                    countryId = 9574
                )

                val deviceInfo = DeviceInfo(
                    uuid = appUuid,
                    info = "Userdeviceinfo",
                    identifier = "DeviceIdentifier",
                    isPrimary = 1
                )

                val appInfo = AppInfo(
                    uuid = appUuid,
                    bundleId = bundleId,
                    isPrimary = 1
                )

                val ldapInfo = LdapInfo(
                    uuid = appUuid,
                    username = fullName,
                    domain = "ldapApp"
                )

                val storeUserInfo = diAuthRepository.storeUserInfo(
                    userInfo = userInfo,
                    addressInfo = addressInfo,
                    phoneNumberInfo = phoneNumberInfo,
                    emailInfo = emailInfo,
                    identityDocumentInfo = identityDocumentInfo,
                    deviceInfo = deviceInfo,
                    appInfo = appInfo,
                    ldapInfo = ldapInfo
                )

                if (storeUserInfo.isSuccessful) {
                    val (diPrivateKey, diCsr) = generateKeyPairAndCsr(fullName)

                    val downloadCert = diAuthRepository.downloadCert(
                        diCsr = diCsr,
                        ldapUuid = appUuid,
                        appUuid = appUuid,
                        bundleId = bundleId
                    )

                    if (downloadCert.isSuccessful) {
                        clearUserData()
                        val data = downloadCert.data!!
                        digitalIdentityManager.saveDiCertificate(diPrivateKey, data.diCert)

                        userInfoUseCase.saveUserInfo(
                            appUuid = appUuid,
                            ldapUuid = appUuid,
                            name = fullName,
                            nric = idNumber,
                            email = email,
                            phone = phoneNumber,
                            addressLine1 = addressLine1,
                            addressLine2 = addressLine2,
                            addressLine3 = addressLine3,
                            addressLine4 = addressLine4,
                            addressLine5 = addressLine5
                        )

                        return Response(
                            isSuccessful = true,
                            msg = downloadCert.msg,
                            stateWord = downloadCert.stateWord
                        )
                    } else {
                        return Response(
                            isSuccessful = false,
                            msg = downloadCert.msg,
                            stateWord = downloadCert.stateWord
                        )
                    }
                } else {
                    return Response(
                        isSuccessful = false,
                        msg = storeUserInfo.msg,
                        stateWord = storeUserInfo.stateWord
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return Response(
                isSuccessful = false,
                msg = "Error onboarding",
                stateWord = -1
            )
        } else {
            return Response(
                isSuccessful = false,
                msg = "License key is not valid",
                stateWord = -2
            )
        }
    }
}