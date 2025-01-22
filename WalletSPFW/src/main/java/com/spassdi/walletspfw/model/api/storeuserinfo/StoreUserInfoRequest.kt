package com.spassdi.walletspfw.model.api.storeuserinfo

data class StoreUserInfoRequest(
    val userInfo: UserInfo,
    val addressInfo: AddressInfo,
    val phoneNumberInfo: PhoneNumberInfo,
    val emailInfo: EmailInfo,
    val identityDocumentInfo: IdentityDocumentInfo,
    val deviceInfo: DeviceInfo,
    val appInfo: AppInfo,
    val ldapInfo: LdapInfo
)

data class UserInfo(
    val name: String,
    val firstName: String,
    val lastName: String,
    val status: String
)

data class AddressInfo(
    val countryId: Int,
    val postalCode: Int,
    val stateId: Int,
    val cityId: Int,
    val divisionId: Int,
    val districtId: Int,
    val subdistrictId: Int,
    val line1: String,
    val line2: String,
    val line3: String,
    val line4: String,
    val line5: String,
    val area: String,
    val street: String,
    val buildingName: String,
    val isPrimary: Int,
    val latitude: String,
    val longitude: String
)

data class PhoneNumberInfo(
    val phoneNumber: String,
    val isVerified: Int,
    val isPrimary: Int
)

data class EmailInfo(
    val emailAddress: String,
    val isVerified: Int,
    val isPrimary: Int
)

data class IdentityDocumentInfo(
    val idType: String,
    val idNumber: String,
    val countryId: Int
)

data class DeviceInfo(
    val uuid: String,
    val info: String,
    val identifier: String,
    val isPrimary: Int
)

data class AppInfo(
    val uuid: String,
    val bundleId: String,
    val isPrimary: Int
)

data class LdapInfo(
    val uuid: String,
    val username: String,
    val domain: String
)