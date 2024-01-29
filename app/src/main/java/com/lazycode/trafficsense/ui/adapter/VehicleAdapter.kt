package com.lazycode.trafficsense.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lazycode.trafficsense.data.models.VehicleItem
import com.lazycode.trafficsense.databinding.ItemVehicleRowBinding

class VehicleAdapter : ListAdapter<VehicleItem, VehicleAdapter.MyViewHolder>(DIFF_CALLBACK) {
    var onDeleteClick: ((Int, String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemVehicleRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    inner class MyViewHolder(val binding: ItemVehicleRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(vehicle: VehicleItem) {
            binding.apply {
                Glide.with(root)
                    .load(vehicle.vehicleImages!![0]?.imageUrl)
                    .into(ivVehicle)

                tvVehicleName.text = vehicle.name
                tvVehicleCapacity.text = StringBuilder("Kapasitas : ${vehicle.capacity}")

                btnDelete.setOnClickListener {
                    onDeleteClick?.invoke(vehicle.id!!, vehicle.name!!)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VehicleItem>() {
            override fun areItemsTheSame(oldItem: VehicleItem, newItem: VehicleItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VehicleItem, newItem: VehicleItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}