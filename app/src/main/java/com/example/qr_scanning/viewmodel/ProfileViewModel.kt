package com.example.qr_scanning.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qr_scanning.model.User
import com.example.qr_scanning.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile // LiveDataとして公開

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = userRepository.getUser(1) // 仮のIDでユーザーを取得
                _userProfile.postValue(user)
            } catch (e: Exception) {
                // エラー発生時の処理
                _userProfile.postValue(null)
            }
        }
    }

    fun updateUserName(newName: String) {
        val currentUser = _userProfile.value
        if (currentUser != null) {
            val updatedUser = currentUser.copy(name = newName)
            CoroutineScope(Dispatchers.IO).launch {
                userRepository.updateUser(updatedUser)
                loadUserProfile()
            }
        }
    }
}
