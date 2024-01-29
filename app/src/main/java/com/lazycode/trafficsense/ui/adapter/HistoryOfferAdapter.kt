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
import com.lazycode.trafficsense.data.models.PayloadItemHistory
import com.lazycode.trafficsense.databinding.ItemHistoryPassengerRowBinding
import java.text.NumberFormat
import java.util.Locale

class HistoryOfferAdapter :
    ListAdapter<PayloadItemHistory, HistoryOfferAdapter.MyViewHolder>(DIFF_CALLBACK) {
    var onContactClick: ((String, String) -> Unit)? = null

    var onJemputClick: ((String, String) -> Unit)? = null
    var onDealClick: ((Int, Int, String, String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemHistoryPassengerRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    inner class MyViewHolder(val binding: ItemHistoryPassengerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(driverPassengerItem: PayloadItemHistory) {
            binding.apply {
                Glide.with(root)
                    .load(driverPassengerItem.carpoolingData?.driver?.profilePictureUrl)
                    .into(ivPassenger)

                tvCarpoolDriverName.text =
                    StringBuilder("Driver : ${driverPassengerItem.carpoolingData?.driver?.name}")
                tvPassengerCapacity.text =
                    StringBuilder("Pengajuan Kapasitas : ${driverPassengerItem.passageCount}")

                val numberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

                if (driverPassengerItem.status?.toInt() == 1) {
                    if (driverPassengerItem.price == null) {
                        tvStatus.text = StringBuilder("Waiting")
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
                        layoutBtnNegosiasi.isVisible = false
                    } else {
                        tvStatus.text = StringBuilder("Negosiasi")

                        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.yellow))
                        tvStatus.background = ContextCompat.getDrawable(
                            root.context,
                            R.drawable.background_outlined_textview_yellow
                        )
                        tvPassengerPrice.text =
                            StringBuilder(
                                numberFormat.format(
                                    driverPassengerItem.price.toString().toDouble()
                                )
                            )
                        layoutBtnNegosiasi.isVisible = true
                        tvPassengerPrice.isVisible = true
                    }
                } else {
                    tvStatus.text = StringBuilder("Disetujui")
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
                    onDealClick?.invoke(
                        driverPassengerItem.carpoolingId.toString().toInt(),
                        driverPassengerItem.id.toString().toInt(),
                        numberFormat.format(driverPassengerItem.price.toString().toDouble()).toString(),
                        driverPassengerItem.carpoolingData?.driver?.name.toString()
                    )
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PayloadItemHistory>() {
            override fun areItemsTheSame(
                oldItem: PayloadItemHistory,
                newItem: PayloadItemHistory
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PayloadItemHistory,
                newItem: PayloadItemHistory
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}