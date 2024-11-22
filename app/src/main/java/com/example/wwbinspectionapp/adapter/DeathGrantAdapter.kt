package com.example.wwbinspectionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wwbinspectionapp.OnClick.ApproveRejectListener
import com.example.wwbinspectionapp.databinding.DeathGrantsItemBinding
import com.example.wwbinspectionapp.grantList.Data
import com.example.wwbinspectionapp.utils.DateTimeFormatter

class DeathGrantAdapter(
    private val deathgrantList: List<Data>,
//    private val onApproveClick: (DeathGrantModel) -> Unit,
//    private val onRejectClick: (DeathGrantModel, String?) -> Unit
    private val approveRejectListener: ApproveRejectListener
) : RecyclerView.Adapter<DeathGrantAdapter.DeathGrantViewHolder>() {

    inner class DeathGrantViewHolder(private val binding: DeathGrantsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isRejectReasonVisible = false

        fun bind(deathgrant: Data) {
            binding.tvNameWwb.text = deathgrant.factory_worker?.workers?.name ?: ""
            binding.tvDependentWwb.text = deathgrant.worker_dependent?.name ?: ""
            binding.tvEssiDeath.text = deathgrant.factory_worker?.workers?.essi_no ?: ""
            binding.tvDateWwb.text = DateTimeFormatter.format(deathgrant.date_of_death ?: "")
            binding.tvContactWwb.text = deathgrant.contact_no ?: ""
            //binding.tvNatureJobWwb.text = deathgrant.nature_of_job?:""

            // Initially hide the EditText for rejection reason
            // binding.etRejectReason.visibility = View.VISIBLE

            // Set click listeners for Approve and Reject buttons
            binding.btnApproveWwb.setOnClickListener {
//                onApproveClick(deathgrant)
                approveRejectListener.onApproveClickListener(
                    bindingAdapterPosition,
                    deathgrant.app_status ?: "",
                    deathgrant.grant_id ?: -1,
                    deathgrant.factory_worker?.factory_worker_id ?: -1
                )
            }

            binding.btnRejetWwb.setOnClickListener {
                // Toggle visibility of the EditText for rejection reason
//                isRejectReasonVisible = !isRejectReasonVisible
//                binding.etRejectReason.visibility = if (isRejectReasonVisible) View.VISIBLE else View.VISIBLE
                approveRejectListener.onRejectClickListener(
                    bindingAdapterPosition,
                    deathgrant.app_status ?: "",
                    deathgrant.grant_id ?: -1,
                    deathgrant.factory_worker?.factory_worker_id ?: -1
                )

            }

            // Optionally, handle rejection reason submission here
//            binding.etRejectReason.setOnFocusChangeListener { _, hasFocus ->
//                if (!hasFocus && isRejectReasonVisible) {
//                    val rejectReason = binding.etRejectReason.text.toString()
//                    if (rejectReason.isNotEmpty()) {
//                        onRejectClick(deathgrant, rejectReason)
//                        onRejectClick.onRejectClickListener(position)
//                    }

//                }
//            }
        }

        /*    // Provide a method to get the rejection reason from the EditText
        fun getRejectReason(): String {
            return binding.etRejectReason.text.toString().trim()
        }
    }
*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeathGrantViewHolder {
        val binding =
            DeathGrantsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeathGrantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeathGrantViewHolder, position: Int) {
        holder.bind(deathgrantList[position])
    }

    override fun getItemCount(): Int = deathgrantList.size

}