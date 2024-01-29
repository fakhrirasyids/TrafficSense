package com.lazycode.trafficsense.ui.carpool.carpoolhistory

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivityCarpoolHistoryBinding
import com.lazycode.trafficsense.ui.adapter.HistoryOfferAdapter
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.fragments.BottomSheetNegotiate
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.fragments.BottomSheetPickInfo
import com.lazycode.trafficsense.utils.Constants
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Result
import org.koin.android.viewmodel.ext.android.viewModel

class CarpoolHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCarpoolHistoryBinding

    private val carpoolHistoryViewModel: CarpoolHistoryViewModel by viewModel()

    private val acceptedDriverPassengerAdapter = HistoryOfferAdapter()
    private val negotiateDriverPassengerAdapter = HistoryOfferAdapter()

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarpoolHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        binding.toolbar.setNavigationOnClickListener { finish() }

        observeViewModel()
        setRecyclerView()

        setupPlayAnimation()
    }

    private fun observeViewModel() {
        carpoolHistoryViewModel.apply {
            isLoading.observe(this@CarpoolHistoryActivity) {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }

            historyAccepted.observe(this@CarpoolHistoryActivity) {
                binding.acceptLayout.isVisible = it.isNotEmpty()

                acceptedDriverPassengerAdapter.submitList(it)
            }

            historyNegotiate.observe(this@CarpoolHistoryActivity) { negotiateList ->
                binding.negotiateLayout.isVisible = negotiateList.isNotEmpty()

                negotiateDriverPassengerAdapter.submitList(
                    negotiateList.sortedWith(
                        compareBy(
                            { it.price == null },
                            { it.price })
                    )
                )
            }

            errorText.observe(this@CarpoolHistoryActivity) {
//                binding.rvVehicle.isVisible = it.isEmpty()
//                binding.layoutError.isVisible = it.isNotEmpty()
            }
        }
    }

    private fun setRecyclerView() {
        binding.rvPassengerAccept.apply {
            val layoutManagerModel = LinearLayoutManager(this@CarpoolHistoryActivity)

            acceptedDriverPassengerAdapter.onJemputClick = { pickInfo, dropInfo ->
                val bundle = Bundle()
                bundle.putString(BottomSheetPickInfo.KEY_PICK_INFO, pickInfo)
                bundle.putString(BottomSheetPickInfo.KEY_DESTINATION_INFO, dropInfo)
                val bottomSheetPickInfo = BottomSheetPickInfo()
                bottomSheetPickInfo.arguments = bundle
                bottomSheetPickInfo.show(
                    supportFragmentManager,
                    "CarpoolHistory"
                )
            }

            acceptedDriverPassengerAdapter.onContactClick = { driverName, contact ->
                Constants.openWhatsappHelper(
                    this@CarpoolHistoryActivity,
                    this@CarpoolHistoryActivity,
                    driverName,
                    contact
                )
            }

            adapter = acceptedDriverPassengerAdapter
            layoutManager = layoutManagerModel
            addItemDecoration(
                DividerItemDecoration(
                    baseContext,
                    layoutManagerModel.orientation
                )
            )
        }

        binding.rvPassengerNegotiate.apply {
            val layoutManagerModel = LinearLayoutManager(this@CarpoolHistoryActivity)

            negotiateDriverPassengerAdapter.onJemputClick = { pickInfo, dropInfo ->
                val bundle = Bundle()
                bundle.putString(BottomSheetPickInfo.KEY_PICK_INFO, pickInfo)
                bundle.putString(BottomSheetPickInfo.KEY_DESTINATION_INFO, dropInfo)
                val bottomSheetPickInfo = BottomSheetPickInfo()
                bottomSheetPickInfo.arguments = bundle
                bottomSheetPickInfo.show(
                    supportFragmentManager,
                    "DriverPassengerActivity"
                )
            }
            negotiateDriverPassengerAdapter.onContactClick = { driverName, contact ->
                Constants.openWhatsappHelper(
                    this@CarpoolHistoryActivity,
                    this@CarpoolHistoryActivity,
                    driverName,
                    contact
                )
            }

            negotiateDriverPassengerAdapter.onDealClick =
                { carpoolingId, passengerId, price, driverName ->
                    val builder = AlertDialog.Builder(this@CarpoolHistoryActivity)
                    builder.setCancelable(false)

                    with(builder)
                    {
                        setTitle("Peringatan")
                        setMessage("Apakah anda setuju dengan tawaran harga $price dari $driverName?")
                        setPositiveButton("Ya") { dialog, _ ->
                            dialog.dismiss()
                            carpoolHistoryViewModel.updateStatusDeal(carpoolingId, passengerId)
                                .observe(this@CarpoolHistoryActivity) { result ->
                                    when (result) {
                                        is Result.Loading -> {
                                            loadingDialog.show()
                                        }

                                        is Result.Success -> {
                                            loadingDialog.dismiss()
                                            if (result.data.success!!) {
                                                val infoBuilder =
                                                    AlertDialog.Builder(this@CarpoolHistoryActivity)
                                                infoBuilder.setCancelable(false)

                                                with(infoBuilder)
                                                {
                                                    setTitle("Berhasil Menyetujui")
                                                    setMessage("Klik OK untuk melanjutkan.")
                                                    setPositiveButton("Ok") { dialogInfo, _ ->
                                                        carpoolHistoryViewModel.getHistoryPassengers()
                                                        dialogInfo.dismiss()
                                                    }
                                                    show()
                                                }
                                            } else {
                                                alertDialogMessage(
                                                    this@CarpoolHistoryActivity,
                                                    result.data.message.toString(),
                                                    "Gagal Menyetujui"
                                                )
                                            }
                                        }

                                        is Result.Error -> {
                                            loadingDialog.dismiss()
                                            alertDialogMessage(
                                                this@CarpoolHistoryActivity,
                                                result.error,
                                                "Error"
                                            )
                                        }
                                    }
                                }
                        }
                        setNegativeButton("Tidak") { dialog, _ ->
                            dialog.dismiss()
                        }
                        show()
                    }
                }

            adapter = negotiateDriverPassengerAdapter
            layoutManager = layoutManagerModel
            addItemDecoration(
                DividerItemDecoration(
                    baseContext,
                    layoutManagerModel.orientation
                )
            )
        }
    }

    @SuppressLint("Recycle")
    private fun setupPlayAnimation() {
        val acceptLayout: Animator =
            ObjectAnimator.ofFloat(binding.acceptLayout, View.ALPHA, 1f).setDuration(300)
        val negotiateLayout: Animator =
            ObjectAnimator.ofFloat(binding.negotiateLayout, View.ALPHA, 1f).setDuration(300)

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(acceptLayout, negotiateLayout)
        animatorSet.startDelay = 200
        animatorSet.start()
    }
}