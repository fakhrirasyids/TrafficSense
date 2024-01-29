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
import com.lazycode.trafficsense.databinding.ItemMyCarpoolRowBinding

class MyCarpoolAdapter :
    ListAdapter<CarpoolingItem, MyCarpoolAdapter.MyViewHolder>(DIFF_CALLBACK) {
    var onApplyClick: ((Int) -> Unit)? = null
    var onUpdateClick: ((Int, Int) -> Unit)? = null

    var onDepartureInfoClick: ((String, String, String) -> Unit)? = null
    var onVehicleInfoClick: ((String, String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemMyCarpoolRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    inner class MyViewHolder(val binding: ItemMyCarpoolRowBinding) :
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

                when (carpool.status?.toInt()) {
                    1 -> {
                        tvStatus.text = StringBuilder("Iklan")
                        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.blue))
                        tvStatus.background = ContextCompat.getDrawable(
                            root.context,
                            R.drawable.background_outlined_textview_blue
                        )
                    }

                    2 -> {
                        tvStatus.text = StringBuilder("Penuh")
                        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.yellow))
                        tvStatus.background = ContextCompat.getDrawable(
                            root.context,
                            R.drawable.background_outlined_textview_yellow
                        )
                    }

                    3 -> {
                        tvStatus.text = StringBuilder("Berangkat")
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                root.context,
                                android.R.color.holo_green_dark
                            )
                        )
                        tvStatus.background = ContextCompat.getDrawable(
                            root.context,
                            R.drawable.background_outlined_textview_green
                        )

                        layoutBtnCheck.isVisible = false
                    }

                    4 -> {
                        tvStatus.text = StringBuilder("Tiba")
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                root.context,
                                android.R.color.holo_green_dark
                            )
                        )
                        tvStatus.background = ContextCompat.getDrawable(
                            root.context,
                            R.drawable.background_outlined_textview_green
                        )

                        layoutBtnCheck.isVisible = false
                    }

                    5 -> {
                        tvStatus.text = StringBuilder("Selesai")
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                root.context,
                                android.R.color.holo_red_dark
                            )
                        )
                        tvStatus.background = ContextCompat.getDrawable(
                            root.context,
                            R.drawable.background_outlined_textview_red
                        )

                        layoutBtnCheck.isVisible = false
                        layoutBtnContact.isVisible = false
                    }
                }

//                tvTimeDeparture.text = carpool.departureAt
//                tvDeparture.text = carpool.departureInfo
//                tvDestination.text = carpool.arriveInfo

                btnCek.setOnClickListener {
                    onApplyClick?.invoke(carpool.id!!)
                }

                btnContact.setOnClickListener {
                    onUpdateClick?.invoke(
                        carpool.id.toString().toInt(),
                        carpool.status.toString().toInt()
                    )
                }

                btnDepartureIngo.setOnClickListener {
                    onDepartureInfoClick?.invoke(
                        carpool.departureAt!!,
                        carpool.departureInfo!!,
                        carpool.arriveInfo!!
                    )
                }

                btnVehicleInfo.setOnClickListener {
                    onVehicleInfoClick?.invoke(carpool.vehicle?.name!!, carpool.vehicle.capacity!!)
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