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

    private val _userPoints = MutableLiveData<Int>()
    val userPoints: LiveData<Int> get() = _userPoints

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            var user = userRepository.getUser(1)
            if (user == null) {
                user = User(name = "名前（未設定）", profileImageUrl = "", points = 0)
                userRepository.insertUser(user)
            }
            _userProfile.postValue(user)
            _userPoints.postValue(user.points)
        }
    }
}
