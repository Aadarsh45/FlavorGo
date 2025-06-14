package com.example.flavorgo.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.flavorgo.R
import com.example.flavorgo.data.model.NutritionalInfoObject
import com.example.flavorgo.data.model.NutritionalRequest
import com.example.flavorgo.data.model.NutritionalResponse
import com.example.flavorgo.databinding.FragmentScanDishBinding
import com.example.flavorgo.network.LogMealApiService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class ScanDishFragment : Fragment() {

    private lateinit var binding: FragmentScanDishBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private val TAG = "ScanDishFragment"

    @Inject
    lateinit var apiService: LogMealApiService

    private lateinit var apiKey: String
    private var imageUri: Uri? = null
    private var recognizedImageId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanDishBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: Fragment view created")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Fragment view initialized")

        apiKey = getString(R.string.bEARER)
        Log.d(TAG, "onViewCreated: API Key initialized")

        binding.captureButton.setOnClickListener {
            Log.d(TAG, "Capture button clicked")
            dispatchTakePictureIntent()
        }
    }

    private fun compressImageToUnder1MB(bitmap: Bitmap): File {
        var quality = 80
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true)
        val file = File(requireContext().cacheDir, "upload_image.jpg")

        var stream: ByteArrayOutputStream
        var byteArray: ByteArray

        do {
            stream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            byteArray = stream.toByteArray()
            quality -= 5
        } while (byteArray.size > 1024 * 1024 && quality > 10)

        // Write to file
        FileOutputStream(file).use {
            it.write(byteArray)
        }

        return file
    }

    private fun dispatchTakePictureIntent() {
        val photoFile = File(requireContext().cacheDir, "captured_image.jpg")
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.flavorgo.provider",
            photoFile
        )

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            Log.d(TAG, "dispatchTakePictureIntent: Opened camera intent")
        } else {
            Log.e(TAG, "dispatchTakePictureIntent: No camera app found")
            showToast("No camera app available")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageUri?.let {
                Log.d(TAG, "onActivityResult: Image captured successfully, URI=$it")
                binding.progressBar.visibility = View.VISIBLE
                binding.resultLayout.visibility = View.GONE
                uploadImage(it)
            }
        } else {
            Log.d(TAG, "onActivityResult: Image capture failed or cancelled")
        }
    }

    private fun uploadImage(imageUri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                val file = compressImageToUnder1MB(bitmap)

                val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file.readBytes())
                val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)

                Log.d(TAG, "uploadImage: Compressed image size = ${file.length() / 1024} KB")

                val response = apiService.recognizeFood(apiKey, multipartBody).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { result ->
                            recognizedImageId = result.imageId
                            Log.d(TAG, "uploadImage: Image recognized successfully, imageId=$recognizedImageId")

                            // Show image preview
                            binding.foodImageView.setImageURI(imageUri)

                            // If there are segments detected, we should confirm them first
                            if(!result.segmentation_results.isNullOrEmpty()) {
                                Log.d(TAG, "Found ${result.segmentation_results.size} food items")
                                // Here we could implement dish confirmation before getting nutritional info
                                // For now we'll skip and get nutrition directly
                            }

                            getNutritionalInfo(recognizedImageId!!)
                        }
                    } else {
                        binding.progressBar.visibility = View.GONE
                        Log.e(TAG, "uploadImage: Recognition failed, responseCode=${response.code()}")
                        showToast("Recognition failed (Code: ${response.code()})")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "uploadImage: Exception", e)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    showToast("Upload failed: ${e.message}")
                }
            }
        }
    }

    private fun getNutritionalInfo(imageId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val requestBody = NutritionalRequest(imageId)
                val response = apiService.getNutritionalInfo(apiKey, requestBody).execute()

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val nutritionData = response.body()
                        Log.d(TAG, "getNutritionalInfo: Success, data=$nutritionData")

                        nutritionData?.let { data ->
                            updateUI(data)
                        } ?: run {
                            showToast("No nutritional data returned")
                        }
                    } else {
                        Log.e(TAG, "getNutritionalInfo: Failed, responseCode=${response.code()}")
                        showToast("Nutritional info retrieval failed (Code: ${response.code()})")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "getNutritionalInfo: Exception", e)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    showToast("Error fetching nutritional info: ${e.message}")
                }
            }
        }
    }




