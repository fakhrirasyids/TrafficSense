package com.lazycode.trafficsense.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.data.models.CarpoolingItem
import com.lazycode.trafficsense.databinding.ItemAllCarpoolRowBinding

class AllCarpoolAdapter :
    ListAdapter<CarpoolingItem, AllCarpoolAdapter.MyViewHolder>(DIFF_CALLBACK) {
    var onApplyClick: ((Int,Int) -> Unit)? = null
    var onContactClick: ((String, String) -> Unit)? = null

    var onDepartureInfoClick: ((String, String, String) -> Unit)? = null
    var onVehicleInfoClick: ((String, String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemAllCarpoolRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    inner class MyViewHolder(val binding: ItemAllCarpoolRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(carpool: CarpoolingItem) {
            binding.apply {
                Glide.with(root)
                    .load(carpool.driver?.profilePictureUrl)
                    .into(ivCarpool)

                tvCarpoolDriverName.text = carpool.driver?.name

                var filledPassage = 0
                for (passenger in carpool.carpoolingPassangers!!) {
                    if (passenger!!.isApproved.toString().toInt() == 1) {
                        filledPassage += passenger.passageCount.toString().toInt()
                    }
                }

                if (filledPassage == carpool.capacity.toString().toInt()) {
                    carpool.status = "2"
                }

                tvCarpoolPassenger.text = StringBuilder("$filledPassage/${carpool.capacity}")
//                tvTimeDeparture.text = carpool.departureAt
//                tvDeparture.text = carpool.departureInfo
//                tvDestination.text = carpool.arriveInfo

                if (carpool.isMine!!) {
                    layoutBtn.isVisible = false
                    tvStatus.isVisible = true
                    tvStatus.text = StringBuilder("Saya")
                    tvStatus.background = ContextCompat.getDrawable(
                        root.context,
                        R.drawable.background_gradient_horizontal_blue
                    )
                } else {
                    when (carpool.status?.toInt()) {
                        1 -> {
                            tvStatus.isVisible = false
                            layoutBtn.isVisible = true
                        }

                        2 -> {
                            layoutBtn.isVisible = false
                            tvStatus.isVisible = true
                            tvStatus.text = StringBuilder("Penuh")
                            tvStatus.background = ContextCompat.getDrawable(
                                root.context,
                                R.drawable.background_gradient_horizontal_yellow
                            )
                        }

                        3 -> {
                            layoutBtn.isVisible = false
                            tvStatus.isVisible = true
                            tvStatus.text = StringBuilder("Berangkat")
                            tvStatus.background = ContextCompat.getDrawable(
                                root.context,
                                R.drawable.background_gradient_horizontal_green
                            )
                        }

                        4 -> {
                            layoutBtn.isVisible = false
                            tvStatus.isVisible = true
                            tvStatus.text = StringBuilder("Tiba")
                            tvStatus.background = ContextCompat.getDrawable(
                                root.context,
                                R.drawable.background_gradient_horizontal_green
                            )
                        }

                        5 -> {
                            layoutBtn.isVisible = false
                            tvStatus.isVisible = true
                            tvStatus.text = StringBuilder("Tiba")
                            tvStatus.background = ContextCompat.getDrawable(
                                root.context,
                                R.drawable.background_gradient_horizontal_red
                            )
                        }

                        else -> {
                            tvStatus.isVisible = false
                            layoutBtn.isVisible = true
                        }
                    }
                }

                btnApply.setOnClickListener {
                    onApplyClick?.invoke(carpool.id!!,carpool.vehicle?.capacity!!.toString().toInt())
                }
                btnContact.setOnClickListener {
                    onContactClick?.invoke(carpool.driver?.name!!, carpool.driver.phoneNumber!!)
                }

                btnDepartureIngo.setOnClickListener {
                    onDepartureInfoClick?.invoke(carpool.departureAt!!,carpool.departureInfo!!,carpool.arriveInfo!!)
                }

                btnVehicleInfo.setOnClickListener {
                    onVehicleInfoClick?.invoke(carpool.vehicle?.name!!,carpool.vehicle.capacity!!)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CarpoolingItem>() {
            override fun areItemsTheSame(
                oldItem: CarpoolingItem,
                newItem: CarpoolingItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CarpoolingItem,
                newItem: CarpoolingItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}