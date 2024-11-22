package com.example.wwbinspectionapp.OnClick

interface ApproveRejectListener {
    fun onApproveClickListener(position: Int,app_status: String, grantId :Int, factoryWorkerId : Int)
    fun onRejectClickListener(position: Int,app_status: String, grantId :Int, factoryWorkerId : Int)

}