package com.kiras.auth.data

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteFullException
import com.kiras.auth.data.mappers.toAuthInfo
import com.kiras.auth.domain.AuthRepository
import com.kiras.core.database.dao.UserDao
import com.kiras.core.database.entity.UserEntity
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.EmptyResult
import com.kiras.core.domain.util.Result

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val sessionStorage: SessionStorage,
    private val passwordHasher: PasswordHasher
): AuthRepository {

    private fun String.normalizeEmail(): String = trim().lowercase()

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
        username: String,
        password: String
    ): EmptyResult<DataError.Local> {
        val normalizedEmail = email.normalizeEmail()
        if(userDao.getUserByEmail(normalizedEmail) != null) {
            return Result.Error(DataError.Local.CONFLICT)
        }
        val user = UserEntity(
            email = normalizedEmail,
            username = username.trim(),
            passwordHash = passwordHasher.hash(password)
        )
        try {
            userDao.insertUser(user)
        } catch (e: SQLiteConstraintException) {
            return Result.Error(DataError.Local.CONFLICT)
        } catch (e: SQLiteFullException) {
            return Result.Error(DataError.Local.DISC_FULL)
        }
        sessionStorage.set(user.toAuthInfo())
        return Result.Success(Unit)
    }
}
