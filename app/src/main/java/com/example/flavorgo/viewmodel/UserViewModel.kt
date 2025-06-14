package com.example.flavorgo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.flavorgo.data.model.UserProfileResponse
import com.example.flavorgo.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    suspend fun getUserProfile(token: String): Response<UserProfileResponse> {
        return api.getUserProfile(token)
    }
}
