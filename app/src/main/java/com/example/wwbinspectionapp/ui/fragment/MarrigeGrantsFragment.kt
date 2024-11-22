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
    import com.example.wwbinspectionapp.adapter.MarriageGrantAdapter
    import com.example.wwbinspectionapp.auth.AuthViewModel
    import com.example.wwbinspectionapp.databinding.FragmentMarrigeGrantsBinding
    import com.example.wwbinspectionapp.enums.GrantType
    import com.example.wwbinspectionapp.grantList.Data
    import com.example.wwbinspectionapp.ui.WorkerWalfareBoardActivity
    import com.example.wwbinspectionapp.utils.NetworkResult
    import com.example.wwbinspectionapp.utils.TokenManager
    import dagger.hilt.android.AndroidEntryPoint
    import javax.inject.Inject
    
    @AndroidEntryPoint
    class MarrigeGrantsFragment : Fragment(), ApproveRejectListener {
    
        private var binding: FragmentMarrigeGrantsBinding? = null
        private lateinit var marriageGrantAdapter: MarriageGrantAdapter
        private val viewModel: AuthViewModel by viewModels()
    
        @Inject
        lateinit var tokenManager: TokenManager
    
        private val marriageGrantList = ArrayList<Data>()
        private var factoryId: Int = -1
    
        companion object {
            private const val ARG_FACTORY_ID = "factory_id"
    
        // Create a new instance of the fragment with factoryId as an argument
        fun newInstance(factoryId: Int): MarrigeGrantsFragment {
            val fragment = MarrigeGrantsFragment()
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
    
            binding = FragmentMarrigeGrantsBinding.inflate(layoutInflater, container, false)
    
    
            binding?.apply {
                marriageGrantAdapter =
                    MarriageGrantAdapter(marriageGrantList, this@MarrigeGrantsFragment)
    
                // Set up RecyclerView with a LinearLayoutManager
                rvMarriageGrant.layoutManager = LinearLayoutManager(requireContext())
                rvMarriageGrant.adapter = marriageGrantAdapter
                rvMarriageGrant.setHasFixedSize(true)
    
                getGrantList()
            }
    
            binding!!.swipeRefreshLayout.setOnRefreshListener {
                getGrantList()
            }
    
            return binding?.root
        }
    
    
    /*
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
                            if (this.isNotEmpty()) {
                                marriageGrantList.clear()
                                marriageGrantList.addAll(this)
                                marriageGrantAdapter.notifyDataSetChanged()
                            } else {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(
                                        requireContext(),
                                        result.data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
            if (barToken != null)
                viewModel.getGrantList(
                    barToken,
                    GrantType.marriage_grants.name,
                    WorkerWalfareBoardActivity.factoryId ?: -1
                )
        }
    */

        private fun getGrantList() {
            val barToken = tokenManager.getToken()
            viewModel.grantListResponseLiveData.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is NetworkResult.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                        binding?.progressLayout?.root?.visibility = View.GONE
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        // Hide RecyclerView and show No Data text
                        binding?.rvMarriageGrant?.visibility = View.GONE
                        binding?.tvNoDataFound?.visibility = View.VISIBLE
                    }
                    is NetworkResult.Loading -> {
                        binding?.progressLayout?.root?.visibility = View.VISIBLE
                    }
                    is NetworkResult.Success -> {
                        binding?.progressLayout?.root?.visibility = View.GONE
                        binding?.swipeRefreshLayout?.isRefreshing = false // Stop refresh animation
                        result.data?.data?.apply {
                            if (this.isNotEmpty()) {
                                marriageGrantList.clear()
                                marriageGrantList.addAll(this)
                                marriageGrantAdapter.notifyDataSetChanged()

                                // Show RecyclerView and hide No Data text
                                binding?.rvMarriageGrant?.visibility = View.VISIBLE
                                binding?.tvNoDataFound?.visibility = View.GONE
                            } else {
                                // Hide RecyclerView and show No Data text
                                binding?.rvMarriageGrant?.visibility = View.GONE
                                binding?.tvNoDataFound?.visibility = View.VISIBLE
                                Toast.makeText(
                                    requireContext(),
                                    result.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
            if (barToken != null)
                viewModel.getGrantList(
                    barToken,
                    GrantType.marriage_grants.name,
                    WorkerWalfareBoardActivity.factoryId ?: -1
                )
        }
    
        @SuppressLint("NotifyDataSetChanged")
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
                    grantType = GrantType.marriage_grants.name, // Use the correct grant type for scholarships
                    appStatus = "approved", // Set app status to "approved"
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
            marriageGrantAdapter.notifyDataSetChanged()
            Toast.makeText(requireActivity(), "Approved", Toast.LENGTH_SHORT).show()
        }
    
        @SuppressLint("NotifyDataSetChanged")
        override fun onRejectClickListener(
            position: Int,
            app_status: String,
            grantId: Int,
            factoryWorkerId: Int
        ) {
            showRejectDialog(position, app_status, grantId, factoryWorkerId)
        }
    
        @SuppressLint("NotifyDataSetChanged")
        private fun showRejectDialog(
            position: Int,
            app_status: String,
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
                            grantType = GrantType.marriage_grants.name,
                            appStatus = "rejected", // Set app status to "reject"
                            grantId = grantId,
                            factoryWorkerId = factoryWorkerId
                        )
                    }
                    // Log before calling the approveGrant function
                    Log.d(
                        "GrantActionmarriage",
                        "approveGrant called with Token: $barToken, GrantType: ${GrantType.marriage_grants.name}, " +
                                "AppStatus: reject, Grant ID: $grantId, Factory Worker ID: $factoryWorkerId"
                    )
                    // Optionally remove the item from the list
                    marriageGrantList.removeAt(position)
                    marriageGrantAdapter.notifyDataSetChanged()
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
    
        override fun onResume() {
            super.onResume()
            getGrantList()
        }
    
    }