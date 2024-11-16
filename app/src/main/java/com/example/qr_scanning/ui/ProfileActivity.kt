package com.example.qr_scanning.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.qr_scanning.base.MyApplication
import com.example.qr_scanning.databinding.ActivityProfileBinding
import com.example.qr_scanning.viewmodel.ProfileViewModel
import com.example.qr_scanning.viewmodel.ViewModelFactory

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModelの取得
        val app = application as MyApplication
        val factory = ViewModelFactory(app.userRepository)
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        // プロフィール情報の初期化
        setupObservers()

        // ボタンのクリックリスナー設定
        setupListeners()
    }

    private fun setupObservers() {
        // プロフィール名を表示
        profileViewModel.userProfile.observe(this) { user ->
            if (user != null) {
                binding.etUserName.setText(user.name)
            } else {
                Toast.makeText(this, "ユーザー情報の取得に失敗しました", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        // プロフィール画像変更ボタン（何もしない）
        binding.btnChangeProfileImage.setOnClickListener {
            Toast.makeText(this, "画像の変更は現在サポートされていません", Toast.LENGTH_SHORT).show()
        }

        // 保存ボタン
        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etUserName.text.toString()
            if (newName.isNotBlank()) {
                profileViewModel.updateUserName(newName)
                Toast.makeText(this, "プロフィールが保存されました", Toast.LENGTH_SHORT).show()
                finish() // Activityを終了
            } else {
                Toast.makeText(this, "ユーザー名を入力してください", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
