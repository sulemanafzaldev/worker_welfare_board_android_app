package com.example.wwbinspectionapp.model.factoryList

data class FactoryListResponce(
    val `data`: List<Data>,
    val message: String,
    val success: Boolean
)