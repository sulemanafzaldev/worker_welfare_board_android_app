package com.example.wwbinspectionapp.grantList

data class GrantListResponse(
    val `data`: List<Data>,
    val message: String,
    val success: Boolean
)