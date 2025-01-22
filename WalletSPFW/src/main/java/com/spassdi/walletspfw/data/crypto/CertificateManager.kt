package com.spassdi.walletspfw.data.crypto

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder
import org.bouncycastle.util.io.pem.PemObjectGenerator
import org.bouncycastle.util.io.pem.PemWriter
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.StringWriter
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Date

internal class CertificateManager {
    companion object {
        const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER).apply {
        load(null)
    }

    private fun convertStringToX509Cert(certificate: String): X509Certificate {
        val targetStream: InputStream = ByteArrayInputStream(certificate.toByteArray())
        return CertificateFactory
            .getInstance("X509")
            .generateCertificate(targetStream) as X509Certificate
    }

    fun convertCertToString(cert: Certificate): String {
        val stringWriter = StringWriter()

        PemWriter(stringWriter).use { pemWriter ->
            val pemObjectGenerator: PemObjectGenerator = JcaMiscPEMGenerator(cert)
            pemWriter.writeObject(pemObjectGenerator)
        }

        return stringWriter.toString()
    }

    fun generateCSR(
        privateKey: PrivateKey,
        publicKey: PublicKey,
        subject: X500Name,
        signatureAlgorithm: String
    ): String {
        val builder = JcaPKCS10CertificationRequestBuilder(subject, publicKey)
        val signer = JcaContentSignerBuilder(signatureAlgorithm)
            .build(privateKey)

        val csr = builder.build(signer)

        val stringWriter = StringWriter()
        JcaPEMWriter(stringWriter).use { it.writeObject(csr) }

        return stringWriter.toString()
    }

    fun getCertificate(alias: String): Certificate? {
        val certificate = keyStore.getCertificate(alias)
        return certificate
    }

    fun saveCertificate(
        alias: String,
        privateKey: PrivateKey,
        certificate: String
    ) {
        val certificateChain = arrayOf(convertStringToX509Cert(certificate))
        keyStore.setKeyEntry(alias, privateKey, null, certificateChain)
    }

    fun checkCertificateValidity(alias: String): Boolean {
        return try {
            val certificate = keyStore.getCertificate(alias) as X509Certificate
            certificate.checkValidity()
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }
}