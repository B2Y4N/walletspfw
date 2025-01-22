package com.spassdi.walletspfw.data.crypto

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.AlgorithmParameterSpec
import java.util.Base64

internal class KeyPairManager {
    companion object {
        const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER).apply {
        load(null)
    }

    //create key for ECDSA
    fun createECDSAKey(algorithm: String, spec: AlgorithmParameterSpec): KeyPair {
        val keyGenerator = KeyPairGenerator.getInstance(algorithm)
        keyGenerator.initialize(spec)
        return keyGenerator.generateKeyPair()
    }

    fun getPrivateKey(alias: String): PrivateKey? {
        val existingKey = keyStore.getKey(alias, null) as? PrivateKey
        return existingKey
    }

    fun getPublicKey(alias: String): PublicKey? {
        val existingKey = keyStore.getCertificate(alias).publicKey
        return existingKey
    }

    fun signData(data: String, privateKey: PrivateKey, signatureAlgorithm: String): String {
        val signature = Signature.getInstance(signatureAlgorithm)
        signature.initSign(privateKey)
        signature.update(data.toByteArray())
        val signatureBytes = signature.sign()
        return Base64.getEncoder().encodeToString(signatureBytes)
    }

    fun removeKeyPair(alias: String) {
        keyStore.deleteEntry(alias)
    }
}