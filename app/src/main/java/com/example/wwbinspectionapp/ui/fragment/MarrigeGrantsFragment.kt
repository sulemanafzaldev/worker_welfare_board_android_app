// MarriageGrantsFragment.kt
package com.example.wwbinspectionapp.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wwbinspectionapp.OnClick.ApproveRejectListener
import com.example.wwbinspectionapp.R
import com.example.wwbinspectionapp.adapter.MarriageGrantAdapter
import com.example.wwbinspectionapp.auth.AuthViewModel
import com.example.wwbinspectionapp.databinding.FragmentMarrigeGrantsBinding
import com.example.wwbinspectionapp.enums.GrantType
import com.example.wwbinspectionapp.grantList.Data
import com.example.wwbinspectionapp.ui.WorkerWalfareViewModel
import com.example.wwbinspectionapp.utils.NetworkResult
import com.example.wwbinspectionapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MarrigeGrantsFragment : Fragment(), ApproveRejectListener {

    private var _binding: FragmentMarrigeGrantsBinding? = null
    private val binding get() = _binding!!
    private lateinit var marriageGrantAdapter: MarriageGrantAdapter
    private val viewModel: AuthViewModel by viewModels()
    private val workerWalfareViewModel: WorkerWalfareViewModel by activityViewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    private val marriageGrantList = ArrayList<Data>()
    private var factoryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No need to get factoryId from arguments
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMarrigeGrantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observeGrantList()

        // Observe factoryId from the shared ViewModel
        workerWalfareViewModel.factoryId.observe(viewLifecycleOwner) { newFactoryId ->
            if (factoryId != newFactoryId) {
                factoryId = newFactoryId
                // Reload data for the new factory
                getGrantList()
            }
        }

        // Optionally, load data initially if factoryId is already set
        workerWalfareViewModel.factoryId.value?.let {
            factoryId = it
            getGrantList()
        }
    }

    private fun setupRecyclerView() {
        marriageGrantAdapter = MarriageGrantAdapter(marriageGrantList, this)

        binding.rvMarriageGrant.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = marriageGrantAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            getGrantList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeGrantList() {
        // Observe LiveData once to prevent multiple observers
        viewModel.grantListResponseLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Error -> {
                    showToast(result.message)
                    hideLoading()
                    showNoData()
                }
                is NetworkResult.Loading -> {
                    showLoading()
                }
                is NetworkResult.Success -> {
                    hideLoading()
                    binding.swipeRefreshLayout.isRefreshing = false
                    result.data?.data?.let { dataList ->
                        if (dataList.isNotEmpty()) {
                            marriageGrantList.clear()
                            marriageGrantList.addAll(dataList)
                            marriageGrantAdapter.notifyDataSetChanged()
                            showData()
                        } else {
                            showNoData()
                           // showToast(result.data.message)
                        }
                    }
                }
            }
        }
    }

    private fun getGrantList() {
        val barToken = tokenManager.getToken()
        if (barToken != null && factoryId != -1) {
            viewModel.getGrantList(
                authToken = barToken,
                grantType = GrantType.marriage_grants.name,
                factoryId = factoryId
            )
        } else {
            showToast("Invalid factory selection or missing token.")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onApproveClickListener(
        position: Int,
        appStatus: String,
        grantId: Int,
        factoryWorkerId: Int
    ) {

        val barToken = tokenManager.getToken()
        if (barToken != null) {
            // Call the approveGrant function with the approve status
            viewModel.approveGrant(
                authToken = barToken,
                grantType = GrantType.marriage_grants.name,
                appStatus = "approved",
                grantId = grantId,
                factoryWorkerId = factoryWorkerId
            )
        }
        // Log the action for debugging
        Log.d(
            "GrantAction",
            "approveGrant called with Token: $barToken, GrantType: ${GrantType.marriage_grants.name}, " +
                    "AppStatus: approved, Grant ID: $grantId, Factory Worker ID: $factoryWorkerId"
        )
        // Optionally remove the item from the list
        marriageGrantList.removeAt(position)
        marriageGrantAdapter.notifyItemRemoved(position)
        showToast("Approved")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRejectClickListener(
        position: Int,
        appStatus: String,
        grantId: Int,
        factoryWorkerId: Int
    ) {
        showRejectDialog(position, appStatus, grantId, factoryWorkerId)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showRejectDialog(
        position: Int,
        appStatus: String,
        grantId: Int,
        factoryWorkerId: Int
    ) {
        val builder = AlertDialog.Builder(requireContext())
        val input = EditText(requireContext()).apply {
            hint = "Enter your remarks"
        }

        builder.setTitle("Reject Request")
            .setMessage("Please provide remarks for rejection")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("Reject") { dialog, _ ->
                val remarks = input.text.toString()

                if (remarks.isNotEmpty()) {
                    val barToken = tokenManager.getToken()

                    // Call the approveGrant function with the reject status
                    if (barToken != null) {
                        viewModel.approveGrant(
                            authToken = barToken,
                            grantType = GrantType.marriage_grants.name,
                            appStatus = "rejected",
                            grantId = grantId,
                            factoryWorkerId = factoryWorkerId
                        )
                    }
                    // Log before calling the approveGrant function
                    Log.d(
                        "GrantActionMarriage",
                        "approveGrant called with Token: $barToken, GrantType: ${GrantType.marriage_grants.name}, " +
                                "AppStatus: rejected, Grant ID: $grantId, Factory Worker ID: $factoryWorkerId"
                    )
                    // Optionally remove the item from the list
                    marriageGrantList.removeAt(position)
                    marriageGrantAdapter.notifyItemRemoved(position)
                    showToast("Rejected: $remarks")
                    dialog.dismiss()
                } else {
                    showToast("Remarks are required")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the grant list
        getGrantList()
    }

    private fun showLoading() {
        binding.progressLayout.root.visibility = View.VISIBLE
        binding.rvMarriageGrant.visibility = View.GONE
        binding.tvNoDataFound.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressLayout.root.visibility = View.GONE
    }

    private fun showData() {
        binding.rvMarriageGrant.visibility = View.VISIBLE
        binding.tvNoDataFound.visibility = View.GONE
    }

    private fun showNoData() {
        binding.rvMarriageGrant.visibility = View.GONE
        binding.tvNoDataFound.visibility = View.VISIBLE
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