//    private fun updateUI(data: NutritionalResponse) {
//        binding.resultLayout.visibility = View.VISIBLE
//
//        // Set food name
//        binding.foodNameTextView.text = data.foodName.joinToString(", ")
//
//
//        // Check if nutritional info is available
//        if (data.hasNutritionalInfo && data.nutritional_info != null) {
//            val info = data.nutritional_info
//
//            // Display main nutritional values
//            binding.caloriesTextView.text = "${info.calories} kcal"
//            binding.proteinTextView.text = "${info.protein.value}${info.protein.unit}"
//            binding.carbsTextView.text = "${info.carbs.value}${info.carbs.unit}"
//            binding.fatTextView.text = "${info.fat.value}${info.fat.unit}"
//
//            // Display additional nutritional details
//            val detailsText = StringBuilder()
//            detailsText.append("Serving Size: ${data.serving_size ?: "Standard portion"}\n")
//            detailsText.append("Sugar: ${info.sugar.value}${info.sugar.unit}\n")
//            detailsText.append("Fiber: ${info.fiber.value}${info.fiber.unit}\n")
//            detailsText.append("Saturated Fat: ${info.saturatedFat.value}${info.saturatedFat.unit}\n")
//            detailsText.append("Sodium: ${info.sodium.value}${info.sodium.unit}")
//
//            binding.nutritionalDetailsTextView.text = detailsText.toString()
//
//            // Show reference intake levels if available
//            info.dailyIntakeReference?.let { ref ->
//                val referenceText = StringBuilder("Daily Intake Reference:\n")
//                referenceText.append("Calories: ${ref.calories}\n")
//                referenceText.append("Protein: ${ref.protein}\n")
//                referenceText.append("Carbs: ${ref.carbs}\n")
//                referenceText.append("Fat: ${ref.fat}\n")
//
//                binding.referenceIntakeTextView.text = referenceText.toString()
//                binding.referenceIntakeTextView.visibility = View.VISIBLE
//            } ?: run {
//                binding.referenceIntakeTextView.visibility = View.GONE
//            }
//
//            // If multiple items were recognized, show their details too
//            if (!data.nutritional_info_per_item.isNullOrEmpty()) {
//                val itemsText = StringBuilder("Individual Items:\n")
//
//                data.nutritional_info_per_item.forEach { item ->
//                    itemsText.append("- ${item.dish_name}: ${item.nutritional_info?.calories ?: 0} kcal\n")
//                }
//
//                binding.individualItemsTextView.text = itemsText.toString()
//                binding.individualItemsTextView.visibility = View.VISIBLE
//            } else {
//                binding.individualItemsTextView.visibility = View.GONE
//            }
//
//        } else {
//            // No nutritional info
//            binding.caloriesTextView.text = "N/A"
//            binding.proteinTextView.text = "N/A"
//            binding.carbsTextView.text = "N/A"
//            binding.fatTextView.text = "N/A"
//            binding.nutritionalDetailsTextView.text = "No nutritional information available for this dish."
//            binding.referenceIntakeTextView.visibility = View.GONE
//            binding.individualItemsTextView.visibility = View.GONE
//        }
//    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        Log.d(TAG, "showToast: $message")
    }
    private fun updateUI(data: NutritionalResponse) {
        binding.resultLayout.visibility = View.VISIBLE

        // Set food names - join all food names if multiple exist
        binding.foodNameTextView.text = if (data.foodName.isNotEmpty()) {
            data.foodName.joinToString(", ")
        } else {
            "Multiple dishes detected"
        }

        // Clear previous views if any
        binding.nutritionCard.removeAllViews()

        if (data.nutritional_info_per_item.isNullOrEmpty()) {
            // Handle single food item case
            updateSingleFoodUI(data)
        } else {
            // Handle multiple food items case
            updateMultipleFoodsUI(data)
        }
    }

    private fun updateSingleFoodUI(data: NutritionalResponse) {
        val info = data.nutritional_info

        val nutritionView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_nutrition_info, binding.nutritionCard, false)

        nutritionView.findViewById<TextView>(R.id.itemNameTextView).text =
            data.foodName.firstOrNull() ?: "Dish"

        nutritionView.findViewById<TextView>(R.id.caloriesTextView).text =
            "${info?.calories?.let { "%.1f".format(it) } ?: "N/A"} kcal"

        nutritionView.findViewById<TextView>(R.id.proteinTextView).text =
            info?.protein?.let { "${it.value}${it.unit}" } ?: "N/A"

        nutritionView.findViewById<TextView>(R.id.carbsTextView).text =
            info?.carbs?.let { "${it.value}${it.unit}" } ?: "N/A"

        nutritionView.findViewById<TextView>(R.id.fatTextView).text =
            info?.fat?.let { "${it.value}${it.unit}" } ?: "N/A"

        val detailsText = buildDetailsText(info, data.serving_size)
        nutritionView.findViewById<TextView>(R.id.nutritionalDetailsTextView).text = detailsText

        binding.nutritionCard.addView(nutritionView)
    }

    private fun updateMultipleFoodsUI(data: NutritionalResponse) {
        data.nutritional_info_per_item!!.forEachIndexed { index, item ->
            val nutritionView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_nutrition_info, binding.nutritionCard, false)

            // Set item name (use position if name not available)
            val itemName = item.dish_name ?: "Item ${index + 1}"
            nutritionView.findViewById<TextView>(R.id.itemNameTextView).text = itemName

            val info = item.nutritional_info
            nutritionView.findViewById<TextView>(R.id.caloriesTextView).text =
                "${info?.calories?.let { "%.1f".format(it) } ?: "N/A"} kcal"

            nutritionView.findViewById<TextView>(R.id.proteinTextView).text =
                info?.protein?.let { "${it.value}${it.unit}" } ?: "N/A"

            nutritionView.findViewById<TextView>(R.id.carbsTextView).text =
                info?.carbs?.let { "${it.value}${it.unit}" } ?: "N/A"

            nutritionView.findViewById<TextView>(R.id.fatTextView).text =
                info?.fat?.let { "${it.value}${it.unit}" } ?: "N/A"

            val detailsText = buildDetailsText(info, item.serving_size)
            nutritionView.findViewById<TextView>(R.id.nutritionalDetailsTextView).text = detailsText

            // Add divider between items except for the last one
            if (index < data.nutritional_info_per_item!!.size - 1) {
                val divider = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    ).apply {
                        setMargins(0, 16.dpToPx(), 0, 16.dpToPx())
                    }
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.divider))
                }
                binding.nutritionCard.addView(divider)
            }

            binding.nutritionCard.addView(nutritionView)
        }
    }

    private fun buildDetailsText(info: NutritionalInfoObject?, servingSize: Double?): String {
        val detailsText = StringBuilder()

        servingSize?.let {
            detailsText.append("Serving Size: ${it}g\n")
        }

        info?.sugar?.let { detailsText.append("Sugar: ${it.value}${it.unit}\n") }
        info?.fiber?.let { detailsText.append("Fiber: ${it.value}${it.unit}\n") }
        info?.saturatedFat?.let { detailsText.append("Saturated Fat: ${it.value}${it.unit}\n") }
        info?.sodium?.let { detailsText.append("Sodium: ${it.value}${it.unit}") }

        return if (detailsText.isNotEmpty()) {
            detailsText.toString()
        } else {
            "Nutritional details not available"
        }
    }

    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}