package com.nhlstenden.smarthome.utils

import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

const val TRANSFORMATION = "RSA"

/**
 * RSA encryption util
 *
 * @author Robert
 * @since 1.0
 */
class RSA(private val public: PublicKey, private val private: PrivateKey) {

    companion object {
        /** RSA [Cipher] instance */
        private val cipher = Cipher.getInstance(TRANSFORMATION)

        /**
         * Generates an [RSA] instance using the specified [keySize]
         *
         * @return the created [RSA] instance
         */
        fun create(keySize: Int) = try {
            // Initialize a keypair generator for RSA
            val generator = KeyPairGenerator.getInstance(TRANSFORMATION)
            generator.initialize(keySize)

            // Generate keypair, convert to RSA util and return
            val pair = generator.generateKeyPair()
            RSA(pair.public, pair.private)
        } catch (e: Exception) {
            println("Failed to initialize RSA: ${e.message}")
            null
        }

        /**
         * Generates an [RSA] instance from the specified [public] and [private] key as a [Base64] [String]
         *
         * @return the created [RSA] instance
         */
        fun from(public: String, private: String): RSA {
            val decoder = Base64.getDecoder()
            val factory = KeyFactory.getInstance(TRANSFORMATION)

            // Generate encoded key specs from base64 keys
            val publicKeySpec = X509EncodedKeySpec(decoder.decode(public))
            val privateKeySpec = PKCS8EncodedKeySpec(decoder.decode(private))

            // Generate keys from key specs
            val publicKey = factory.generatePublic(publicKeySpec)
            val privateKey = factory.generatePrivate(privateKeySpec)
            return RSA(publicKey, privateKey)
        }
    }

    /**
     * Encrypts the specified input using this [RSA] instance's public key
     *
     * @return the encrypted [String], or null if an exception occurred
     */
    fun encrypt(input: String) = try {
        // Initialize cipher for encrypting
        cipher.init(Cipher.ENCRYPT_MODE, public)

        // Convert input to byte array and encrypt
        val encrypted = cipher.doFinal(input.toByteArray())

        // Encode byte array to base64 string and return
        Base64.getEncoder().encodeToString(encrypted)
    } catch (e: Exception) {
        println("Failed to encrypt ($input): ${e.message}")
        null
    }

    /**
     * Decrypts the specified input using this [RSA] instance's private key
     *
     * @return the decrypted [String], or null if an exception occurred
     */
    fun decrypt(input: String) = try {
        // Initialize cipher for decrypting
        cipher.init(Cipher.DECRYPT_MODE, private)

        // Convert input to byte array
        val decoded = Base64.getDecoder().decode(input)

        // Decrypt, convert to string and return
        String(cipher.doFinal(decoded))
    } catch (e: Exception) {
        println("Failed to decrypt ($input): ${e.message}")
        null
    }

    /** Gets the public key as a [Base64] [String] */
    fun getPublic() = Base64.getEncoder().encodeToString(public.encoded)!!

    /** Gets the private key as a [Base64] [String] */
    fun getPrivate() = Base64.getEncoder().encodeToString(private.encoded)!!
}