package com.kiras.auth.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    private val hasher = PasswordHasher()

    @Test
    fun `hash verifies against the same password`() = runTest {
        val hash = hasher.hash("Secret123")

        assertTrue(hasher.verify("Secret123", hash))
    }

    @Test
    fun `hash does not verify against a different password`() = runTest {
        val hash = hasher.hash("Secret123")

        assertFalse(hasher.verify("secret123", hash))
        assertFalse(hasher.verify("Secret123 ", hash))
    }

    @Test
    fun `hash never contains the raw password and is salted`() = runTest {
        val first = hasher.hash("Secret123")
        val second = hasher.hash("Secret123")

        assertFalse(first.contains("Secret123"))
        assertNotEquals(first, second)
        assertTrue(first.startsWith("pbkdf2_sha256$210000$"))
    }

    @Test
    fun `malformed stored hash is rejected`() = runTest {
        assertFalse(hasher.verify("Secret123", "Secret123"))
        assertFalse(hasher.verify("Secret123", "pbkdf2_sha256\$abc\$!!\$!!"))
    }
}
