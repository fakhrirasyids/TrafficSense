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
import com.lazycode.trafficsense.data.models.DriverPassengerItem
import com.lazycode.trafficsense.databinding.ItemPassengerRowBinding
import java.text.NumberFormat
import java.util.Locale

class DriverPassengerAdapter :
    ListAdapter<DriverPassengerItem, DriverPassengerAdapter.MyViewHolder>(DIFF_CALLBACK) {
    var onContactClick: ((String, String) -> Unit)? = null

    var onJemputClick: ((String, String) -> Unit)? = null
    var onUpdatePriceClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemPassengerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    inner class MyViewHolder(val binding: ItemPassengerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(driverPassengerItem: DriverPassengerItem) {
            binding.apply {
                Glide.with(root)
                    .load(driverPassengerItem.passangerData?.profilePictureUrl)
                    .into(ivPassenger)

                tvPassengerName.text = driverPassengerItem.passangerData?.name
                tvPassengerCapacity.text =
                    StringBuilder("Pengajuan Kapasitas : ${driverPassengerItem.passageCount}")

                val numberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

                if (driverPassengerItem.status?.toInt() == 1) {
                    if (driverPassengerItem.price == null) {
                        tvStatus.text = StringBuilder("Negosiasi")
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
                        tvPassengerPrice.isVisible = false
                        layoutBtnNegosiasi.isVisible = true
                    } else {
                        tvStatus.text = StringBuilder("Waiting")
                        tvPassengerPrice.text =
                            StringBuilder(numberFormat.format(driverPassengerItem.price.toString().toDouble()))
                        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.yellow))
                        tvStatus.background = ContextCompat.getDrawable(
                            root.context,
                            R.drawable.background_outlined_textview_yellow
                        )
                        layoutBtnNegosiasi.isVisible = false
                        tvPassengerPrice.isVisible = true
                    }
                } else {
                    tvStatus.text = StringBuilder("Diterima")
                    tvPassengerPrice.text =
                        StringBuilder(numberFormat.format(driverPassengerItem.price.toString().toDouble()))
                    tvStatus.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            android.R.color.white
                        )
                    )
                    tvStatus.background = ContextCompat.getDrawable(
                        root.context,
                        R.drawable.background_btn_green
                    )
                    layoutBtnNegosiasi.isVisible = false
                    tvPassengerPrice.isVisible = true
                }

//                when (driverPassengerItem.status?.toInt()) {
//                    1 -> {
//                        tvStatus.text = StringBuilder("Negosiasi")
//                        tvStatus.setTextColor(
//                            ContextCompat.getColor(
//                                root.context,
//                                android.R.color.holo_green_dark
//                            )
//                        )
//                        tvStatus.background = ContextCompat.getDrawable(
//                            root.context,
//                            R.drawable.background_outlined_textview_green
//                        )
//                        layoutBtnNegosiasi.isVisible = true
//                    }
//
//                    2 -> {
//                        tvStatus.text = StringBuilder("Deal Harga")
//                        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.yellow))
//                        tvStatus.background = ContextCompat.getDrawable(
//                            root.context,
//                            R.drawable.background_outlined_textview_yellow
//                        )
//                        layoutBtnNegosiasi.isVisible = false
//                    }
//
//                    3 -> {
//                        tvStatus.text = StringBuilder("Diterima")
//                        tvStatus.setTextColor(
//                            ContextCompat.getColor(
//                                root.context,
//                                android.R.color.white
//                            )
//                        )
//                        tvStatus.background = ContextCompat.getDrawable(
//                            root.context,
//                            R.drawable.background_btn_green
//                        )
//                        layoutBtnNegosiasi.isVisible = false
//                    }
//                }

//                tvTimeDeparture.text = carpool.departureAt
//                tvDeparture.text = carpool.departureInfo
//                tvDestination.text = carpool.arriveInfo

//                btnApply.setOnClickListener {
//                    onApplyClick?.invoke(carpool)
//                }
                btnContact.setOnClickListener {
                    onContactClick?.invoke(
                        driverPassengerItem.passangerData?.name.toString(),
                        driverPassengerItem.passangerData?.phoneNumber.toString()
                    )
                }

                btnDepartureIngo.setOnClickListener {
                    onJemputClick?.invoke(
                        driverPassengerItem.pickInfo!!,
                        driverPassengerItem.dropInfo!!
                    )
                }

                btnNegosiasi.setOnClickListener {
                    onUpdatePriceClick?.invoke(driverPassengerItem.id!!)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DriverPassengerItem>() {
            override fun areItemsTheSame(
                oldItem: DriverPassengerItem,
                newItem: DriverPassengerItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DriverPassengerItem,
                newItem: DriverPassengerItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}