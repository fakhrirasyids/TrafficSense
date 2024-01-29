package com.lazycode.trafficsense.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lazycode.trafficsense.data.models.SavedTripItem
import com.lazycode.trafficsense.databinding.ItemSavedRouteRowBinding

class SavedRouteAdapter :
    ListAdapter<SavedTripItem, SavedRouteAdapter.MyViewHolder>(DIFF_CALLBACK) {
    var onItemClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemSavedRouteRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    inner class MyViewHolder(val binding: ItemSavedRouteRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(savedTripItem: SavedTripItem) {
            binding.apply {
                tvDepartureInfo.text = savedTripItem.departureInfo
                tvArriveInfo.text = savedTripItem.arriveInfo

                tvCoInfo.text = StringBuilder("CO : ${savedTripItem.coTotal}")

                root.setOnClickListener {
                    onItemClick?.invoke(savedTripItem.id!!)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SavedTripItem>() {
            override fun areItemsTheSame(oldItem: SavedTripItem, newItem: SavedTripItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SavedTripItem,
                newItem: SavedTripItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}