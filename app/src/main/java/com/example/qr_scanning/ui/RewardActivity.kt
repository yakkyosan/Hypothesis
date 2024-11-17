package com.example.qr_scanning.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qr_scanning.base.MyApplication
import com.example.qr_scanning.databinding.ActivityRewardBinding
import com.example.qr_scanning.viewmodel.RewardViewModel
import com.example.qr_scanning.viewmodel.ViewModelFactory

class RewardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardBinding
    private lateinit var rewardItemAdapter: RewardItemAdapter

    // ViewModel の初期化
    val rewardViewModel: RewardViewModel by viewModels {
        val app = application as MyApplication
        ViewModelFactory(
            userRepository = app.localDatabaseService.userRepository,
            itemRepository = app.localDatabaseService.itemRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView のセットアップ
        setupRecyclerView()

        // LiveData の監視
        setupObservers()

        // ボタンのクリックリスナー
        setupListeners()
    }

    private fun setupRecyclerView() {
        rewardItemAdapter = RewardItemAdapter { itemId ->
            rewardViewModel.exchangeItem(itemId)
        }
        binding.recyclerViewItems.apply {
            layoutManager = LinearLayoutManager(this@RewardActivity)
            adapter = rewardItemAdapter
        }
    }

    private fun setupObservers() {
        // アイテムリストの監視
        rewardViewModel.itemList.observe(this) { items ->
            // RecyclerViewに反映
            val adapter = RewardItemAdapter { itemId ->
                rewardViewModel.exchangeItem(itemId)
            }
            binding.recyclerViewItems.adapter = adapter
            adapter.submitList(items)
        }

        // メッセージの監視
        rewardViewModel.exchangeMessage.observe(this) { message ->
            showToast(message)
        }
    }

    private fun setupListeners() {
        binding.btnBackToMain.setOnClickListener {
            finish() // メイン画面に戻る
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
