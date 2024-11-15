package com.example.qr_scanning.viewmodel

// ProfileActivityのデータ管理

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_scanning.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> get() = _userName

    private val _profileImage = MutableStateFlow<String?>(null)
    val profileImage: StateFlow<String?> get() = _profileImage

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val user = userRepository.getUser(1)
            _userName.value = user?.name
            _profileImage.value = user?.profileImageUrl
        }
    }

    fun updateUserProfile(name: String, imageUrl: String) {
        viewModelScope.launch {
            val user = userRepository.getUser(1)
            user?.let {
                it.name = name
                it.profileImageUrl = imageUrl
                userRepository.updateUser(it)
                _userName.value = name
                _profileImage.value = imageUrl
            }
        }
    }
}
