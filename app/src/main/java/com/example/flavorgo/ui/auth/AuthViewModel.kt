package com.example.flavorgo.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flavorgo.data.model.AuthResponse
import com.example.flavorgo.data.model.LoginRequest
import com.example.flavorgo.data.model.User
import com.example.flavorgo.data.repository.AuthRepository
import com.example.flavorgo.network.ApiService
import com.example.flavorgo.utils.Resource
import com.example.flavorgo.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _signUpResponse = MutableLiveData<Resource<AuthResponse>>()
    val signUpResponse: LiveData<Resource<AuthResponse>> = _signUpResponse

    private val _signInResponse = MutableLiveData<Resource<AuthResponse>>()
    val signInResponse: LiveData<Resource<AuthResponse>> = _signInResponse

    fun signUp(user: User) {
        viewModelScope.launch {
            _signUpResponse.postValue(Resource.Loading())
            val result = repository.signup(user)
            Log.d("API_DEBUG", "Making API call to: yoururl")
            if (result is Resource.Success) {
                Log.d("AuthViewModel", "Full SignUp Response: ${result.data}")
            } else {
                Log.e("AuthViewModel", "Error in SignUp Response: ${result.message}")
            }
            _signUpResponse.postValue(result)
        }
    }


    fun signIn(loginRequest: LoginRequest) {
        viewModelScope.launch {
            _signInResponse.postValue(Resource.Loading())
            _signInResponse.postValue(repository.signin(loginRequest))
        }
    }
}
