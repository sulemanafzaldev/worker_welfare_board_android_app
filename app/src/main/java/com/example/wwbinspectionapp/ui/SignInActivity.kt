package com.example.wwbinspectionapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wwbinspectionapp.R
import com.example.wwbinspectionapp.auth.AuthViewModel
import com.example.wwbinspectionapp.databinding.ActivitySignInBinding
import com.example.wwbinspectionapp.model.UserRequest
import com.example.wwbinspectionapp.utils.NetworkResult
import com.example.wwbinspectionapp.utils.NetworkUtils
import com.example.wwbinspectionapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val saveToken = tokenManager.getToken()
        val savedepType = tokenManager.getDepartmentType()

        if (saveToken != null && savedepType != null) {
            redirecActivity(savedepType)
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailAddress.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (NetworkUtils.isInternetAvailable(this)) {
                    authViewModel.loginUser(UserRequest(email, password))
                } else {
                    Toast.makeText(
                        this,
                        "No internet connection. Please check your connection and try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        authViewModel.userResponseLiveData.observe(this) { result ->

            when (result) {
                is NetworkResult.Success -> {
                    binding.progressLayout.root.visibility = View.GONE

                    val loginResp = result.data

                    if (loginResp != null && loginResp.success) {
                        tokenManager.saveToken(loginResp.token)
                        tokenManager.saveDepartmentType(loginResp.department.d_type)

                        Log.d("suleman", "response- ${result.data}");

                        redirecActivity(savedepType)
                        finish()
                    } else {
                        binding.progressLayout.root.visibility = View.GONE
                        val errormsg = result.message ?: "Login Failed"
                        Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkResult.Error -> {
                    binding.progressLayout.root.visibility = View.GONE

                    // Check if the error message from the API contains specific error info
                    val errorMessage = result.message ?: "An error occurred"

                    if (errorMessage.contains("Username") && errorMessage.contains("was not found")) {
                        Toast.makeText(
                            this@SignInActivity,
                            "The email you entered was not found ",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // Display a generic error message
                        Toast.makeText(
                            this@SignInActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun redirecActivity(depType: String? = null) {
        Log.d("suleman", "dpt type - $depType")
        when (depType) {
            "wwb" -> {
                val intent = Intent(this@SignInActivity, WorkerWalfareBoardActivity::class.java)
                startActivity(intent)
            }

            else -> {
                Toast.makeText(this@SignInActivity, "Only WWb User Login", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}