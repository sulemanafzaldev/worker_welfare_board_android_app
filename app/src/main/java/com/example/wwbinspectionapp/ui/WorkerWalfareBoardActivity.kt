package com.example.wwbinspectionapp.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wwbinspectionapp.R
import com.example.wwbinspectionapp.adapter.TempAdapter
import com.example.wwbinspectionapp.auth.AuthViewModel
import com.example.wwbinspectionapp.databinding.ActivityWorkerWalfareBoardBinding
import com.example.wwbinspectionapp.model.factoryList.Data
import com.example.wwbinspectionapp.utils.NetworkResult
import com.example.wwbinspectionapp.utils.StatusBarManager
import com.example.wwbinspectionapp.utils.TokenManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class WorkerWalfareBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkerWalfareBoardBinding
    private val viewModel: AuthViewModel by viewModels()
    private val mFactoryList = ArrayList<Data>()

    @Inject
    lateinit var tokenManager: TokenManager

    companion object {
        var factoryId: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerWalfareBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAppBarListener()
        // setupLogoutButton()
        getFactoryListFromAPI()

        StatusBarManager.changeStatusBarColor(this, window, R.color.white)

        binding.imgLogout.setOnClickListener {
            // Clear the saved token
            tokenManager.clearToken()
            // Redirect to SignInActivity
            val intent = Intent(this@WorkerWalfareBoardActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.spinnerFactoryWwb.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (mFactoryList.isNotEmpty() && position > 0) {
                        // Show the TabLayout and ViewPager
                        binding.tabLayout.visibility = View.VISIBLE
                        binding.viewPager.visibility = View.VISIBLE
                        factoryId = mFactoryList[position].id
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    binding.tabLayout.visibility = View.GONE
                    binding.viewPager.visibility = View.GONE
                }
            }

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Marriage Grant"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Death Grant"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Hajj Grant"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("ScholarShip"))


        val adapter = factoryId?.let { TempAdapter(this, binding.tabLayout.tabCount, it) }
        binding.viewPager.adapter = adapter


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        binding.tabLayout.apply {
            this.post {
                for (i in 0 until this.tabCount) {
                    val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
                    val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(
                        6,
                        2,
                        6,
                        2
                    ) // left, top, right, bottom margins in px
                    tab.requestLayout()
                }
            }
        }

    }

    private fun getFactoryListFromAPI() {

        val barToken = tokenManager.getToken()

        viewModel.factoryListLiveData.observe(this) { result ->
            when (result) {

                is NetworkResult.Success -> {
                    binding.progressLayout.root.visibility = View.GONE
                    Log.d("FactoryListSuccess", "Data: ${result.data}")
                    val factoryNames = ArrayList<String>()
                    // Check if the data from the API is empty
                    if (result.data?.data.isNullOrEmpty()) {
                        // If no data, show only "Select Factory"
                        factoryNames.add("Select Factory")
                        // Clear the mFactoryList to avoid crashes when selecting
                        if (mFactoryList.isNotEmpty()) mFactoryList.clear()
                        mFactoryList.add(Data("", "", "", -1, "", "", ""))
                        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()

                    } else {
                        // If data is available, populate the spinner with factory names
                        result.data?.data?.forEach {
                            factoryNames.add(it.name)
                        }
                        factoryNames.add(0, "Select Factory")

                        if (mFactoryList.isNotEmpty()) mFactoryList.clear()

                        // Add factories to mFactoryList
                        mFactoryList.addAll(result.data!!.data)
                        // Add a placeholder at position 0
                        mFactoryList.add(0, Data("", "", "", -1, "", "", ""))
                    }
                    val adapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_item, factoryNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerFactoryWwb.adapter = adapter
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    binding.progressLayout.root.visibility = View.GONE
                }

                is NetworkResult.Loading -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }

                else -> {

                }
            }
        }

        if (barToken != null) {
            viewModel.getUserFactoryList(barToken)
        }
    }

    private fun setupLogoutButton() {
        binding.imgLogout.setOnClickListener {
            tokenManager.clearToken()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupAppBarListener() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val collapsePercentage = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
            updateTolBarColor(collapsePercentage == 1f)
        }
    }

    private fun updateTolBarColor(isCollapsed: Boolean) {

        val color = if (isCollapsed) Color.BLACK else Color.WHITE
        binding.tvLogout.setTextColor(color)
        binding.imgLogout.setColorFilter(color)
    }

    override fun onRestart() {
        super.onRestart()
        getFactoryListFromAPI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()

    }
}