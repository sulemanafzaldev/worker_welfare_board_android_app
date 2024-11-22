package com.example.wwbinspectionapp.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wwbinspectionapp.OnClick.ApproveRejectListener
import com.example.wwbinspectionapp.databinding.MarriageGrantItemBinding
import com.example.wwbinspectionapp.grantList.Data
import com.example.wwbinspectionapp.utils.DateTimeFormatter


class MarriageGrantAdapter(
    private val items: List<Data>,
//    private val onApproveClick: (List<MarriageGrantModel>) -> Unit,
//    private val onRejectClick: (List<MarriageGrantModel>) -> Unit
    private val approveRejectListener: ApproveRejectListener
) : RecyclerView.Adapter<MarriageGrantAdapter.MarriageGrantViewHolder>() {

    inner class MarriageGrantViewHolder(private val binding: MarriageGrantItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(marriageGrant: Data) {

            binding.tvNameMarriage.text = marriageGrant.factory_worker?.workers?.name
            binding.tvDependentMarriage.text = marriageGrant.worker_dependent?.name
            binding.tvDateMarriage.text = DateTimeFormatter.format(marriageGrant.marriage_date ?: "")
            binding.tvContactMarriage.text = marriageGrant.contact_no
            binding.tvEssiMarriage.text = marriageGrant.factory_worker?.workers?.essi_no

            // Handle Approve button click
            binding.btnApproveMarriage.setOnClickListener {
                //onApproveClick(marriageGrant)
                Log.d(
                    "ApproveClick", "Approve button clicked: Position = $bindingAdapterPosition," +
                            " Grant ID = ${marriageGrant.grant_id ?: -1}, Factory Worker ID = ${marriageGrant.factory_worker_id ?: -1}"
                )
                approveRejectListener.onApproveClickListener(
                    bindingAdapterPosition,
                    marriageGrant.app_status ?: "",
                    marriageGrant.grant_id ?: -1,
                    marriageGrant.factory_worker?.factory_worker_id ?: -1
                )
            }

            // Handle Reject button click
            binding.btnRejectMarriage.setOnClickListener {
//                onRejectClick(marriageGrant)
                Log.d(
                    "ApproveClick", "Reject button clicked: Position = $bindingAdapterPosition," +
                            " Grant ID = ${marriageGrant.grant_id}, " +
                            "Factory Worker ID = ${marriageGrant.factory_worker?.factory_worker_id}"
                )

                approveRejectListener.onRejectClickListener(
                    bindingAdapterPosition,
                    marriageGrant.app_status ?: "",
                    marriageGrant.grant_id ?: -1,
                    marriageGrant.factory_worker?.factory_worker_id ?: -1
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarriageGrantViewHolder {
        val binding = MarriageGrantItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MarriageGrantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarriageGrantViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}