# SPass Wallet Demo Library
This library provides a set of classes to manage and showcase some of the functionalities.
It acts as a demo for the SPass Wallet library.

This library is written in Kotlin and is available for Android.

## Table of Contents
- [SPass Wallet Demo Library](#spass-wallet-demo-library)
  - [Table of Contents](#table-of-contents)
  - [:heavy\_exclamation\_mark: Disclaimer](#heavy_exclamation_mark-disclaimer)
  - [Requirements](#requirements)
  - [Installation](#installation)
  - [Usage](#usage)
    - [Instantiating the DiAuthUseCase](#instantiating-the-diauthusecase)
    - [Onboarding](#onboarding)
    - [Login](#login)
    - [Instantiating the UserInfoUseCase](#instantiating-the-userinfousecase)
    - [Save User Info](#save-user-info)
    - [Retrieve User Info](#retrieve-user-info)
    - [Clear User Info](#clear-user-info)
    - [Check Cert Validity](#check-cert-validity)

## :heavy_exclamation_mark: Disclaimer
The released software is an initial development release version:

- The initial development release is an early endeavor reflecting the efforts of a short timeboxed
  period, and by no
  means can be considered as the final product.
- The initial development release may be changed substantially over time, might introduce new
  features but also may
  change or remove existing ones, potentially breaking compatibility with your existing code.
- The initial development release is limited in functional scope.
- The initial development release may contain errors or design flaws and other problems that could
  cause system or other
  failures and data loss.
- The initial development release has reduced security, privacy, availability, and reliability
  standards relative to
  future releases. This could make the software slower, less reliable, or more vulnerable to attacks
  than mature
  software.
- The initial development release is not yet comprehensively documented.
- Users of the software must perform sufficient engineering and additional testing in order to
  properly evaluate their
  application and determine whether any of the open-sourced components is suitable for use in that
  application.
- We strongly recommend not putting this version of the software into production use.
- Only the latest version of the software will be supported

## Requirements
- Android 8.1 (API level 27) or higher

## Installation
To import the library into your project:

**Step 1**: Add the JitPack repository to your project's `settings.gradle` file

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**Step 2**: Add the following dependency to your app's `build.gradle` file

```groovy
dependencies {
    implementation("com.github.B2Y4N:walletspfw:1.0.1")
}
```

## Usage
Below is a quick overview of how to use the library.

### Instantiating the DiAuthUseCase
The library provides a DiAuthUseCase class that can be used to create a digital identity account, and logging in using the created account.

To create a new instance of DiAuthUseCase:

```kotlin
import android.content.Context
import com.spassdi.walletspfw.domain.DiAuthUseCase

val diAuthUseCase = DiAuthUseCase(context)
```

### Onboarding
The following example shows how to onboard or create an account using DiAuthUseCase:

```kotlin
val response = diAuthUseCase.onboard(
    firstName = "John",
    lastName = "Doe",
    email = "johndoe01@example.com",
    idNumber = "010101130101",
    phoneNumber = +60123456789
)
```

The result of the onboarding process can be viewed from the response returned by the method. The following is the structure of the response:

```
Response(
    isSuccessful: Boolean, 
    msg: String, 
    stateWord: Int
)
```

### Login
Once the account has been successfully created, the login action can be performed using the following example:

```kotlin
val response = diAuthUseCase.login(
    idNumber = "010101130101",
    deviceUuid = UUID.randomUUID().toString(),
    deviceInfo = "Example device info"
)
```

Similar to the onboarding, a response containing the result of the method will be returned.

### Instantiating the UserInfoUseCase
The library also provides a UserInfoUseCase class that can be used to save user info, retrieve saved user info, as well as clear saved user info and check user's cert validity.

To create a new instance of UserInfoUseCase:

```kotlin
import android.content.Context
import com.spassdi.walletspfw.domain.UserInfoUseCase

val userInfoUseCase = UserInfoUseCase(context)
```

### Save User Info
User info is automatically saved upon successful onboarding and login. To manually save the user info, see the following example:

```kotlin
userInfoUseCase.saveUserInfo(
    appUuid = UUID.randomUUID().toString(),
    ldapUuid = UUID.randomUUID().toString(),
    name = "Jane Doe",
    nric = "010101130102",
    email = "janedoe02@example.com",
    phone = "+601234567890",
    addressLine1 = "123MainSt",
    addressLine2 = "Apt4B",
    addressLine3 = "LandmaekBuilding",
    addressLine4 = "",
    addressLine5 = ""
)
```

### Retrieve User Info
The following example shows how to retrieve the saved user info:

```kotlin
val userInfo = userInfoUseCase.getAllInfo() // return all info
val name = userInfoUseCase.getName() // return specific info
```

Structure of the user info returned from `getAllInfo()`:

```
UserInfo(
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
)
```

### Clear User Info
The following example shows how to clear the user info:

```kotlin
userInfoUseCase.clearUserInfo()
```

### Check Cert Validity
A digital cert is created upon successful onboarding and login. To check the cert validity, see the following example:

```kotlin
userInfoUseCase.checkCertValidity()
```

The result of the cert validity will be returned in `Boolean`