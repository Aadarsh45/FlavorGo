package com.example.flavorgo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.flavorgo.R
import com.example.flavorgo.data.model.User
import com.example.flavorgo.data.repository.AuthRepository
import com.example.flavorgo.databinding.ActivitySignUpBinding
import com.example.flavorgo.network.RetrofitClient
import com.example.flavorgo.ui.home.HomeActivity
import com.example.flavorgo.utils.Resource
import com.example.flavorgo.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenManager:TokenManager
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     


        Log.d("SignUpActivity", "onCreate() called")


        Log.d("SignUpActivity", "TokenManager initialized")
        val authViewModel: AuthViewModel by viewModels {
            AuthViewModelFactory(
                AuthRepository(
                    RetrofitClient(tokenManager, getString(R.string.base_url)).apiService,
                    tokenManager
                ),
                tokenManager
            )
        }

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginRedirectText.setOnClickListener {
            Log.d("SignUpActivity", "Redirecting to SignInActivity")
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        binding.signUpButton.setOnClickListener {
            val fullName = binding.fullNameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val role = "ROLE_CUSTOMER"

            Log.d("SignUpActivity", "SignUp button clicked with Full Name: $fullName, Email: $email, Role: $role")

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.signUp(User(fullName, email, password, role))
                Log.d("SignUpActivity", "SignUp request sent to ViewModel")
            } else {
                Log.w("SignUpActivity", "SignUp failed due to empty fields")
                Toast.makeText(this, "All fields are required Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.signUpResponse.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    Log.d("SignUpActivity", "SignUp successful: ${response.data}")
                    Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    Log.e("SignUpActivity", "SignUp failed: ${response.message}")
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    Log.d("SignUpActivity", "SignUp is in progress...")
                }
            }
        })
    }
}
