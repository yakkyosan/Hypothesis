package com.example.qr_scanning.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_scanning.model.Item
import com.example.qr_scanning.repository.ItemRepository
import com.example.qr_scanning.repository.UserRepository
import kotlinx.coroutines.launch

class RewardViewModel(
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _itemList = MutableLiveData<List<Item>>()
    val itemList: LiveData<List<Item>> get() = _itemList

    private val _exchangeMessage = MutableLiveData<String>()
    val exchangeMessage: LiveData<String> get() = _exchangeMessage

    private val _userPoints = MutableLiveData<Int>()
    val userPoints: LiveData<Int> get() = _userPoints

    init {
        loadItems()
        loadUserPoints()
    }

    private fun loadItems() {
        viewModelScope.launch {
            val items = itemRepository.getAllItems()
            if (items.isEmpty()) {
                _exchangeMessage.postValue("アイテムデータがありません")
            } else {
                _itemList.postValue(items)
            }
        }
    }

    private fun loadUserPoints() {
        viewModelScope.launch {
            val user = userRepository.getUser(1)
            user?.let {
                _userPoints.postValue(it.points)
            } ?: run {
                _exchangeMessage.postValue("ユーザーが見つかりません")
            }
        }
    }

    fun exchangeItem(itemId: Int) {
        viewModelScope.launch {
            try {
                val item = itemRepository.getItemById(itemId)
                val user = userRepository.getUser(1)

                if (item != null && user != null) {
                    if (user.points >= item.requiredPoints) {
                        // ポイントを消費して交換
                        user.points -= item.requiredPoints
                        userRepository.updateUser(user)
                        _exchangeMessage.postValue("${item.name} を交換しました！")

                        // exchangeStatus を 1（交換済み）に設定
                        item.exchangeStatus = 1
                        itemRepository.updateItem(item)

                        // ユーザーポイントを更新
                        _userPoints.postValue(user.points)

                        // アイテムリストを再読み込みしてUIを更新
                        loadItems()
                    } else {
                        // ポイント不足のメッセージ
                        _exchangeMessage.postValue("ポイントが足りません！")
                    }
                } else {
                    _exchangeMessage.postValue("交換に失敗しました。")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _exchangeMessage.postValue("エラーが発生しました。")
            }
        }
    }

    fun useCoupon(itemId: Int) {
        viewModelScope.launch {
            val item = itemRepository.getItemById(itemId)
            if (item != null && item.exchangeStatus == 1) {
                // exchangeStatus を 2（クーポン使用済み）に設定
                item.exchangeStatus = 2
                itemRepository.updateItem(item)
                _exchangeMessage.postValue("クーポンを使用しました！")

                // アイテムリストを再読み込みしてUIを更新
                loadItems()
            } else {
                _exchangeMessage.postValue("クーポンを使用できません。")
            }
        }
    }
}
