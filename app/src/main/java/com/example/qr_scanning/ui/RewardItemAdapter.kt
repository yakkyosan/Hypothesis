package com.example.qr_scanning.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qr_scanning.databinding.FragmentRewardDetailBinding
import com.example.qr_scanning.model.Item

class RewardItemAdapter(
    private val onExchangeClicked: (Item) -> Unit,
    private val onDetailClicked: (Item) -> Unit,
    private val onCouponClicked: (Item) -> Unit // クーポン用コールバック
) : ListAdapter<Item, RewardItemAdapter.RewardItemViewHolder>(DiffCallback) {

    private var userPoints: Int = 0

    fun setUserPoints(points: Int) {
        userPoints = points
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardItemViewHolder {
        val binding = FragmentRewardDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RewardItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RewardItemViewHolder, position: Int) {
        holder.bind(getItem(position), onExchangeClicked, onDetailClicked, onCouponClicked, userPoints)
    }

    class RewardItemViewHolder(
        private val binding: FragmentRewardDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Item,
            onExchangeClicked: (Item) -> Unit,
            onDetailClicked: (Item) -> Unit,
            onCouponClicked: (Item) -> Unit,
            userPoints: Int
        ) {
            binding.tvItemName.text = item.name
            binding.tvItemPoints.text = "必要ポイント：${item.requiredPoints}"
            binding.tvItemPoints.setTextColor(
                if (item.requiredPoints > userPoints) Color.RED else Color.BLACK
            )
            binding.imgItem.setImageResource(item.imageResId)

            binding.btnDetails.setOnClickListener { onDetailClicked(item) }

            when (item.exchangeStatus) {
                0 -> { // 未交換
                    binding.btnExchange.text = "交換"
                    binding.btnExchange.setOnClickListener { onExchangeClicked(item) }
                }
                1 -> { // 交換済み（クーポン未使用）
                    binding.btnExchange.text = "クーポン表示"
                    binding.btnExchange.setOnClickListener { onCouponClicked(item) }
                }
                2 -> { // クーポン使用済み
                    binding.btnExchange.text = "使用済み"
                    binding.btnExchange.isEnabled = false
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }
}
