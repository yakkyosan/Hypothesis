// UserRepository.kt
package com.example.qr_scanning.repository

import androidx.lifecycle.LiveData
import com.example.qr_scanning.dao.UserDao
import com.example.qr_scanning.model.User

class UserRepository(private val userDao: UserDao) {

    /**
     * ユーザーをIDで取得する
     * @param userId: ユーザーID
     * @return User オブジェクトまたは null
     */
    suspend fun getUser(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    /**
     * 新しいユーザーを追加する
     * @param user: 追加するUserオブジェクト
     */
    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    /**
     * 既存のユーザー情報を更新する
     * @param user: 更新するUserオブジェクト
     */
    suspend fun updateUser(user: User) {
        userDao.update(user)
    }
}
