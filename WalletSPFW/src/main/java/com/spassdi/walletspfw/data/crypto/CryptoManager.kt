package com.spassdi.walletspfw.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

internal class CryptoManager {
    companion object {
        const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER).apply {
        load(null)
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("diwEncrypt", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "diwEncrypt",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(inputText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val encryptedBytes = cipher.doFinal(inputText.toByteArray())
        val iv = cipher.iv

        val encryptedDataWithIv = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, encryptedDataWithIv, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, encryptedDataWithIv, iv.size, encryptedBytes.size)
        return Base64.encodeToString(encryptedDataWithIv, Base64.DEFAULT)
    }

    fun decrypt(data: String): String {
        try {
            val encryptedDataWithIv = Base64.decode(data, Base64.DEFAULT)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val iv = encryptedDataWithIv.copyOfRange(0, cipher.blockSize)
            cipher.init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))

            val encryptedData = encryptedDataWithIv.copyOfRange(cipher.blockSize, encryptedDataWithIv.size)
            val decryptedBytes = cipher.doFinal(encryptedData)
            return String(decryptedBytes)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
    }
}