package com.example.flavorgo.di

import android.content.Context
import com.example.flavorgo.data.repository.CartRepository
import com.example.flavorgo.data.repository.CartRepositoryImpl
import com.example.flavorgo.data.repository.OrderRepository
import com.example.flavorgo.data.repository.OrderRepositoryImpl
import com.example.flavorgo.network.ApiService
import com.example.flavorgo.network.RetrofitClient
import com.example.flavorgo.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideBaseUrl(): String = "http://192.168.8.99:5454/"

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context)

    @Provides
    @Singleton
    fun provideRetrofitClient(
        tokenManager: TokenManager,
        baseUrl: String
    ): RetrofitClient = RetrofitClient(tokenManager, baseUrl)

    @Provides
    @Singleton
    fun provideApiService(retrofitClient: RetrofitClient): ApiService =
        retrofitClient.apiService

    @Provides
    @Singleton
    fun provideCartRepository(
        apiService: ApiService,
        tokenManager: TokenManager
    ): CartRepository = CartRepositoryImpl(apiService, tokenManager)

    @Provides
    @Singleton
    fun provideOrderRepository(
        apiService: ApiService,
        tokenManager: TokenManager
    ): OrderRepository {
        return OrderRepositoryImpl(apiService, tokenManager)
    }
}