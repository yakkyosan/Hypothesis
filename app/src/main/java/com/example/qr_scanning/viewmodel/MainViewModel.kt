package com.example.qr_scanning.viewmodel

// MainActivityのデータ管理

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_scanning.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userPoints = MutableStateFlow(0)
    val userPoints: StateFlow<Int> get() = _userPoints

    init {
        fetchUserPoints()
    }

    private fun fetchUserPoints() {
        viewModelScope.launch {
            val user = userRepository.getUser(1) // ID 1のユーザーを取得
            _userPoints.value = user?.points ?: 0
        }
    }
}
