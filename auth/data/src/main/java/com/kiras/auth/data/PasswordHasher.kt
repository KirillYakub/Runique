package com.kiras.auth.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Salted PBKDF2-HMAC-SHA256 password hashing.
 *
 * Hashes are stored as `pbkdf2_sha256$<iterations>$<base64 salt>$<base64 hash>` so the
 * parameters travel with the hash and can be raised later without breaking existing users.
 */
class PasswordHasher(
    private val iterations: Int = DEFAULT_ITERATIONS
) {

    private val secureRandom = SecureRandom()

    suspend fun hash(password: String): String = withContext(Dispatchers.Default) {
        val salt = ByteArray(SALT_LENGTH_BYTES).also(secureRandom::nextBytes)
        val hash = pbkdf2(password, salt, iterations, KEY_LENGTH_BITS)
        listOf(
            ALGORITHM_ID,
            iterations.toString(),
            encoder.encodeToString(salt),
            encoder.encodeToString(hash)
        ).joinToString(SEPARATOR)
    }

    suspend fun verify(password: String, storedHash: String): Boolean = withContext(Dispatchers.Default) {
        val parts = storedHash.split(SEPARATOR)
        if(parts.size != 4 || parts[0] != ALGORITHM_ID) {
            return@withContext false
        }
        val storedIterations = parts[1].toIntOrNull() ?: return@withContext false
        val salt: ByteArray
        val expected: ByteArray
        try {
            salt = decoder.decode(parts[2])
            expected = decoder.decode(parts[3])
        } catch (e: IllegalArgumentException) {
            return@withContext false
        }
        val actual = pbkdf2(password, salt, storedIterations, expected.size * 8)
        MessageDigest.isEqual(expected, actual)
    }

    private fun pbkdf2(password: String, salt: ByteArray, iterations: Int, keyLengthBits: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLengthBits)
        return try {
            SecretKeyFactory.getInstance(PBKDF2_ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private companion object {
        const val ALGORITHM_ID = "pbkdf2_sha256"
        const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
        const val SEPARATOR = "$"
        const val DEFAULT_ITERATIONS = 210_000
        const val SALT_LENGTH_BYTES = 16
        const val KEY_LENGTH_BITS = 256

        val encoder: Base64.Encoder = Base64.getEncoder()
        val decoder: Base64.Decoder = Base64.getDecoder()
    }
}
