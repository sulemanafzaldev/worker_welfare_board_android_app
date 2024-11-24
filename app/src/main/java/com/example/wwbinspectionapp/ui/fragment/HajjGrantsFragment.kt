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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wwbinspectionapp.OnClick.ApproveRejectListener
import com.example.wwbinspectionapp.R
import com.example.wwbinspectionapp.adapter.HajjAdapter
import com.example.wwbinspectionapp.auth.AuthViewModel
import com.example.wwbinspectionapp.databinding.FragmentHajjGrantsBinding
import com.example.wwbinspectionapp.enums.GrantType
import com.example.wwbinspectionapp.grantList.Data
import com.example.wwbinspectionapp.ui.WorkerWalfareBoardActivity
import com.example.wwbinspectionapp.utils.NetworkResult
import com.example.wwbinspectionapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HajjGrantsFragment : Fragment(), ApproveRejectListener {

    private var binding: FragmentHajjGrantsBinding? = null
    private lateinit var hajjAdapter :HajjAdapter
    private val hajgrantList = ArrayList<Data>()

    @Inject
    lateinit var tokenManager: TokenManager
    private val viewModel:AuthViewModel by viewModels()
    private var factoryId: Int = -1

    companion object {
        private const val ARG_FACTORY_ID = "factory_id"

        // Create a new instance of the fragment with factoryId as an argument
        fun newInstance(factoryId: Int): HajjGrantsFragment {
            val fragment = HajjGrantsFragment()
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
        binding = FragmentHajjGrantsBinding.inflate(inflater, container, false)

                getGrantList()

                return binding?.root
            }

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                setupRecyclerView()
            }

            private fun setupRecyclerView() {

                hajjAdapter = HajjAdapter(hajgrantList , this)
                binding?.rvHajjGrantList?.layoutManager = LinearLayoutManager(requireContext())
                binding?.rvHajjGrantList?.adapter = hajjAdapter
                binding?.rvHajjGrantList?.setHasFixedSize(true)
            }

    private fun getGrantList() {
        val barToken = tokenManager.getToken()
        viewModel.grantListResponseLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    binding?.progressLayout?.root?.visibility = View.GONE
                }
                is NetworkResult.Loading -> {
                    binding?.progressLayout?.root?.visibility = View.VISIBLE
                }
                is NetworkResult.Success -> {
                    binding?.progressLayout?.root?.visibility = View.GONE
                    result.data?.data?.apply {
                        if (this.isNotEmpty()){
                            hajgrantList.clear()
                            hajgrantList.addAll(this)
                            hajjAdapter.notifyDataSetChanged()
                        }
                        else{
                            // Hide RecyclerView and show No Data text
                            Toast.makeText(
                                requireContext(),
                                result.data.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            binding?.tvNoDataFound?.visibility = View.VISIBLE

                        }
                    }
                }
            }
        }
        if (barToken != null)
            viewModel.getGrantList(barToken,
                GrantType.hajj_grants.name,
                WorkerWalfareBoardActivity.factoryId?: -1)
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

                // Call the approveGrant function with the reject status
                if (barToken != null) {
                    viewModel.approveGrant(
                        authToken = barToken,
                        grantType = GrantType.hajj_grants.name, // Use the correct grant type
                        appStatus = "rejected", // Set app status to "reject"
                        grantId = grantId,
                        factoryWorkerId = factoryWorkerId
                    )
                }

                // Log before calling the approveGrant function
                Log.d(
                    "GrantActions",
                    "approveGrant called with Token: $barToken, GrantType: ${GrantType.hajj_grants.name}, " +
                            "AppStatus: rejected, Grant ID: $grantId, Factory Worker ID: $factoryWorkerId"
                )

                // Optionally remove the item from the list
                hajgrantList.removeAt(position)
                hajjAdapter.notifyDataSetChanged()
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
                grantType = GrantType.hajj_grants.name, // Use the correct grant type for Hajj grants
                appStatus = "approved", // Set app status to "approved"
                grantId = grantId,
                factoryWorkerId = factoryWorkerId
            )
        }

        // Log the action for debugging
        Log.d(
            "GrantActionhajj",
            "approveGrant called with Token: $barToken, GrantType: ${GrantType.hajj_grants.name}, " +
                    "AppStatus: approved, Grant ID: $grantId, Factory Worker ID: $factoryWorkerId"
        )

        // Optionally remove the item from the list
        hajgrantList.removeAt(position)
        hajjAdapter.notifyDataSetChanged()
        Toast.makeText(requireActivity(), "Approved", Toast.LENGTH_SHORT).show()
    }


    override fun onRejectClickListener(
        position: Int,
        app_status: String,
        grantId: Int,
        factoryWorkerId: Int
    ) {
        Log.d("GrantAction", "User clicked Reject. Status: $app_status, Grant ID: $grantId, Worker ID: $factoryWorkerId")
        showRejectDialog(position,grantId,factoryWorkerId)
    }
}