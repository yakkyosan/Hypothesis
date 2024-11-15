package com.example.qr_scanning.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qr_scanning.databinding.FragmentRewardDetailBinding
import com.example.qr_scanning.model.Item

class RewardItemAdapter(
    private val onItemClicked: (Int) -> Unit
) : ListAdapter<Item, RewardItemAdapter.RewardItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardItemViewHolder {
        // fragment_reward_detail.xml をバインド
        val binding = FragmentRewardDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RewardItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RewardItemViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClicked)
    }

    class RewardItemViewHolder(
        private val binding: FragmentRewardDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, onItemClicked: (Int) -> Unit) {
            binding.tvItemName.text = item.name
            binding.tvItemPoints.text = "必要ポイント: ${item.requiredPoints}"
            binding.imgItem.setImageResource(item.imageResId) // 仮にリソースIDが含まれる場合
            binding.btnExchange.setOnClickListener {
                onItemClicked(item.id) // 交換ボタンがクリックされたときの処理
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }
}
