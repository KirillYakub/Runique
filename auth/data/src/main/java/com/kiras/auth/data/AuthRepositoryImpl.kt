package com.kiras.auth.data

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteFullException
import com.kiras.auth.domain.AuthRepository
import com.kiras.core.database.dao.UserDao
import com.kiras.core.database.entity.UserEntity
import com.kiras.core.domain.AuthInfo
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.EmptyResult
import com.kiras.core.domain.util.Result

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val sessionStorage: SessionStorage,
    private val passwordHasher: PasswordHasher
): AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): EmptyResult<DataError.Local> {
        val user = userDao.getUserByEmail(email.normalizeEmail())
        if(user == null || !passwordHasher.verify(password, user.passwordHash)) {
            return Result.Error(DataError.Local.INVALID_CREDENTIALS)
        }
        sessionStorage.set(user.toAuthInfo())
        return Result.Success(Unit)
    }

    override suspend fun register(
        email: String,
        password: String
    ): EmptyResult<DataError.Local> {
        val normalizedEmail = email.normalizeEmail()
        if(userDao.getUserByEmail(normalizedEmail) != null) {
            return Result.Error(DataError.Local.CONFLICT)
        }
        val user = UserEntity(
            email = normalizedEmail,
            passwordHash = passwordHasher.hash(password)
        )
        try {
            userDao.insertUser(user)
        } catch (e: SQLiteConstraintException) {
            // The unique email index is the final guard against concurrent registrations.
            return Result.Error(DataError.Local.CONFLICT)
        } catch (e: SQLiteFullException) {
            return Result.Error(DataError.Local.DISC_FULL)
        }
        sessionStorage.set(user.toAuthInfo())
        return Result.Success(Unit)
    }

    private fun String.normalizeEmail(): String = trim().lowercase()

    private fun UserEntity.toAuthInfo(): AuthInfo {
        return AuthInfo(
            accessToken = LOCAL_TOKEN,
            refreshToken = LOCAL_TOKEN,
            userId = userId
        )
    }

    private companion object {
        const val LOCAL_TOKEN = "local"
    }
}
