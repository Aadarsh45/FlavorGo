package com.example.flavorgo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.flavorgo.R
import com.example.flavorgo.data.model.LoginRequest
import com.example.flavorgo.data.repository.AuthRepository
import com.example.flavorgo.databinding.ActivitySignInBinding
import com.example.flavorgo.network.RetrofitClient
import com.example.flavorgo.ui.home.HomeActivity
import com.example.flavorgo.utils.Resource
import com.example.flavorgo.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    @Inject
    lateinit var tokenManager:TokenManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
 // Your token from Postman



        val authViewModel: AuthViewModel by viewModels {
            AuthViewModelFactory(
                AuthRepository(
                    RetrofitClient(tokenManager, getString(R.string.base_url)).apiService,
                    tokenManager
                ),
                tokenManager
            )
        }

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.signIn(LoginRequest(email, password))
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.signInResponse.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    Log.d("SignIn", "Login Successful: ${response.data}")
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    Log.e("SignIn", "Login Error: ${response.message}")
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    Log.d("SignIn", "Signing in...")
                }
            }
        })
    }
}

