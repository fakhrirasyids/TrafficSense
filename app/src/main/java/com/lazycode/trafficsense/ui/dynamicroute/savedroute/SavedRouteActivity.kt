package com.lazycode.trafficsense.ui.dynamicroute.savedroute

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivitySavedRouteBinding
import com.lazycode.trafficsense.ui.adapter.SavedRouteAdapter
import com.lazycode.trafficsense.ui.dynamicroute.savedroute.viewlocation.ViewLocationActivity
import com.lazycode.trafficsense.ui.dynamicroute.savedroute.viewlocation.ViewLocationActivity.Companion.KEY_ROUTE_DETAIL
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Result
import org.koin.android.viewmodel.ext.android.viewModel

class SavedRouteActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedRouteBinding
    private val savedRouteViewModel: SavedRouteViewModel by viewModel()

    private val savedRouteAdapter = SavedRouteAdapter()

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        observeViewModels()
        setListeners()
        setSwipeRefresh()

        setRecyclerView()
    }

    private fun observeViewModels() {
        savedRouteViewModel.apply {
            isLoading.observe(this@SavedRouteActivity) {
                binding.tvEmptyMessage.isVisible = false

                shimmerToggle(it)
                if (!it) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }

            savedRoutesList.observe(this@SavedRouteActivity) {
                binding.tvEmptyMessage.isVisible = it.isEmpty()
                binding.rvRoute.isVisible = it.isNotEmpty()

                savedRouteAdapter.submitList(it)
            }

            errorText.observe(this@SavedRouteActivity) {
                binding.tvEmptyMessage.isVisible = false

                binding.rvRoute.isVisible = it.isEmpty()
                binding.layoutError.isVisible = it.isNotEmpty()
            }
        }
    }

    private fun setSwipeRefresh() {
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                isRefreshing = true
                savedRouteViewModel.getSavedRoutes()
            }
        }
    }


    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }
        }
    }

    private fun shimmerToggle(isStart: Boolean) {
        binding.shimmerLayout.apply {
            isVisible = isStart
            if (isStart) {
                startShimmer()
            } else {
                stopShimmer()
            }
        }
    }

    private fun setRecyclerView() {
        binding.rvRoute.apply {
            val layoutManagerModel = LinearLayoutManager(this@SavedRouteActivity)
            savedRouteAdapter.onItemClick = { id ->
                savedRouteViewModel.getRouteDetail(id).observe(this@SavedRouteActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            loadingDialog.show()
                        }

                        is Result.Success -> {
                            loadingDialog.dismiss()
                            if (result.data.success!!) {
                                val iRouteDetail = Intent(
                                    this@SavedRouteActivity,
                                    ViewLocationActivity::class.java
                                )
                                iRouteDetail.putExtra(KEY_ROUTE_DETAIL, result.data.payload)
                                startActivity(iRouteDetail)
                            } else {
                                alertDialogMessage(
                                    this@SavedRouteActivity,
                                    result.data.message.toString(),
                                    "Gagal"
                                )
                            }
                        }

                        is Result.Error -> {
                            loadingDialog.dismiss()
                            alertDialogMessage(this@SavedRouteActivity, result.error, "Error")
                        }
                    }
                }
            }

            adapter = savedRouteAdapter
            layoutManager = layoutManagerModel
            addItemDecoration(
                DividerItemDecoration(
                    baseContext,
                    layoutManagerModel.orientation
                )
            )
        }
    }
}