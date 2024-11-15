package com.example.qr_scanning.ui

// ホーム画面です！！
// activity_main.xmlを使います

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.qr_scanning.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest

import com.example.qr_scanning.viewmodel.MainViewModel
import com.example.qr_scanning.viewmodel.ViewModelFactory
import com.example.qr_scanning.base.MyApplication

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // MainViewModelをインスタンス化（by viewModelsを使用）
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory((application as MyApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModelからポイントデータを監視し、表示を更新
        observeUserPoints()

        // 各ボタンのリスナーを設定
        binding.btnScanQr.setOnClickListener {
            navigateToQrActivity()
        }

        binding.btnRewards.setOnClickListener {
            navigateToRewardActivity()
        }

        binding.btnProfile.setOnClickListener {
            navigateToProfileActivity()
        }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("DEPRECATION")
    private fun observeUserPoints() {
        lifecycleScope.launchWhenStarted {
            mainViewModel.userPoints.collectLatest { points ->
                binding.tvPoints.text = "ポイント: $points"
            }
        }
    }

    private fun navigateToQrActivity() {
        val intent = Intent(this, QrActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRewardActivity() {
        val intent = Intent(this, RewardActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}


