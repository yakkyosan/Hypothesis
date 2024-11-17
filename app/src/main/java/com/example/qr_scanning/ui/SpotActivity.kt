// SpotActivity.kt
package com.example.qr_scanning.ui

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.qr_scanning.R
import com.example.qr_scanning.databinding.ActivitySpotBinding
import com.example.qr_scanning.model.Spot

class SpotActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpotBinding
    private lateinit var spotList: List<Spot>
    private var isDialogShown = false // ダイアログ表示状態を保持

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 戻るボタンの設定
        binding.btnBack.setOnClickListener {
            finish()
        }

        // スポットデータの取得
        spotList = loadSpotData()

        // リストビューの設定
        val adapter = SpotAdapter(this, spotList)
        binding.listViewSpots.adapter = adapter

        // リストアイテムのクリックイベント
        binding.listViewSpots.setOnItemClickListener { _, _, position, _ ->
            val spot = spotList[position]
            showConfirmationDialog(spot)
        }
    }

    private fun loadSpotData(): List<Spot> {
        val names = resources.getStringArray(R.array.spot_names)
        val addresses = resources.getStringArray(R.array.spot_addresses)
        val mapUrls = resources.getStringArray(R.array.spot_map_urls)

        val spotList = mutableListOf<Spot>()
        for (i in names.indices) {
            val spot = Spot(names[i], addresses[i], mapUrls[i])
            spotList.add(spot)
        }
        return spotList
    }

    private fun showConfirmationDialog(spot: Spot) {
        if (isDialogShown) return // 既にダイアログが表示されている場合は何もしない

        val builder = AlertDialog.Builder(this)
        builder.setTitle(spot.name)
        builder.setMessage("Googleマップを開きますか？")
        builder.setPositiveButton("開く") { _, _ ->
            isDialogShown = true
            openMap(spot.mapUrl)
        }
        builder.setNegativeButton("キャンセル") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.setOnDismissListener {
            isDialogShown = false
        }
        alertDialog.show()
        isDialogShown = true
    }

    private fun openMap(mapUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
        startActivity(intent)
    }
}
