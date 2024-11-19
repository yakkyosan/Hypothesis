package com.example.qr_scanning.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qr_scanning.base.MyApplication
import com.example.qr_scanning.databinding.ActivityRewardBinding
import com.example.qr_scanning.model.Item
import com.example.qr_scanning.model.User
import com.example.qr_scanning.viewmodel.RewardViewModel
import com.example.qr_scanning.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

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

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ボタンのクリックリスナー
        setupListeners()

        // ユーザー情報の取得
        loadUser()
    }

    private fun loadUser() {
        val userRepository = (application as MyApplication).localDatabaseService.userRepository
        lifecycleScope.launch {
            user = userRepository.getUser(1) ?: run {
                showToast("ユーザーが見つかりません")
                finish() // アクティビティを終了
                return@launch
            }
            // RecyclerView と Observers のセットアップ
            setupRecyclerView()
            setupObservers()
        }
    }

    private fun setupRecyclerView() {
        rewardItemAdapter = RewardItemAdapter(
            onExchangeClicked = { item -> handleExchangeClicked(item) },
            onDetailClicked = { item -> showItemDetails(item) },
            onCouponClicked = { item -> showCouponDialog(item) }
        )
        binding.recyclerViewItems.apply {
            layoutManager = LinearLayoutManager(this@RewardActivity)
            adapter = rewardItemAdapter
        }
    }

    private fun showItemDetails(item: Item) {
        val details = when (item.name) {
            "OMEGA3 Naturels EGG" -> "亜麻種子を飼料として与えた親鶏が産む卵で、体内の機能の働きを良くするオメガ３が含まれています。"
            "MILK’ORO Aging Yogurt" -> "オメガ３脂肪酸を多く含むジャージー牛乳を、そのまま発酵。生乳と甜菜糖のみで作る、完全無添加のヨーグルトです。"
            "弁天マンゴー" -> "赤く熟すほど芳醇でコクのある甘みが際立つ、アーウィン種のマンゴー。果肉に繊維がほとんどなく、甘くてジューシーです。"
            "いちじょう米" -> "九州沖縄農業研究センターが開発した、「にこまる」という品種のお米。ツヤがあり粘り強く、冷めても美味しいのが特徴です。"
            "大将マンゴー" -> "酸味の少ない、アーウィン種のアップルマンゴー。その中でも秀でた、大きさが3L～4Lで糖度16以上の逸品です。"
            else -> "詳細情報がありません。"
        }
        // ダイアログで表示
        AlertDialog.Builder(this)
            .setTitle(item.name)
            .setMessage(details)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun setupObservers() {
        // アイテムリストの監視
        rewardViewModel.itemList.observe(this) { items ->
            if (items.isNullOrEmpty()) {
                showToast("表示するアイテムがありません")
            } else {
                rewardItemAdapter.submitList(items)
            }
        }

        // ユーザーポイントの監視
        rewardViewModel.userPoints.observe(this) { points ->
            rewardItemAdapter.setUserPoints(points)
        }

        // メッセージの監視
        rewardViewModel.exchangeMessage.observe(this) { message ->
            showToast(message)
        }
    }

    private fun handleExchangeClicked(item:Item) {
        user?.let { currentUser ->
            if (currentUser.points >= item.requiredPoints) {
                confirmExchange(item)
            } else {
                showToast("ポイントが足りません！")
            }
        } ?: run {
            showToast("ユーザー情報が取得できていません")
        }
    }

    private fun confirmExchange(item: Item) {
        AlertDialog.Builder(this)
            .setTitle("${item.name} を交換しますか？")
            .setMessage(
                """
                所有ポイント: ${user?.points ?: "不明"}
                必要ポイント: ${item.requiredPoints}
                交換後のポイント数: ${user?.points?.minus(item.requiredPoints) ?: "不明"}
                """.trimIndent()
            )
            .setPositiveButton("交換する") { _, _ ->
                rewardViewModel.exchangeItem(item.id)
            }
            .setNegativeButton("キャンセル") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showCouponDialog(item: Item) {
        AlertDialog.Builder(this)
            .setTitle("クーポンを使用しますか？")
            .setMessage("クーポンを使用すると、特典を受け取ることができます。")
            .setPositiveButton("使用する") { _, _ ->
                rewardViewModel.useCoupon(item.id)
            }
            .setNegativeButton("キャンセル") { dialog, _ -> dialog.dismiss() }
            .show()
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
