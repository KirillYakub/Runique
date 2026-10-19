package com.kiras.auth.data

import com.kiras.core.database.dao.UserDao
import com.kiras.core.database.entity.UserEntity
import com.kiras.core.domain.AuthInfo
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthRepositoryImplTest {

    private val userDao = FakeUserDao()
    private val sessionStorage = FakeSessionStorage()
    private val repository = AuthRepositoryImpl(
        userDao = userDao,
        sessionStorage = sessionStorage,
        passwordHasher = PasswordHasher(iterations = 1_000)
    )

    @Test
    fun `register stores a hashed user and starts a session`() = runTest {
        val result = repository.register("Runner@Mail.com ", "Secret123")

        assertEquals(Result.Success(Unit), result)
        val user = userDao.users.single()
        assertEquals("runner@mail.com", user.email)
        assertNotEquals("Secret123", user.passwordHash)
        assertEquals(AuthInfo("local", "local", user.userId), sessionStorage.info)
    }

    @Test
    fun `register with an existing email returns conflict`() = runTest {
        repository.register("runner@mail.com", "Secret123")
        sessionStorage.set(null)

        val result = repository.register("RUNNER@mail.com", "Other123")

        assertEquals(Result.Error(DataError.Local.CONFLICT), result)
        assertEquals(1, userDao.users.size)
        assertNull(sessionStorage.info)
    }

    @Test
    fun `login with correct credentials starts a session`() = runTest {
        repository.register("runner@mail.com", "Secret123")
        sessionStorage.set(null)

        val result = repository.login("Runner@mail.com", "Secret123")

        assertEquals(Result.Success(Unit), result)
        assertEquals(userDao.users.single().userId, sessionStorage.info?.userId)
    }

    @Test
    fun `login with wrong password returns invalid credentials`() = runTest {
        repository.register("runner@mail.com", "Secret123")
        sessionStorage.set(null)

        val result = repository.login("runner@mail.com", "Wrong123")

        assertEquals(Result.Error(DataError.Local.INVALID_CREDENTIALS), result)
        assertNull(sessionStorage.info)
    }

    @Test
    fun `login with unknown email returns invalid credentials`() = runTest {
        val result = repository.login("nobody@mail.com", "Secret123")

        assertEquals(Result.Error(DataError.Local.INVALID_CREDENTIALS), result)
        assertNull(sessionStorage.info)
    }

    private class FakeUserDao : UserDao {
        val users = mutableListOf<UserEntity>()

        override suspend fun insertUser(user: UserEntity) {
            users += user
        }

        override suspend fun getUserByEmail(email: String): UserEntity? {
            return users.firstOrNull { it.email == email }
        }

        override suspend fun getUserById(userId: String): UserEntity? {
            return users.firstOrNull { it.userId == userId }
        }
    }

    private class FakeSessionStorage : SessionStorage {
        var info: AuthInfo? = null

        override suspend fun get(): AuthInfo? = info

        override suspend fun set(info: AuthInfo?) {
            this.info = info
        }
    }
}
