package com.lazycode.trafficsense.ui.dynamicroute

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.lazycode.trafficsense.data.repo.DynamicRoutingRepository
import com.lazycode.trafficsense.data.repo.MapPlacesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest

@FlowPreview
@ExperimentalCoroutinesApi
class DynamicRouteViewModel(
    private val mapPlacesRepository: MapPlacesRepository,
    private val dynamicRoutingRepository: DynamicRoutingRepository,
) : ViewModel() {
    val queryChannelDeparture = MutableStateFlow("")
    val queryChannelDestination = MutableStateFlow("")

    val searchPlacesResultDeparture = queryChannelDeparture
        .debounce(0)
        .distinctUntilChanged()
        .filter {
            it.trim().isNotEmpty()
        }
        .mapLatest {
            mapPlacesRepository.searchMap(it).hits
        }
        .asLiveData()

    val searchPlacesResultDestination = queryChannelDestination
        .debounce(0)
        .distinctUntilChanged()
        .filter {
            it.trim().isNotEmpty()
        }
        .mapLatest {
            mapPlacesRepository.searchMap(it).hits
        }
        .asLiveData()

    fun searchPlaces(query: String) = mapPlacesRepository.searchMapOnce(query).asLiveData()

    fun navigateDestination(
        latDeparture:Double?,
        lonDeparture:Double?,
        latDestination:Double?,
        lonDestination:Double?,
    ) =
        dynamicRoutingRepository.navigateDestination(
            lonDeparture.toString(),
            latDeparture.toString(),
            lonDestination.toString(),
            latDestination.toString(),
        ).asLiveData()
}