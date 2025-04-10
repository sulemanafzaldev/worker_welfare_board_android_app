package com.example.wwbinspectionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wwbinspectionapp.OnClick.ApproveRejectListener
import com.example.wwbinspectionapp.databinding.LayoutScholarshipItemBinding
import com.example.wwbinspectionapp.grantList.Data


class ScholarShipAdapter(
    private val scholarShipList: List<Data>,
    private val approveRejectListener: ApproveRejectListener
) : RecyclerView.Adapter<ScholarShipAdapter.HajjGrantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HajjGrantViewHolder {
        val binding = LayoutScholarshipItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HajjGrantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HajjGrantViewHolder, position: Int) {
        holder.bind(scholarShipList[position])
    }

    override fun getItemCount(): Int = scholarShipList.size

    inner class HajjGrantViewHolder(private val binding: LayoutScholarshipItemBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(scholarship: Data) {
            binding.tvStudentNameScholarship.text = scholarship.worker_dependent?.name?:""
            binding.tvFatherNameScholarship.text = scholarship.factory_worker?.workers?.name?:""
            binding.tvInstituteScholarship.text = scholarship.educational_institute?.name?:""
            binding.tvScholarshipCourse.text = scholarship.course?.course?:""
            binding.tvEssiScholarship.text = scholarship.factory_worker?.workers?.essi_no ?: ""
            binding.tvSessionScholarship.text = scholarship.session?:""

            binding.btnApproveScholarship.setOnClickListener {
                approveRejectListener.onApproveClickListener(bindingAdapterPosition,
                    scholarship.app_status?:"",
                    scholarship.grant_id?:-1,
                    scholarship.factory_worker?.factory_worker_id?:-1)
            }
            binding.btnRejectScholarship.setOnClickListener {
                approveRejectListener.onRejectClickListener(bindingAdapterPosition,
                    scholarship.app_status?:"",
                    scholarship.grant_id?:-1,
                    scholarship.factory_worker?.factory_worker_id?:-1)

            }
        }
    }
}
