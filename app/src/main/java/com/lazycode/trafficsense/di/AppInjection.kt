package com.lazycode.trafficsense.di

import com.lazycode.trafficsense.BuildConfig
import com.lazycode.trafficsense.data.remote.services.AuthApiService
import com.lazycode.trafficsense.data.remote.services.MainApiService
import com.lazycode.trafficsense.data.remote.AuthAuthenticator
import com.lazycode.trafficsense.data.remote.AuthInterceptor
import com.lazycode.trafficsense.data.remote.services.MapPlacesApiService
import com.lazycode.trafficsense.data.repo.AuthRepository
import com.lazycode.trafficsense.data.repo.CarpoolRepository
import com.lazycode.trafficsense.data.repo.DynamicRoutingRepository
import com.lazycode.trafficsense.data.repo.MapPlacesRepository
import com.lazycode.trafficsense.data.repo.SensorRepository
import com.lazycode.trafficsense.ui.auth.AuthViewModel
import com.lazycode.trafficsense.ui.carpool.CarpoolViewModel
import com.lazycode.trafficsense.ui.carpool.addcarpool.AddCarpoolViewModel
import com.lazycode.trafficsense.ui.carpool.carpoolhistory.CarpoolHistoryViewModel
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.DriverPassengerViewModel
import com.lazycode.trafficsense.ui.carpool.vehicle.VehicleViewModel
import com.lazycode.trafficsense.ui.dynamicroute.DynamicRouteViewModel
import com.lazycode.trafficsense.ui.dynamicroute.pickroute.AvailableRoutesViewModel
import com.lazycode.trafficsense.ui.dynamicroute.savedroute.SavedRouteViewModel
import com.lazycode.trafficsense.ui.dynamicrouting.DynamicRoutingViewModel
import com.lazycode.trafficsense.ui.main.MainViewModel
import com.lazycode.trafficsense.ui.main.ui.scan.ScanViewModel
import com.lazycode.trafficsense.ui.profilesettings.ProfileSettingsViewModel
import com.lazycode.trafficsense.ui.splash.SplashViewModel
import com.lazycode.trafficsense.utils.UserPreferences
import com.lazycode.trafficsense.utils.dataStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//val databaseModule = module {
//    factory {  }
//    single {
//        Room.databaseBuilder(
//            androidContext(),
//            NewsDatabase::class.java, "News.db"
//        ).fallbackToDestructiveMigration()
//            .build()
//    }
//}

val networkModule = module {
    single {
        AuthInterceptor(get())
    }
    single {
        AuthAuthenticator(get(), get())
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .authenticator(get<AuthAuthenticator>())
                    .addInterceptor(get<AuthInterceptor>())
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
            )
            .build()
        retrofit.create(MainApiService::class.java)
    }

    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
            )
            .build()
        retrofit.create(AuthApiService::class.java)
    }

    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.GRAPHOPPER_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
            )
            .build()
        retrofit.create(MapPlacesApiService::class.java)
    }
}

val dataStoreModule = module {
    single { androidContext().dataStore }
}

val repositoryModule = module {
    single { UserPreferences(get()) }
    single { AuthRepository(get(), get()) }
    single { SensorRepository(get()) }
    single { DynamicRoutingRepository(get()) }
    single { MapPlacesRepository(get()) }
    single { CarpoolRepository(get()) }
}

val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { AuthViewModel(get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { ScanViewModel(get()) }
    viewModel { DynamicRoutingViewModel(get()) }
    viewModel { AvailableRoutesViewModel(get(), get()) }
    viewModel { DynamicRouteViewModel(get(), get()) }
    viewModel { ProfileSettingsViewModel(get(), get()) }
    viewModel { CarpoolViewModel(get(), get()) }
    viewModel { VehicleViewModel(get()) }
    viewModel { AddCarpoolViewModel(get(), get()) }
    viewModel { CarpoolHistoryViewModel(get()) }
    viewModel { SavedRouteViewModel(get()) }
    viewModel { (carpoolId: Int) -> DriverPassengerViewModel(get(), carpoolId) }
}
