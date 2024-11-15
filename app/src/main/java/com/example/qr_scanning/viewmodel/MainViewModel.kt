package com.example.qr_scanning.viewmodel

// MainActivityのデータ管理

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_scanning.model.User
import com.example.qr_scanning.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userPoints = MutableStateFlow(0)
    val userPoints: StateFlow<Int> get() = _userPoints

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    init {
        fetchUserPoints()
    }

    init {
        loadUserProfile()
    }

    private fun fetchUserPoints() {
        viewModelScope.launch {
            val user = userRepository.getUser(1) // ID 1のユーザーを取得
            _userPoints.value = user?.points ?: 0
        }
    }
    fun loadUserProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            var user = userRepository.getUser(1)
            if (user == null) {
                user = User(name = "名前（未設定）", profileImageUrl = "", points = 0)
                userRepository.insertUser(user)
            }
            _userProfile.postValue(user)
        }
    }
}
