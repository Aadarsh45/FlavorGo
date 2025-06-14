package com.example.flavorgo.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.flavorgo.R
import com.example.flavorgo.databinding.FragmentHomeBinding
import com.example.flavorgo.databinding.FragmentProfileBinding
import com.example.flavorgo.ui.OrderHistoryActivity
import com.example.flavorgo.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.fragment.app.viewModels
import com.example.flavorgo.utils.TokenManager
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jwt = tokenManager.getToken() // or your JWT storage

        lifecycleScope.launch {
            try {
                val response = userViewModel.getUserProfile(jwt.toString())
                if (response.isSuccessful) {
                    Log.d("Profile", "Response: ${response.body()}")
                    Log.d("Profile", "Code: ${response.code()}, Message: ${response.message()}")

                    val profile = response.body()
                    profile?.let {
                        binding.tvUserName.text = it.fullName
                    }
                } else {
                    Log.d("Profile", "Response: ${response.body()}")
                    Log.d("Profile", "Code: ${response.code()}, Message: ${response.message()}")

                }
            } catch (e: Exception) {


            }
        }

        binding.orderHistory.setOnClickListener {
            startActivity(Intent(context, OrderHistoryActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}


