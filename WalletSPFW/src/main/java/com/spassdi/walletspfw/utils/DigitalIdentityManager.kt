package com.spassdi.walletspfw.utils

import android.security.keystore.KeyProperties
import com.spassdi.walletspfw.data.crypto.CertificateManager
import com.spassdi.walletspfw.data.crypto.KeyPairManager
import org.bouncycastle.asn1.x500.X500NameBuilder
import org.bouncycastle.asn1.x500.style.BCStyle
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.Certificate
import java.security.spec.ECGenParameterSpec

internal class DigitalIdentityManager {
    companion object {
        private const val DI_ALGORITHM = KeyProperties.KEY_ALGORITHM_EC
        private const val DI_CURVE = "secp256r1"
        private const val DI_ALIAS = "di_keypair"
        private const val DI_SIGNATURE_ALGORITHM = "SHA256withECDSA"
    }

    private val keyPairManager = KeyPairManager()
    private val certificateManager = CertificateManager()

    // Digital identity
    fun createDiKey(): KeyPair {
        val spec = ECGenParameterSpec(DI_CURVE)
        return keyPairManager.createECDSAKey(DI_ALGORITHM, spec)
    }

    fun getDiPrivateKey(): PrivateKey? {
        return try {
            keyPairManager.getPrivateKey(DI_ALIAS)
        } catch (e: Exception) {
            null
        }
    }

    fun getDiPublicKey(): PublicKey? {
        return try {
            keyPairManager.getPublicKey(DI_ALIAS)
        } catch (e: Exception) {
            null
        }
    }

    fun generateDiCsr(
        commonName: String,
        privateKey: PrivateKey,
        publicKey: PublicKey
    ): String {
        val subject = X500NameBuilder(BCStyle.INSTANCE)
            .addRDN(BCStyle.CN, commonName)
            .addRDN(BCStyle.O, "DI")
            .addRDN(BCStyle.C, "MY")
            .addRDN(BCStyle.L, "Kuching")
            .build()

        val csr = certificateManager.generateCSR(
            privateKey = privateKey,
            publicKey = publicKey,
            subject = subject,
            signatureAlgorithm = DI_SIGNATURE_ALGORITHM
        )

        return csr
    }

    fun getDiCertificate(): Certificate? {
        return certificateManager.getCertificate(DI_ALIAS)
    }

    fun saveDiCertificate(privateKey: PrivateKey, certificate: String) {
        certificateManager.saveCertificate(DI_ALIAS, privateKey, certificate)
    }

    fun checkDiCertificateValidity(): Boolean {
        return certificateManager.checkCertificateValidity(DI_ALIAS)
    }

    fun signDataUsingDiPrivateKey(data: String): String? {
        try {
            return getDiPrivateKey()?.let {
                keyPairManager.signData(
                    data = data,
                    privateKey = it,
                    signatureAlgorithm = DI_SIGNATURE_ALGORITHM
                )
            }
        } catch (e: Exception) {
            throw Exception("Error signing data using DI private key")
        }
    }

    fun deleteDiKeyPair() {
        keyPairManager.removeKeyPair(DI_ALIAS)
    }
}