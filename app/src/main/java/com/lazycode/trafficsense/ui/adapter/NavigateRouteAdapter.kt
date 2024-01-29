package com.lazycode.trafficsense.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.lazycode.trafficsense.data.models.PathsItem
import com.lazycode.trafficsense.databinding.ItemRouteRowBinding
import java.lang.StringBuilder

class NavigateRouteAdapter : RecyclerView.Adapter<NavigateRouteAdapter.ViewHolder>() {
    private val listRoute: ArrayList<PathsItem> = arrayListOf()
    var onRouteClick: ((PathsItem, Int) -> Unit)? = null

    private val allBtnRoute = arrayListOf<MaterialButton>()
    private val allPlaceholderView = arrayListOf<View>()

    fun setList(routeList: ArrayList<PathsItem>) {
        this.listRoute.clear()
        this.allBtnRoute.clear()
        this.listRoute.addAll(routeList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRouteRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        allBtnRoute.add(holder.binding.btnRoute)
        allPlaceholderView.add(holder.binding.placeholderViewBg)
        if (position == 0) {
            holder.binding.btnRoute.isEnabled = false
            holder.binding.placeholderViewBg.isVisible = true
        }
        holder.bind(listRoute[position], position)
    }

    override fun getItemCount() = listRoute.size

    inner class ViewHolder(var binding: ItemRouteRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(path: PathsItem, position: Int) {
            with(binding) {
                btnRoute.text = StringBuilder("Rute ${position+1}")

                btnRoute.setOnClickListener {
                    onRouteClick?.invoke(path,position)
                }
            }
        }
    }

    fun changeBtnBehaviour(position: Int) {
        enableAllBtn()
        allBtnRoute[position].isEnabled = false
        allPlaceholderView[position].isVisible = true
    }

    private fun enableAllBtn() {
        for (routeBtn in allBtnRoute) {
            routeBtn.isEnabled = true
        }

        for (placeHolder in allPlaceholderView) {
            placeHolder.isVisible = false
        }
    }
}