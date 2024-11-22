package com.example.wwbinspectionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wwbinspectionapp.OnClick.ApproveRejectListener
import com.example.wwbinspectionapp.databinding.HajjGrantItemBinding
import com.example.wwbinspectionapp.grantList.Data

class HajjAdapter(
    val hajj_grant_list: List<Data>,
    private val approveRejectListener: ApproveRejectListener
) : RecyclerView.Adapter<HajjAdapter.HajjGrantViewHolder>() {

    inner class HajjGrantViewHolder(private val binding: HajjGrantItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(hajjGrant: Data) {
            binding.tvNameHajj.text = hajjGrant.factory_worker?.workers?.name ?: ""
            binding.tvYearHajj.text = hajjGrant.year
            binding.tvEssiHajj.text = hajjGrant.factory_worker?.workers?.essi_no
            binding.tvContactHajj.text = hajjGrant.contact_no ?: ""

            binding.btnApproveHajj.setOnClickListener {
//                val hajjGrant = hajjGrants[bindingAdapterPosition]
//                onApproveClick(hajjGrant)
                approveRejectListener.onApproveClickListener(
                    bindingAdapterPosition,
                    hajjGrant.app_status ?: "",
                    hajjGrant.grant_id ?: 1,
                    hajjGrant.factory_worker?.factory_worker_id ?: 1
                )
            }
            binding.btnRejectHajj.setOnClickListener {
//                val hajjGrant = hajjGrants[adapterPosition]
//                onRejectClick(hajjGrant)
                approveRejectListener.onRejectClickListener(
                    bindingAdapterPosition,
                    hajjGrant.app_status ?: "",
                    hajjGrant.grant_id ?: 1,
                    hajjGrant.factory_worker?.factory_worker_id ?: 1
                )

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HajjGrantViewHolder {
        val binding =
            HajjGrantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HajjGrantViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return hajj_grant_list.size
    }

    override fun onBindViewHolder(holder: HajjGrantViewHolder, position: Int) {
        holder.bind(hajj_grant_list[position])
    }
}