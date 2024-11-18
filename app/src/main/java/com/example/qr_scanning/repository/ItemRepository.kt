// ItemRepository.kt
package com.example.qr_scanning.repository

import android.util.Log
import com.example.qr_scanning.dao.ItemDao
import com.example.qr_scanning.model.Item

// 商品関連のデータ操作
class ItemRepository(private val itemDao: ItemDao) {

    // すべてのアイテムを取得するメソッド
    fun getAllItems(): List<Item> {
        val items = itemDao.getAllItems()
        Log.d("ItemRepository", "Fetched items: $items")
        return items
    }

    // 特定のアイテムをIDで取得するメソッド
    fun getItemById(itemId: Int): Item? {
        return itemDao.getItemById(itemId)
    }

    // 新しいアイテムを追加するメソッド
    fun insertItem(item: Item) {
        itemDao.insert(item)
    }

    // アイテムの更新
    fun updateItem(item: Item) {
        itemDao.update(item)
    }
}
