package com.spassdi.walletspfw.utils

import com.spassdi.walletspfw.model.licensekey.Header
import com.spassdi.walletspfw.model.licensekey.License
import com.spassdi.walletspfw.model.licensekey.LicenseKey
import com.spassdi.walletspfw.model.licensekey.Payload
import com.spassdi.walletspfw.model.licensekey.Signature
import org.json.JSONObject
import java.util.Base64

internal class LicenseKeyUtil {
    companion object {
        private fun parseHeader(header: String): Header {
            val headerJson = JSONObject(String(Base64.getDecoder().decode(header)))

            val x5c = headerJson.getJSONArray("x5c")
            val x5cList = mutableListOf<String>()
            for (i in 0 until x5c.length()) {
                x5cList.add(x5c.getString(i))
            }

            return Header(
                value = header,
                typ = headerJson.getString("typ"),
                alg = headerJson.getString("alg"),
                x5c = x5cList
            )
        }

        private fun parsePayload(payload: String): Payload {
            val payloadJson = JSONObject(String(Base64.getDecoder().decode(payload)))

            val licenses = payloadJson.getJSONArray("licenses")
            val licenseList = mutableListOf<License>()
            for (i in 0 until licenses.length()) {
                val license = licenses.getJSONObject(i)
                val expiryDate = license.getLong("expiryDate")
                val bundleId = license.getString("bundleId")
                val licenseKey = license.getString("licenseKey")
                val type = license.getString("type")

                licenseList.add(
                    License(
                        expiryDate = expiryDate,
                        bundleId = bundleId,
                        licenseKey = licenseKey,
                        type = type
                    )
                )
            }

            return Payload(
                licenses = licenseList
            )
        }

        private fun parseSignature(jwt: String): Signature {
//            val verify = if(isCertVerify) {
//                val decodedBytes = Base64.getDecoder().decode(certificate)
//                val inputStream = decodedBytes.inputStream()
//                val certificateFactory = CertificateFactory.getInstance("X.509")
//                val cert = certificateFactory.generateCertificate(inputStream) as X509Certificate
//
//                // verify jwt signing
//                val verifier: JWSVerifier = ECDSAVerifier(cert.publicKey as ECPublicKey)
//                val signedJWT = SignedJWT.parse(jwt)
//                signedJWT.verify(verifier)
//            } else {
//                false
//            }

//            val jwtArray = jwt.split(".")

            return Signature(
                verified = true
            )
        }

        fun parseLicenseKey(licenseKey: String): LicenseKey {
            val jwt = licenseKey.split(".")

            val header = parseHeader(jwt[0])
            val payload = parsePayload(jwt[1])

            val signature = parseSignature(licenseKey)

            return LicenseKey(
                header = header,
                payload = payload,
                signature = signature
            )
        }
    }
}