package com.example.wwbinspectionapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wwbinspectionapp.OnClick.ApproveRejectListener
import com.example.wwbinspectionapp.R
import com.example.wwbinspectionapp.adapter.HajjAdapter
import com.example.wwbinspectionapp.adapter.MarriageGrantAdapter
import com.example.wwbinspectionapp.adapter.ScholarShipAdapter
import com.example.wwbinspectionapp.auth.AuthViewModel
import com.example.wwbinspectionapp.databinding.FragmentScholerShipBinding
import com.example.wwbinspectionapp.enums.GrantType
import com.example.wwbinspectionapp.grantList.Data
import com.example.wwbinspectionapp.ui.WorkerWalfareBoardActivity
import com.example.wwbinspectionapp.utils.NetworkResult
import com.example.wwbinspectionapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScholerShipFragment : Fragment(), ApproveRejectListener {

    private val viewModel: AuthViewModel by viewModels()
    @Inject
    lateinit var tokenManager: TokenManager
    private val scholarShipGrantsList = ArrayList<Data>()
    private lateinit var scholarGrantAdapter: ScholarShipAdapter

    private lateinit var binding: FragmentScholerShipBinding

    private var factoryId: Int = -1

    companion object {
        private const val ARG_FACTORY_ID = "factory_id"

        // Create a new instance of the fragment with factoryId as an argument
        fun newInstance(factoryId: Int): ScholerShipFragment {
            val fragment = ScholerShipFragment()
            val args = Bundle()
            args.putInt(ARG_FACTORY_ID, factoryId)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            factoryId = it.getInt(ARG_FACTORY_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScholerShipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Sample data for the adapter

        scholarGrantAdapter = ScholarShipAdapter(scholarShipGrantsList, this)

        // Set up RecyclerView with a LinearLayoutManager
        binding.rvScholarShip.layoutManager = LinearLayoutManager(requireContext())
        binding.rvScholarShip.adapter = scholarGrantAdapter
        binding.rvScholarShip.setHasFixedSize(true)

        getGrantList()
    }

    private fun getGrantList() {
        val barToken = tokenManager.getToken()
        viewModel.grantListResponseLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    binding.progressLayout.root.visibility = View.GONE
                }

                is NetworkResult.Loading -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }

                is NetworkResult.Success -> {
                    binding.progressLayout.root.visibility = View.GONE
                    result.data?.data?.apply {
                        if (this.isNotEmpty()) {
                            scholarShipGrantsList.clear()
                            scholarShipGrantsList.addAll(this)
                            scholarGrantAdapter.notifyDataSetChanged()
                        } else {
                            // Hide RecyclerView and show No Data text
                            Toast.makeText(
                                requireContext(),
                                result.data.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            binding.tvNoDataFound.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        if (barToken != null)
            viewModel.getGrantList(
                barToken,
                GrantType.scholarship_applications.name,
                WorkerWalfareBoardActivity.factoryId ?: -1
            )
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun showRejectDialog(
        position: Int,
        grantId: Int,
        factoryWorkerId: Int
    ) {
        val builder = AlertDialog.Builder(requireContext())
        val input = EditText(requireContext())
        input.hint = "Enter your remarks"

        builder.setTitle("Reject Request")
        builder.setMessage("Please provide remarks for rejection")

        builder.setView(input)
        builder.setCancelable(false)

        builder.setPositiveButton("Reject") { dialog, _ ->
            val remarks = input.text.toString()

            if (remarks.isNotEmpty()) {
                val barToken = tokenManager.getToken()

                if (barToken != null) {
                    // Call the approveGrant function with the reject status
                    viewModel.approveGrant(
                        authToken = barToken,
                        grantType = GrantType.scholarship_applications.name, // Use the correct grant type for scholarships
                        appStatus = "rejected", // Set app status to "rejected"
                        grantId = grantId,
                        factoryWorkerId = factoryWorkerId
                    )
                }

                // Log the action for debugging
                Log.d(
                    "GrantActionsch",
                    "approveGrant called with Token: $barToken, GrantType: ${GrantType.scholarship_applications.name}, " +
                            "AppStatus: rejected, Grant ID: $grantId, Factory Worker ID: $factoryWorkerId"
                )

                // Optionally remove the item from the list
                scholarShipGrantsList.removeAt(position)
                scholarGrantAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Rejected: $remarks", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Remarks are required", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    override fun onApproveClickListener(
        position: Int,
        app_status: String,
        grantId: Int,
        factoryWorkerId: Int
    ) {
        val barToken = tokenManager.getToken()

        if (barToken != null) {
            // Call the approveGrant function with the approve status
            viewModel.approveGrant(
                authToken = barToken,
                grantType = GrantType.scholarship_applications.name, // Use the correct grant type for scholarships
                appStatus = "approved", // Set app status to "approved"
                grantId = grantId,
                factoryWorkerId = factoryWorkerId
            )
        }

        // Log the action for debugging
        Log.d(
            "GrantAction",
            "approveGrant called with Token: $barToken, GrantType: ${GrantType.scholarship_applications.name}, " +
                    "AppStatus: approved, Grant ID: $grantId, Factory Worker ID: $factoryWorkerId"
        )

        // Optionally remove the item from the list
        scholarShipGrantsList.removeAt(position)
        scholarGrantAdapter.notifyDataSetChanged()
        Toast.makeText(requireActivity(), "Approved", Toast.LENGTH_SHORT).show()
    }

    override fun onRejectClickListener(
        position: Int,
        app_status: String,
        grantId: Int,
        factoryWorkerId: Int
    ) {
        showRejectDialog(position, grantId, factoryWorkerId)
    }

}