// WorkerWalfareBoardActivity.kt
package com.example.wwbinspectionapp.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.viewpager2.widget.ViewPager2
import com.example.wwbinspectionapp.R
import com.example.wwbinspectionapp.auth.AuthViewModel
import com.example.wwbinspectionapp.databinding.ActivityWorkerWalfareBoardBinding
import com.example.wwbinspectionapp.model.factoryList.Data
import com.example.wwbinspectionapp.utils.NetworkResult
import com.example.wwbinspectionapp.utils.StatusBarManager
import com.example.wwbinspectionapp.utils.TokenManager
import com.example.wwbinspectionapp.adapter.TempAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class WorkerWalfareBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkerWalfareBoardBinding
    private val viewModel: AuthViewModel by viewModels()
    private val workerWalfareViewModel: WorkerWalfareViewModel by viewModels()
    private val mFactoryList = ArrayList<Data>()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerWalfareBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAppBarListener()
        getFactoryListFromAPI()

        StatusBarManager.changeStatusBarColor(this, window, R.color.white)

        binding.imgLogout.setOnClickListener {
            // Clear the saved token
            tokenManager.clearToken()
            val intent = Intent(this@WorkerWalfareBoardActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        val searchview = binding.searcFactory
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterFactoryList(newText)
                return true
            }
        })

        binding.spinnerFactoryWwb.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (mFactoryList.isNotEmpty() && position > 0) {
                    val selectedFactoryName = mFactoryList[position].name
                    val selectedFactoryId = mFactoryList[position].id
                    Log.d("FactorySelection", "Selected Factory: $selectedFactoryName, ID: $selectedFactoryId")

                    // Show the TabLayout and ViewPager
                    binding.tabLayout.visibility = View.VISIBLE
                    binding.viewPager.visibility = View.VISIBLE

                    val selectedFactory = mFactoryList[position].id
                    workerWalfareViewModel.setFactoryId(selectedFactory) // Update ViewModel

                    // Optionally, reset the ViewPager to the first tab
                    binding.viewPager.currentItem = 0
                } else {
                    binding.tabLayout.visibility = View.GONE
                    binding.viewPager.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.tabLayout.visibility = View.GONE
                binding.viewPager.visibility = View.GONE
            }
        }

        setupTabs()
    }

    private fun filterFactoryList(query: String?) {
        val filterdList = mFactoryList.filter {
            it.name.contains(query?:"", ignoreCase = true)
        }
        val factoryName = filterdList.map { it.name }.toMutableList()
        factoryName.add(0, "Select Factory")
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item ,factoryName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFactoryWwb.adapter = adapter


    }

    private fun setupTabs() {
        val tabTitles = listOf("Marriage Grant", "Death Grant", "Hajj Grant", "Scholarship")

        val adapter = TempAdapter(this, tabTitles.size)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.tabLayout.apply {
            this.post {
                for (i in 0 until this.tabCount) {
                    val tab = (this.getChildAt(0) as ViewGroup).getChildAt(i)
                    val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(6, 2, 6, 2)
                    tab.requestLayout()
                }
            }
        }
    }

    private fun getFactoryListFromAPI() {
        val barToken = tokenManager.getToken()?.trim()
        Log.d("TokenDebug", "Retrieved Token: $barToken")

        viewModel.factoryListLiveData.observe(this) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    binding.progressLayout.root.visibility = View.GONE
                    Log.d("FactoryListSuccess", "Data: ${result.data}")
                    val factoryNames = ArrayList<String>()

                    if (result.data?.data.isNullOrEmpty()) {
                        factoryNames.add("Select Factory")
                        mFactoryList.clear()
                        mFactoryList.add(Data("", "", "", -1, "", "", ""))
//                        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    } else {
                        result.data?.data?.forEach {
                            factoryNames.add(it.name)
                        }
                        factoryNames.add(0, "Select Factory")

                        mFactoryList.clear()
                        mFactoryList.addAll(result.data!!.data)
                        mFactoryList.add(0, Data("", "", "", -1, "", "", ""))
                    }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        factoryNames
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    binding.spinnerFactoryWwb.adapter = adapter
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    binding.progressLayout.root.visibility = View.GONE
                }

                is NetworkResult.Loading -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }

                else -> {}
            }
        }

        if (barToken != null) {
            viewModel.getUserFactoryList(barToken)
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

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
