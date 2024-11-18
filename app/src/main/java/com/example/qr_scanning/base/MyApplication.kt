// MyApplication.kt
package com.example.qr_scanning.base

import android.app.Application
import android.content.res.TypedArray
import android.util.Log
import com.example.qr_scanning.R
import com.example.qr_scanning.database.LocalDatabaseService
import com.example.qr_scanning.model.Item
import com.example.qr_scanning.repository.ItemRepository
import com.example.qr_scanning.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {

    // ローカルデータベースサービスの共有インスタンス
    lateinit var localDatabaseService: LocalDatabaseService

    override fun onCreate() {
        super.onCreate()

        // ローカルデータベースの初期化
        localDatabaseService = LocalDatabaseService(this)

        // アイテムデータの初期化
        initializeItems()

        val items = localDatabaseService.itemRepository.getAllItems()
        Log.d("MyApplication", "Database items after initialization: $items")
    }

    private fun initializeItems() {
        val itemRepository = localDatabaseService.itemRepository

        // データベースにデータがない場合のみ初期データを追加
        CoroutineScope(Dispatchers.IO).launch {
            if (itemRepository.getAllItems().isEmpty()) {
                val ids: IntArray = resources.getIntArray(R.array.item_ids)
                val names: Array<String> = resources.getStringArray(R.array.item_names)
                val points: IntArray = resources.getIntArray(R.array.item_points)
                val images: TypedArray = resources.obtainTypedArray(R.array.item_images)

                for (i in ids.indices) {
                    val item = Item(
                        ids[i],
                        names[i],
                        points[i],
                        images.getResourceId(i, 0),
                        0
                    )
                    itemRepository.insertItem(item)
                }
                images.recycle() // TypedArrayのリソースを解放

                val items = itemRepository.getAllItems()
                Log.d("MyApplication", "Inserted items: $items")
            }
        }
    }
}
