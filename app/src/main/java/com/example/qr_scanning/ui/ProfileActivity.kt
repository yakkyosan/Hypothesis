// ProfileActivity.kt
package com.example.qr_scanning.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.qr_scanning.R
import com.example.qr_scanning.base.MyApplication
import com.example.qr_scanning.databinding.ActivityProfileBinding
import com.example.qr_scanning.viewmodel.ProfileViewModel
import com.example.qr_scanning.viewmodel.ViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    // クラス変数としてProfileViewModelを初期化
    private val profileViewModel: ProfileViewModel by viewModels {
        val app = application as MyApplication
        ViewModelFactory(
            userRepository = app.localDatabaseService.userRepository
        )
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                if (!user.profileImageUrl.isNullOrEmpty()) {
                    binding.imgProfile.setImageURI(Uri.parse(user.profileImageUrl))
                } else {
                    binding.imgProfile.setImageResource(R.drawable.ic_default_profile)
                }
            } else {
                Toast.makeText(this, "ユーザー情報の取得に失敗しました", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        // プロフィール画像変更ボタン（何もしない）
        binding.btnChangeProfileImage.setOnClickListener {
            openImagePicker()
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

    @Suppress("DEPRECATION")
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // Intent to pick image from gallery
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "画像を選択"), PICK_IMAGE_REQUEST)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    val imageUri: Uri? = data.data
                    imageUri?.let {
                        binding.imgProfile.setImageURI(it)
                        // 画像を内部ストレージに保存し、URIを取得
                        val savedUri = saveImageToInternalStorage(it)
                        savedUri?.let { uri ->
                            // ViewModelを通じてデータベースを更新
                            profileViewModel.updateProfileImage(uri.toString())
                        } ?: run {
                            Toast.makeText(this, "画像の保存に失敗しました", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun saveImageToInternalStorage(uri: Uri): Uri? {
        try {
            // 画像をビットマップとして取得
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            // アプリの内部ストレージのディレクトリを取得
            val directory = File(filesDir, "profile_images")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            // 画像を保存するファイルを作成
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(directory, fileName)
            val fos = FileOutputStream(file)
            // ビットマップをJPEG形式で圧縮して保存
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            // 保存したファイルのURIを返す
            return Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}
